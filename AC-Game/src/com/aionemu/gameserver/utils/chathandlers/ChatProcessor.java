/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 * Aion-Lightning is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Aion-Lightning is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. *
 *
 * You should have received a copy of the GNU General Public License along with Aion-Lightning. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * Credits goes to all Open Source Core Developer Groups listed below Please do not change here something, ragarding the developer credits, except the
 * "developed by XXXX". Even if you edit a lot of files in this source, you still have no rights to call it as "your Core". Everybody knows that this
 * Emulator Core was developed by Aion Lightning
 * 
 * @-Aion-Unique-
 * @-Aion-Lightning
 * @Aion-Engine
 * @Aion-Extreme
 * @Aion-NextGen
 * @Aion-Core Dev.
 */
package com.aionemu.gameserver.utils.chathandlers;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.scripting.classlistener.AggregatedClassListener;
import com.aionemu.commons.scripting.classlistener.OnClassLoadUnloadListener;
import com.aionemu.commons.scripting.classlistener.ScheduledTaskClassListener;
import com.aionemu.commons.scripting.scriptmanager.ScriptManager;
import com.aionemu.commons.utils.PropertiesUtils;
import com.aionemu.gameserver.GameServerError;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.GameEngine;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import javolution.util.FastMap;

/**
 * @author KID
 * @Modified Rolandas
 * @modified Yon (Aion Reconstruction Project) -- reworked {@link #handleChatCommand(Player, String)} to consume invalid command
 * input, warning the user.
 */
public class ChatProcessor implements GameEngine {
	
	private static final Logger log = LoggerFactory.getLogger("ADMINAUDIT_LOG");
	private static ChatProcessor instance = new ChatProcessor();
	private Map<String, ChatCommand> commands = new FastMap<String, ChatCommand>();
	private Map<String, Byte> accessLevel = new FastMap<String, Byte>();
	private ScriptManager sm = new ScriptManager();
	private Exception loadException = null;
	
	public static ChatProcessor getInstance() {
		return instance;
	}
	
	@Override
	public void load(CountDownLatch progressLatch) {
		try {
			log.info("Chat processor load started");
			init(sm, this);
		} finally {
			if (progressLatch != null) {
				progressLatch.countDown();
			}
		}
	}
	
	@Override
	public void shutdown() {}
	
	private ChatProcessor() {}
	
	private ChatProcessor(ScriptManager scriptManager) {
		init(scriptManager, this);
	}
	
	private void init(final ScriptManager scriptManager, ChatProcessor processor) {
		loadLevels();
		
		AggregatedClassListener acl = new AggregatedClassListener();
		acl.addClassListener(new OnClassLoadUnloadListener());
		acl.addClassListener(new ScheduledTaskClassListener());
		acl.addClassListener(new ChatCommandsLoader(processor));
		scriptManager.setGlobalClassListener(acl);
		
		final File[] files = new File[] {	new File("./data/scripts/system/adminhandlers.xml"), new File("./data/scripts/system/playerhandlers.xml"),
											new File("./data/scripts/system/weddinghandlers.xml")};
		final CountDownLatch loadLatch = new CountDownLatch(files.length);
		
		for (int i = 0; i < files.length; i++) {
			final int index = i;
			ThreadPoolManager.getInstance().execute(new Runnable() {
				
				@Override
				public void run() {
					try {
						scriptManager.load(files[index]);
					} catch (Exception e) {
						loadException = e;
					} finally {
						loadLatch.countDown();
					}
				}
			});
		}
		
		try {
			loadLatch.await();
		} catch (InterruptedException e1) {}
		if (loadException != null) {
			throw new GameServerError("Can't initialize chat handlers.", loadException);
		}
	}
	
	public void registerCommand(ChatCommand cmd) {
		if (commands.containsKey(cmd.getAlias())) {
			log.warn("Command " + cmd.getAlias() + " is already registered. Fail");
			return;
		}
		
		if (!accessLevel.containsKey(cmd.getAlias())) {
			log.warn("Command " + cmd.getAlias() + " do not have access level. Fail");
			return;
		}
		
		cmd.setAccessLevel(accessLevel.get(cmd.getAlias()));
		commands.put(cmd.getAlias(), cmd);
	}
	
	public void reload() {
		ScriptManager tmpSM;
		final ChatProcessor adminCP;
		Map<String, ChatCommand> backupCommands = new FastMap<String, ChatCommand>(commands);
		commands.clear();
		loadException = null;
		
		try {
			tmpSM = new ScriptManager();
			adminCP = new ChatProcessor(tmpSM);
		} catch (Throwable e) {
			commands = backupCommands;
			throw new GameServerError("Can't reload chat handlers.", e);
		}
		
		if (tmpSM != null && adminCP != null) {
			backupCommands.clear();
			sm.shutdown();
			sm = null;
			sm = tmpSM;
			instance = adminCP;
		}
	}
	
	private void loadLevels() {
		accessLevel.clear();
		try {
			java.util.Properties props = PropertiesUtils.load("config/administration/commands.properties");
			
			for (Object key : props.keySet()) {
				String str = (String) key;
				accessLevel.put(str, Byte.valueOf(props.getProperty(str).trim()));
			}
		} catch (IOException e) {
			log.error("Can't read commands.properties", e);
		}
	}
	
	public boolean handleChatCommand(Player player, String text) {
		if (text.split(" ").length == 0) {
			return false;
		}
		
		boolean adminCmd = false, weddingCmd = false;
		if ((adminCmd = text.startsWith("//")) || (weddingCmd = text.startsWith("..")) || (text.startsWith("."))) {
			String cmd;
			ChatCommand chatCmd; //instanceof will return false on null values.
			if (adminCmd | weddingCmd) {
				cmd = text.substring(2);
				chatCmd = getCommand(cmd);
				if ((adminCmd && chatCmd instanceof AdminCommand) || (weddingCmd && chatCmd instanceof WeddingCommand)) {
					return chatCmd.process(player, cmd);
				} 
				if (adminCmd & player.getAccessLevel() > 0) {
					//Send message about invalid admin command.
					PacketSendUtility.sendMessage(player, "Unrecognized admin command: " + text);
					return true; //Consume invalid input for admins; don't reveal admin commands to normal players.
				}
				if (weddingCmd && !cmd.startsWith(".") && !cmd.isEmpty()) {
					//Send message about invalid wedding command.
					PacketSendUtility.sendMessage(player, "Unrecognized wedding command: " + text);
					return true; //Consume invalid input; it can catch false positives, but it should be rare.
				}
			} else {
				//Not admin or wedding command, must be player command, or configurable admin dot command!
				cmd = text.substring(1);
				chatCmd = getCommand(cmd);
				if (chatCmd instanceof PlayerCommand || (CustomConfig.ENABLE_ADMIN_DOT_COMMANDS && chatCmd instanceof AdminCommand)) {
					return chatCmd.process(player, cmd);
				} else if (!cmd.isEmpty()) {
					//Send message about invalid command.
					PacketSendUtility.sendMessage(player, "Unrecognized command: " + text);
					return true; //Consume invalid input; it can catch false positives, but it should be rare.
				}
			}
		}
		return false;
		//Old code left as a nod to former devs -- it's ghost code; delete it if this is moved to git.
//		if ((text.startsWith("//") && getCommand(text.substring(2)) instanceof AdminCommand) || (text.startsWith("..") && getCommand(text.substring(2)) instanceof WeddingCommand)) {
//			return (getCommand(text.substring(2))).process(player, text.substring(2));
//		} else if (text.startsWith(".")
//				&& (getCommand(text.substring(1)) instanceof PlayerCommand || (CustomConfig.ENABLE_ADMIN_DOT_COMMANDS && getCommand(text.substring(1)) instanceof AdminCommand))) {
//			return (getCommand(text.substring(1))).process(player, text.substring(1));
//		} else {
//			return false;
//		}
	}
	
	private ChatCommand getCommand(String text) {
		String alias = text.split(" ")[0];
		ChatCommand cmd = this.commands.get(alias);
		return cmd;
	}
	
	public void onCompileDone() {
		log.info("Loaded " + commands.size() + " commands.");
	}
}

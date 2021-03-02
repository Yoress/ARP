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
package admincommands;

import java.util.Collection;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapType;

/**
 * @author Antraxx
 */
public class PlayerList extends AdminCommand {
	
	public PlayerList() {
		super("playerlist");
	}
	
	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(admin, "syntax //playerlist <all|ely|asmo|premium|vip>");
			return;
		}
		
		// get all currently connected players
		Collection<Player> players = World.getInstance().getAllPlayers();
		PacketSendUtility.sendMessage(admin, "Currently connected players:");
		int count = 0;
		for (Player p : players) {
			if (params != null && params.length > 0) {
				String cmd = params[0].toLowerCase().trim();
				if (("ely").startsWith(cmd)) {
					if (p.getCommonData().getRace() == Race.ASMODIANS) {
						continue;
					}
				}
				if (("asmo").startsWith(cmd)) {
					if (p.getCommonData().getRace() == Race.ELYOS) {
						continue;
					}
				}
				if (("premium").startsWith(cmd)) {
					if (p.getPlayerAccount().getMembership() == 2) {
						continue;
					}
				}
				if (("vip").startsWith(cmd)) {
					if (p.getPlayerAccount().getMembership() == 1) {
						continue;
					}
				}
			}
			count++;
			StringBuilder str = new StringBuilder();
			str.append("Char: ");
			str.append(p.getName());
			str.append(" (");
			str.append(p.getAcountName());
			str.append(")  - ");
			str.append(p.getCommonData().getRace());
			str.append("/Lv. ");
			str.append(p.getLevel());
			str.append(" ");
			str.append(p.getCommonData().getPlayerClass());
			str.append(" - Location: ");
			str.append(WorldMapType.getWorld(p.getWorldId()));
			
			PacketSendUtility.sendMessage(admin, str.toString());
//			PacketSendUtility.sendMessage(player, "Char: " + p.getName() + " (" + p.getAcountName() + ") " + " - " + p.getCommonData().getRace().name() + "/"
//					+ p.getCommonData().getPlayerClass().name() + " - Location: " + WorldMapType.getWorld(p.getWorldId()).name());
		}
		PacketSendUtility.sendMessage(admin, "Currently, there are " + players.size() + " connected players; " + count + " are displayed.");
	}
	
	@Override
	public void onFail(Player admin, String message) {
		PacketSendUtility.sendMessage(admin, "syntax //playerlist <all|ely|asmo|premium|vip>");
	}
}

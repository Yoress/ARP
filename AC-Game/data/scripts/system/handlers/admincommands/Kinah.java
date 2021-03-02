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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * @author Sarynth Simple admin assistance command for adding kinah to self, named player or target player. Based on //add command. Kinah Item Id -
 * 182400001 (Using ItemId.KINAH.value())
 */
public class Kinah extends AdminCommand {
	
	public Kinah() {
		super("kinah");
	}
	
	@Override
	public void execute(Player admin, String... params) {
		long kinahCount;
		Player receiver;
		
		if (params.length == 1) {
			receiver = admin;
			try {
				kinahCount = Long.parseLong(params[0]);
			} catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "Kinah value must be an integer.");
				return;
			}
		} else {
			receiver = World.getInstance().findPlayer(Util.convertName(params[0]));
			
			if (receiver == null) {
				PacketSendUtility.sendMessage(admin, "Could not find a player by that name.");
				return;
			}
			
			try {
				kinahCount = Long.parseLong(params[1]);
			} catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "Kinah value must be an integer.");
				return;
			}
		}
		
		long count = ItemService.addItem(receiver, ItemId.KINAH.value(), kinahCount);
		
		if (count == 0) {
			PacketSendUtility.sendMessage(admin, "Kinah given successfully.");
			PacketSendUtility.sendMessage(receiver, "An admin gives you some kinah.");
		} else {
			PacketSendUtility.sendMessage(admin, "Kinah couldn't be given.");
		}
	}
	
	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //kinah [player] <quantity>");
	}
}

/**
 * This file is part of the Aion Reconstruction Project Server.
 *
 * The Aion Reconstruction Project Server is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * The Aion Reconstruction Project Server is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with the Aion Reconstruction Project Server. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * @AionReconstructionProjectTeam
 */
package admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;


/**
 * Used to force teleport a player or a player and their entire group.
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class ForcedMove extends AdminCommand {
	
	public ForcedMove() {
		super("forcemove");
	}
	
	final static String HELP_MESSAGE = "Syntax: //forcemove -g <characterName>\nSwitches:\n\t-g:  move the given player, along with his entire group.";
	
	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 1) {
			onFail(admin, HELP_MESSAGE);
			return;
		}
		
		if (params.length == 1) {
			Player playerToMove = World.getInstance().findPlayer(Util.convertName(params[0]));
			if (playerToMove == null) {
				onFail(admin, "The specified player is not online or does not exist.");
				return;
			}
			
			if (playerToMove == admin) {
				onFail(admin, "Cannot use this command on yourself.");
				return;
			}
			
			TeleportService2.teleportTo(playerToMove, admin.getWorldId(), admin.getInstanceId(), admin.getX(), admin.getY(), admin.getZ(), admin.getHeading());
			PacketSendUtility.sendMessage(admin, "Forcefully teleported player " + playerToMove.getName() + " to your location.");
			PacketSendUtility.sendMessage(playerToMove, "You have been forcefully teleported by " + admin.getName() + ".");
		} else if (params[0].equalsIgnoreCase("-g") && params.length > 1) {
			Player groupToMove = World.getInstance().findPlayer(Util.convertName(params[1]));
			if (groupToMove == null) {
				PacketSendUtility.sendMessage(admin, "The specified player is not online or does not exist.");
				return;
			}
			
			if (!groupToMove.isInGroup2()) {
				PacketSendUtility.sendMessage(admin, groupToMove.getName() + " is not in group.");
				TeleportService2.teleportTo(groupToMove, admin.getWorldId(), admin.getInstanceId(), admin.getX(), admin.getY(), admin.getZ(), admin.getHeading());
				PacketSendUtility.sendMessage(admin, "Forcefully teleported player " + groupToMove.getName() + " to your location.");
				PacketSendUtility.sendMessage(groupToMove, "You have been forcefully teleported by " + admin.getName() + ".");
				return;
			}
			
			for (Player target : groupToMove.getPlayerGroup2().getMembers()) {
				if (target != admin) {
					TeleportService2.teleportTo(target, admin.getWorldId(), admin.getInstanceId(), admin.getX(), admin.getY(), admin.getZ(), admin.getHeading());
					PacketSendUtility.sendMessage(target, "You have been forcefully teleported by " + admin.getName() + ".");
					PacketSendUtility.sendMessage(admin, "Forcefully teleported player " + target.getName() + " to your location.");
				}
			}
		}
	}
	
}

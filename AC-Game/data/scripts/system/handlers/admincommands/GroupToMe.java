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
import com.aionemu.gameserver.network.aion.clientpackets.CM_QUESTION_ACCEPT_SUMMON_RESPONSE.TeleportDestination;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW_ACCEPT_SUMMON;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * @author Source
 * @modified Yon (Aion Reconstruction Project) -- Now prompts the user to accept a summoning instead of forcing them to teleport.
 */
public class GroupToMe extends AdminCommand {
	
	public GroupToMe() {
		super("grouptome");
	}
	
	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 1) {
			onFail(admin, null);
			return;
		}
		
		Player groupToMove = World.getInstance().findPlayer(Util.convertName(params[0]));
		if (groupToMove == null) {
			PacketSendUtility.sendMessage(admin, "The player is not online.");
			return;
		}
		
		if (!groupToMove.isInGroup2()) {
			PacketSendUtility.sendMessage(admin, groupToMove.getName() + " is not in group.");
			return;
		}
		
		int timeout = 30;
		if (params.length > 1) {
			try {
				timeout = Integer.parseInt(params[1]);
			} catch (NumberFormatException e) {
				timeout = 30;
			}
		}
		
		for (Player target : groupToMove.getPlayerGroup2().getMembers()) {
			if (target != admin) {
//				TeleportService2.teleportTo(target, admin.getWorldId(), admin.getInstanceId(), admin.getX(), admin.getY(), admin.getZ(), admin.getHeading());
//				PacketSendUtility.sendMessage(target, "You have been summoned by " + admin.getName() + ".");
//				PacketSendUtility.sendMessage(admin, "You summon " + target.getName() + ".");
				summonPlayer(admin, target, timeout);
			}
		}
	}
	
	/**
	 * Prompts the summonTarget to accept a summon to the location of the admin within timeout seconds.
	 * 
	 * @param admin -- an admin summoning the summonTarget
	 * @param summonTarget -- the player being summoned by the admin
	 * @param timeout -- the amount of time for the summonTarget to accept the summon
	 * @author Yon (Aion Reconstruction Project) 
	 */
	private static void summonPlayer(Player admin, Player summonTarget, int timeout) {
		if (timeout < 30) timeout = 30;
		if (timeout > 300) timeout = 300;
		int worldId = admin.getWorldId();
		int instanceId = admin.getInstanceId();
		float x = admin.getX();
		float y = admin.getY();
		float z = admin.getZ();
		byte h = admin.getHeading();
		//SkillId 20657 is "Summoning Ritual"
		TeleportDestination dest = TeleportDestination.addTeleportRequestFor(summonTarget, admin, 20657, worldId, instanceId, x, y, z, h, timeout);
		if (dest == null) {
			PacketSendUtility.sendPacket(admin, SM_SYSTEM_MESSAGE.STR_MSG_Recall_CANNOT_ACCEPT_EFFECT(summonTarget.getName()));
			return;
		}
		PacketSendUtility.sendPacket(summonTarget, new SM_QUESTION_WINDOW_ACCEPT_SUMMON(dest));
		PacketSendUtility.sendMessage(admin, "You have sent a summoning request to " + summonTarget.getName() + ".");
	}
	
	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //grouptome <player> [timeout]");
	}
}

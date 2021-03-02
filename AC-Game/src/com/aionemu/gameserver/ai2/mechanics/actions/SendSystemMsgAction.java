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
package com.aionemu.gameserver.ai2.mechanics.actions;

import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class SendSystemMsgAction extends Action {
	
	public final int stringId;
	
	public SendSystemMsgAction(int stringId) {
		super(ActionType.send_system_msg);
		this.stringId = stringId;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		int Unused_So_I_Know_This_Class_Is_Not_In_An_Acceptable_State = 0;
		final int worldId = ai.getOwner().getWorldId();
		final int worldInstanceId = ai.getOwner().getInstanceId();
		//TODO: Is this supposed to hit everyone on the map??
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline() && player.getWorldId() == worldId && player.getInstanceId() == worldInstanceId) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(stringId));
				}
			}
		});
	}
	
	@Override
	public int hashCode() {
		return 3*stringId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SendSystemMsgAction) {
			return ((SendSystemMsgAction) obj).stringId == stringId;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + stringId + "]";
	}
	
}

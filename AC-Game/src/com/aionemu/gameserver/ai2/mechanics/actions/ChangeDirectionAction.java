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
import com.aionemu.gameserver.network.aion.serverpackets.SM_HEADING_UPDATE;
import com.aionemu.gameserver.utils.PacketSendUtility;


/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class ChangeDirectionAction extends Action {
	
	final public int direction;
	
	public ChangeDirectionAction(int direction) {
		super(ActionType.change_direction);
		this.direction = direction;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		/*
		 * This can probably look weird if the NPC is moving when this happens... but they shouldn't be moving if this is used!
		 */
		ai.getOwner().setXYZH(null, null, null, (byte) (direction/3));
		PacketSendUtility.broadcastPacket(ai.getOwner(), new SM_HEADING_UPDATE(ai.getObjectId(), ai.getOwner().getHeading()));
	}
	
	@Override
	public int hashCode() {
		return 3*direction;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ChangeDirectionAction) {
			ChangeDirectionAction o = (ChangeDirectionAction) obj;
			return (o.direction == direction);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + direction + "]";
	}
	
}

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
package com.aionemu.gameserver.ai2.mechanics.conditions;

import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class IsWaypointIndexCondition extends Condition {
	
	final public int index;
	
	public IsWaypointIndexCondition(int index) {
		super(ConditionType.is_waypoint_index);
		this.index = index;
	}
	
	@Override
	public boolean check(MechanicEvent event, AbstractMechanicsAI2 ai) {
		return ai.getOwner().getMoveController().getCurrentPoint() == index;
	}
	
	@Override
	public int hashCode() {
		return 3*index;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IsWaypointIndexCondition) {
			IsWaypointIndexCondition o = (IsWaypointIndexCondition) obj;
			return (o.index == index);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + index + "]";
	}
	
}

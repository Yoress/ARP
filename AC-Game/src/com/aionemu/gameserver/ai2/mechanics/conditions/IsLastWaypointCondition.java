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
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.walker.WalkerTemplate;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class IsLastWaypointCondition extends Condition {
	
	public IsLastWaypointCondition() {
		super(ConditionType.is_last_waypoint);
	}
	
	@Override
	public boolean check(MechanicEvent event, AbstractMechanicsAI2 ai) {
		int routeStep = ai.getOwner().getMoveController().getCurrentPoint() + 1;
		WalkerTemplate template = DataManager.WALKER_DATA.getWalkerTemplate(ai.getOwner().getSpawn().getWalkerId());
		if (template != null) {
			return template.getRouteSteps().size() == routeStep;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		//All of these objects are the same.
		return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IsLastWaypointCondition) {
			//All of these objects are the same.
			return true;
		}
		return false;
	}
	
}

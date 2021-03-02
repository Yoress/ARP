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
import com.aionemu.gameserver.ai2.mechanics.context.MoveType;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
//import com.aionemu.gameserver.controllers.movement.NpcMoveController;
//import com.aionemu.gameserver.dataholders.DataManager;
//import com.aionemu.gameserver.model.gameobjects.Npc;
//import com.aionemu.gameserver.model.templates.walker.WalkerTemplate;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class GotoWaypointAction extends Action {
	
	public final int waypointId;
	
	public final MoveType moveType;
	
	public GotoWaypointAction(int waypointId, MoveType moveType) {
		super(ActionType.goto_waypoint);
		this.waypointId = waypointId;
		this.moveType = moveType;
	}
	
	/**
	 * This method -- while currently implemented -- is NOT supported by the walker system.
	 * <p>
	 * This Action should never be performed until the mechanic data has been adjusted to
	 * match the current Walker system.
	 */
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		//TODO: Uncomment when ready
//		Npc owner = ai.getOwner();
//		NpcMoveController mover = owner.getMoveController();
//		WalkerTemplate walkerTemplate = DataManager.WALKER_DATA.getWalkerTemplate(owner.getSpawn().getWalkerId());
//		if (walkerTemplate == null) return;
//		mover.setRouteStep(walkerTemplate.getRouteStep(waypointId + 1), walkerTemplate.getRouteStep(mover.getCurrentPoint()));
	}
	
	@Override
	public int hashCode() {
		return 3*waypointId + 5*moveType.ordinal();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GotoWaypointAction) {
			return (((GotoWaypointAction) obj).waypointId == waypointId && ((GotoWaypointAction) obj).moveType == moveType);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + moveType + "] --> " + waypointId;
	}
	
}

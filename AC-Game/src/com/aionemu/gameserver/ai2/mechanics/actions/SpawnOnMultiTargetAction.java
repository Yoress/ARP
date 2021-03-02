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
import com.aionemu.gameserver.ai2.mechanics.context.OrderIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.SpawnId;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;


/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class SpawnOnMultiTargetAction extends Action {
	
	final public SpawnId spawnId;
	
	final public int npcNameid;
	
	final public int numToSpawn;
	
	final public int spawnRange;
	
	final public int liveTime;
	
	final public boolean despawnAtAttackState;
	
	final public OrderIndicator orderInAttackerList;
	
	final public int totalSetToSpawn;
	
	final public int validDistance;
	
	final public boolean attackTargetAfterSpawn;
	
	final public int hatepointsToAdd;
	
	public SpawnOnMultiTargetAction(SpawnId spawnId, int npcNameid, int numToSpawn, int spawnRange, int liveTime, boolean despawnAtAttackState, OrderIndicator orderInAttackerList, int totalSetToSpawn, int validDistance, boolean attackTargetAfterSpawn, int hatepointsToAdd) {
		super(ActionType.spawn_on_multi_target);
		this.spawnId = spawnId;
		this.npcNameid = npcNameid;
		this.numToSpawn = numToSpawn;
		this.spawnRange = spawnRange;
		this.liveTime = liveTime;
		this.despawnAtAttackState = despawnAtAttackState;
		this.orderInAttackerList = orderInAttackerList;
		this.totalSetToSpawn = totalSetToSpawn;
		this.validDistance = validDistance;
		this.attackTargetAfterSpawn = attackTargetAfterSpawn;
		this.hatepointsToAdd = hatepointsToAdd;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		ai.createSpawnGroup(spawnId, this);
	}
	
	@Override
	public int hashCode() {
		return 3*spawnId.ordinal()
			 + 5*npcNameid
			 + 7*numToSpawn
			 + 11*spawnRange
			 + 13*liveTime
			 + (despawnAtAttackState ? 17 : 0)
			 + 19*orderInAttackerList.ordinal()
			 + 23*totalSetToSpawn
			 + 29*validDistance
			 + (attackTargetAfterSpawn ? 31 : 0)
			 + 37*hatepointsToAdd;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SpawnOnMultiTargetAction) {
			SpawnOnMultiTargetAction o = (SpawnOnMultiTargetAction) obj;
			return (o.spawnId == spawnId
					&& o.npcNameid == npcNameid
					&& o.numToSpawn == numToSpawn
					&& o.spawnRange == spawnRange
					&& o.liveTime == liveTime
					&& o.despawnAtAttackState == despawnAtAttackState
					&& o.orderInAttackerList == orderInAttackerList
					&& o.totalSetToSpawn == totalSetToSpawn
					&& o.validDistance == validDistance
					&& o.attackTargetAfterSpawn == attackTargetAfterSpawn
					&& o.hatepointsToAdd == hatepointsToAdd);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + spawnId + "]: [" + npcNameid + "]x" + numToSpawn + " --> "
				+ liveTime + "ms [" + totalSetToSpawn + ", " + orderInAttackerList + "] @[" + spawnRange + ", " + validDistance
				+ "] <" + despawnAtAttackState + ", " + attackTargetAfterSpawn + "> " + hatepointsToAdd;
	}
	
}

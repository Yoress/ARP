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
import com.aionemu.gameserver.ai2.mechanics.context.ObjIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.SpawnId;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class SpawnOnTargetAction extends Action {
	
	public final ObjIndicator targetObj;
	
	public final SpawnId spawnId;
	
	public final int npcId;
	
	public final int numToSpawn;
	
	public final int spawnRange;
	
	public final int liveTime;
	
	public final boolean despawnAtAttackState;
	
	public final int validDistance;
	
	public final boolean attackTargetAfterSpawn;
	
	public final int hatepointsToAdd;

	public SpawnOnTargetAction(ObjIndicator targetObj, SpawnId spawnId, int npcId, int numToSpawn, int spawnRange,
								int liveTime, boolean despawnAtAttackState, int validDistance, boolean attackTargetAfterSpawn,
								int hatepointsToAdd) {
		super(ActionType.spawn_on_target);
		this.targetObj = targetObj;
		this.spawnId = spawnId;
		this.npcId = npcId;
		this.numToSpawn = numToSpawn;
		this.spawnRange = spawnRange;
		this.liveTime = liveTime;
		this.despawnAtAttackState = despawnAtAttackState;
		this.validDistance = validDistance;
		this.attackTargetAfterSpawn = attackTargetAfterSpawn;
		this.hatepointsToAdd = hatepointsToAdd;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		ai.createSpawnGroup(spawnId, this, event.getObjectIndicator(targetObj, ai));
	}
	
	@Override
	public int hashCode() {
		int ret = 0;
		ret += 3*targetObj.ordinal();
		ret += 5*spawnId.ordinal();
		ret += 7*npcId + 11*numToSpawn;
		ret += 13*spawnRange;
		ret += 17*liveTime;
		ret += (despawnAtAttackState ? 19 : 0);
		ret += 23*validDistance;
		ret += (attackTargetAfterSpawn ? 29 : 0);
		ret += 31*hatepointsToAdd;
		return ret;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SpawnOnTargetAction) {
			SpawnOnTargetAction o = (SpawnOnTargetAction) obj;
			return (o.targetObj == targetObj
					&& o.spawnId == spawnId
					&& o.npcId == npcId
					&& o.numToSpawn == numToSpawn
					&& o.spawnRange == spawnRange
					&& o.liveTime == liveTime
					&& o.despawnAtAttackState == despawnAtAttackState
					&& o.validDistance == validDistance
					&& o.attackTargetAfterSpawn == attackTargetAfterSpawn
					&& o.hatepointsToAdd == hatepointsToAdd);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + spawnId + "]: [" + npcId + "]x" + numToSpawn + " --> "
								+ liveTime + "ms [" + targetObj + "] @[" + spawnRange + ", " + validDistance
								+ "] <" + despawnAtAttackState + ", " + attackTargetAfterSpawn + "> " + hatepointsToAdd;
	}
	
}

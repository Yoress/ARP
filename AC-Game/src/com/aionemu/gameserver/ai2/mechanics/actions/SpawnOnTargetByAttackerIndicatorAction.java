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
import com.aionemu.gameserver.ai2.mechanics.context.AttackerIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.SpawnId;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class SpawnOnTargetByAttackerIndicatorAction extends Action {
	
	public final AttackerIndicator target;
	
	public final SpawnId spawnId;
	
	public final int npcId;
	
	public final int numToSpawn;
	
	public final int spawnRange;
	
	public final int liveTime;
	
	public final boolean despawnAtAttackState;
	
	public final int validDistance;
	
	public final boolean attackTargetAfterSpawn;
	
	public final int hatepointsToAdd;
	
	public final boolean restrictedRange;
	
	public SpawnOnTargetByAttackerIndicatorAction(AttackerIndicator target, SpawnId spawnId, int npcId, int numToSpawn,
													int spawnRange, int liveTime, boolean despawnAtAttackState, int validDistance,
													boolean attackTargetAfterSpawn, int hatepointsToAdd, boolean restrictedRange) {
		super(ActionType.spawn_on_target_by_attacker_indicator);
		this.target = target;
		this.spawnId = spawnId;
		this.npcId = npcId;
		this.numToSpawn = numToSpawn;
		this.spawnRange = spawnRange;
		this.liveTime = liveTime;
		this.despawnAtAttackState = despawnAtAttackState;
		this.validDistance = validDistance;
		this.attackTargetAfterSpawn = attackTargetAfterSpawn;
		this.hatepointsToAdd = hatepointsToAdd;
		this.restrictedRange = restrictedRange;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		ai.createSpawnGroup(spawnId, this, getAttackerIndicator(target, ai));
	}
	
	@Override
	public int hashCode() {
		int ret = 0;
		ret += 3*target.ordinal();
		ret += 5*spawnId.ordinal();
		ret += 7*npcId;
		ret += 11*numToSpawn;
		ret += 13*spawnRange;
		ret += 17*liveTime;
		ret += (despawnAtAttackState ? 19 : 0);
		ret += 23*validDistance;
		ret += (attackTargetAfterSpawn ? 29 : 0);
		ret += 31*hatepointsToAdd;
		ret += (restrictedRange ? 37 : 0);
		return ret;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SpawnOnTargetByAttackerIndicatorAction) {
			SpawnOnTargetByAttackerIndicatorAction o = (SpawnOnTargetByAttackerIndicatorAction) obj;
			return (o.target == target
					&& o.spawnId == spawnId
					&& o.npcId == npcId
					&& o.numToSpawn == numToSpawn
					&& o.spawnRange == spawnRange
					&& o.liveTime == liveTime
					&& o.despawnAtAttackState == despawnAtAttackState
					&& o.validDistance == validDistance
					&& o.attackTargetAfterSpawn == attackTargetAfterSpawn
					&& o.hatepointsToAdd == hatepointsToAdd
					&& o.restrictedRange == restrictedRange);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + spawnId + "]: [" + npcId + "]x" + numToSpawn + " --> "
				+ liveTime + "ms [" + target + "] @[" + spawnRange + ", " + validDistance
				+ "] <" + despawnAtAttackState + ", " + attackTargetAfterSpawn + ", " + restrictedRange
				+ "> " + hatepointsToAdd;
	}
	
}

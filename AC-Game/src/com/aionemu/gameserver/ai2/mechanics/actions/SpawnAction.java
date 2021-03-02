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
import com.aionemu.gameserver.ai2.mechanics.context.SpawnId;
import com.aionemu.gameserver.ai2.mechanics.context.SpawnLocationType;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class SpawnAction extends Action {
	
	public final SpawnId spawnId;
	
	public final int npcId;
	
	public final int numToSpawn;
	
	public final SpawnLocationType spawnLocationType;
	
	public final float x;
	
	public final float y;
	
	public final float z;
	
	public final int dir;
	
	public final int spawnRange;
	
	public final int liveTime;
	
	public final boolean despawnAtAttackState;
	
	public final boolean isAerialSpawn;
	
	public final String pathname;
	
	public SpawnAction(SpawnId spawnId, int npcId, int numToSpawn, SpawnLocationType spawnLocationType,
						float x, float y, float z, int dir, int spawnRange, int liveTime, boolean despawnAtAttackState,
						boolean isAerialSpawn, String pathname) {
		super(ActionType.spawn);
		this.spawnId = spawnId;
		this.npcId = npcId;
		this.numToSpawn = numToSpawn;
		this.spawnLocationType = spawnLocationType;
		this.x = x;
		this.y = y;
		this.z = z;
		this.dir = dir;
		this.spawnRange = spawnRange;
		this.liveTime = liveTime;
		this.despawnAtAttackState = despawnAtAttackState;
		this.isAerialSpawn = isAerialSpawn;
		this.pathname = pathname;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		ai.createSpawnGroup(spawnId, this);
	}
	
	@Override
	public int hashCode() {
		int ret = 0;
		ret += 3*spawnId.ordinal();
		ret += 5*npcId + 7*numToSpawn;
		ret += 11*spawnLocationType.ordinal();
		ret += 13*((int) x);
		ret += 17*((int) y);
		ret += 19*((int) z);
		ret += 23*dir;
		ret += 29*spawnRange;
		ret += 31*liveTime;
		ret += (despawnAtAttackState ? 37 : 0);
		ret += (isAerialSpawn ? 41 : 0);
		ret += 43*pathname.hashCode();
		return ret;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SpawnAction) {
			SpawnAction o = (SpawnAction) obj;
			return (o.spawnId == spawnId
					&& o.npcId == npcId
					&& o.numToSpawn == numToSpawn
					&& o.spawnLocationType == spawnLocationType
					&& o.x == x && o.y == y && o.z == z && o.dir == dir
					&& o.spawnRange == spawnRange
					&& o.liveTime == liveTime
					&& o.despawnAtAttackState == despawnAtAttackState
					&& o.isAerialSpawn == isAerialSpawn
					&& o.pathname == pathname);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + spawnId + "]: [" + npcId + "]x" + numToSpawn + " --> " + liveTime + "ms (" + x + ", " + y + ", " + z + ", " + dir
								+ ") [" + spawnLocationType + "] @" + spawnRange + " <" + despawnAtAttackState + ", "
								+ isAerialSpawn + "> --> " + pathname;
	}
	
}

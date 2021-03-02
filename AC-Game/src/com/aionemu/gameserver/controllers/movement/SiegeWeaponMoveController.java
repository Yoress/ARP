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
package com.aionemu.gameserver.controllers.movement;

import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOVE;
import com.aionemu.gameserver.taskmanager.tasks.MoveTaskManager;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.geo.GeoService;
import com.aionemu.gameserver.world.geo.nav.NavService;

/**
 * @author xTz
 * @modified Yon (Aion Reconstruction Project) -- Added pathfinding support to
 * {@link #moveToDestination()} and {@link #moveToLocation(float, float, float, float)}.
 * Added {@link #getTargetZ(Summon, float, float, float)}.
 */
public class SiegeWeaponMoveController extends SummonMoveController {
	
	private float pointX;
	private float pointY;
	private float pointZ;
	private float offset = 0.1f;
	private boolean cachedPathValid;
	private float[][] cachedPath;
	public static final float MOVE_CHECK_OFFSET = 0.1f;
	
	public SiegeWeaponMoveController(Summon owner) {
		super(owner);
	}
	
	/**
	 * @return if destination reached
	 */
	@Override
	public void moveToDestination() {
		if (!owner.canPerformMove() || (owner.getAi2().getSubState() == AISubState.CAST)) {
			if (started.compareAndSet(true, false)) {
				setAndSendStopMove(owner);
			}
			updateLastMove();
			return;
		} else if (started.compareAndSet(false, true)) {
			movementMask = MovementMask.NPC_STARTMOVE;
			PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner));
		}
		
		if (MathUtil.getDistance(owner.getTarget(), pointX, pointY, pointZ) > MOVE_CHECK_OFFSET) {
			pointX = owner.getTarget().getX();
			pointY = owner.getTarget().getY();
			pointZ = owner.getTarget().getZ();
			cachedPathValid = false;
		}
		if ((!cachedPathValid || cachedPath == null) && owner.getTarget() instanceof Creature) {
			cachedPath = NavService.getInstance().navigateToTarget(owner, (Creature) owner.getTarget());
			cachedPathValid = true;
		}
		if (cachedPath != null && cachedPath.length > 0 && owner.getTarget() instanceof Creature) {
			float[] p1 = cachedPath[0];
			assert p1.length == 3;
			moveToLocation(p1[0], p1[1], getTargetZ(owner, p1[0], p1[1], p1[2]), offset);
		} else {
			if (cachedPath != null) cachedPath = null;
			moveToLocation(pointX, pointY, pointZ, offset);
		}
		updateLastMove();
	}
	
	@Override
	public void moveToTargetObject() {
		updateLastMove();
		MoveTaskManager.getInstance().addCreature(owner);
	}
	
	/**
	 * @param targetX
	 * @param targetY
	 * @param targetZ
	 * @param offset
	 * @return
	 */
	protected void moveToLocation(float targetX, float targetY, float targetZ, float offset) {
		boolean directionChanged;
		float ownerX = owner.getX();
		float ownerY = owner.getY();
		float ownerZ = owner.getZ();
		
		directionChanged = targetX != targetDestX || targetY != targetDestY || targetZ != targetDestZ;
		
		if (directionChanged) {
			heading = (byte) (Math.toDegrees(Math.atan2(targetY - ownerY, targetX - ownerX)) / 3);
		}
		
		targetDestX = targetX;
		targetDestY = targetY;
		targetDestZ = targetZ;
		
		float currentSpeed = owner.getGameStats().getMovementSpeedFloat();
		float futureDistPassed = currentSpeed * (System.currentTimeMillis() - lastMoveUpdate) / 1000f;
		
		float dist = (float) MathUtil.getDistance(ownerX, ownerY, ownerZ, targetX, targetY, targetZ);
		
		if (dist == 0) {
			return;
		}
		
		if (futureDistPassed > dist) {
			futureDistPassed = dist;
		}
		
		if (futureDistPassed == dist) {
			if (cachedPath != null && cachedPath.length > 0) {
				float[][] tempCache = new float[cachedPath.length - 1][];
				if (tempCache.length > 0) {
					System.arraycopy(cachedPath, 1, tempCache, 0, cachedPath.length - 1);
					cachedPath = tempCache;
				} else {
					cachedPath = null;
					cachedPathValid = false;
				}
			}
		}
		
		float distFraction = futureDistPassed / dist;
		float newX = (targetDestX - ownerX) * distFraction + ownerX;
		float newY = (targetDestY - ownerY) * distFraction + ownerY;
		float newZ = (targetDestZ - ownerZ) * distFraction + ownerZ;
		World.getInstance().updatePosition(owner, newX, newY, newZ, heading, false);
		if (directionChanged) {
			movementMask = MovementMask.NPC_STARTMOVE;
			PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner));
		}
	}
	
	/**
	 * Adjusts the target Z-value to better approximate the position where the owner should move to.
	 * If {@link GeoDataConfig#GEO_NPC_MOVE} is enabled and the npc is not flying, the GeoService will
	 * be used to determine the true Z value (if the npc's geoZ has not been updated recently).
	 * 
	 * @param owner -- owner
	 * @param x -- Target x position
	 * @param y -- Target y position
	 * @param z -- Target z position (given by {@link NavService}), it's assumed to be within 1 of true z.
	 * @return The adjusted Z-value for this destination
	 */
	private float getTargetZ(Summon owner, float x, float y, float z) {
		float targetZ = z;
		if (GeoDataConfig.GEO_NPC_MOVE && !owner.isFlying()) {
			if (owner.getGameStats().checkGeoNeedUpdate()) {
				targetZ = GeoService.getInstance().getZ(owner.getWorldId(), x, y, z, 1.1F, owner.getInstanceId());
			}
		}
		return targetZ;
	}
	
}

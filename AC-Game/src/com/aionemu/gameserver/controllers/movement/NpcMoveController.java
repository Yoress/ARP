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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.handler.TargetEventHandler;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.ai2.mechanics.context.Alias;
import com.aionemu.gameserver.ai2.mechanics.context.Alias.AliasPosition;
import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.model.actions.NpcActions;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.geometry.Point3D;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.templates.walker.RouteStep;
import com.aionemu.gameserver.model.templates.zone.Point2D;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOVE;
import com.aionemu.gameserver.spawnengine.WalkerGroup;
import com.aionemu.gameserver.taskmanager.tasks.MoveTaskManager;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.collections.LastUsedCache;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.geo.GeoService;
import com.aionemu.gameserver.world.geo.nav.NavService;

/**
 * @author ATracer
 * @modified Yon (Aion Reconstruction Project) -- {@link #setRouteStep(RouteStep, RouteStep)} modified to look at Z values with the GeoService,
 * added {@link #getTargetZ(Npc, float, float, float)}, modified {@link #moveToLocation(float, float, float, float)} to add support for
 * pathfinding cache + removed deprecated method calls and tweaked geoZ calculation, modified {@link #moveToDestination()} to add support
 * for pathfinding, smooth turning for random walking NPC's.
 * @reworked Yon (Aion Reconstruction Project) -- Deprecated (soon to remove) {@link #lastSteps}, {@link #stepSequenceNr},
 * {@link #storeStep()}, {@link #recallPreviousStep()}, {@link #clearBackSteps()}; added {@link #moveToAlias(Alias)}, and {@link #moveToHome()}.
 */
public class NpcMoveController extends CreatureMoveController<Npc> {
	
	private static final Logger log = LoggerFactory.getLogger(NpcMoveController.class);
	public static final float MOVE_CHECK_OFFSET = 0.1f;
	private static final float MOVE_OFFSET = 0.05f;
	private Destination destination = Destination.TARGET_OBJECT;
	private float pointX;
	private float pointY;
	private float pointZ;
	private boolean cachedPathValid;
	private float[][] cachedPath;
	
	@Deprecated
	private LastUsedCache<Byte, Point3D> lastSteps = null;
	@Deprecated
	private byte stepSequenceNr = 0;
	
	private float offset = 0.1f;
	// walk related
	List<RouteStep> currentRoute;
	int currentPoint;
	int walkPause;
	private float cachedTargetZ;
	
	//Mechanics System related
	private AliasPosition aliasPosition;
	
	public NpcMoveController(Npc owner) {
		super(owner);
	}
	
	private static enum Destination {
		TARGET_OBJECT,
		POINT,
		HOME,
		ALIAS;
	}
	
	/**
	 * Move to current target
	 */
	public void moveToTargetObject() {
		if (started.compareAndSet(false, true)) {
			if (owner.getAi2().isLogging()) {
				AI2Logger.moveinfo(owner, "MC: moveToTarget started");
			}
			destination = Destination.TARGET_OBJECT;
			updateLastMove();
			MoveTaskManager.getInstance().addCreature(owner);
		}
	}
	
	public void moveToPoint(float x, float y, float z) {
		if (started.compareAndSet(false, true)) {
			if (owner.getAi2().isLogging()) {
				AI2Logger.moveinfo(owner, "MC: moveToPoint started");
			}
			destination = Destination.POINT;
			pointX = x;
			pointY = y;
			pointZ = z;
			updateLastMove();
			MoveTaskManager.getInstance().addCreature(owner);
		}
	}
	
	public void moveToAlias(Alias alias) {
		if (started.compareAndSet(false, true)) {
			if (owner.getAi2().isLogging()) {
				AI2Logger.moveinfo(owner, "MC: moveToAlias started");
			}
			cachedPathValid = false;
			aliasPosition = alias.getPosition();
			destination = Destination.ALIAS;
			pointX = aliasPosition.x;
			pointY = aliasPosition.y;
			pointZ = aliasPosition.z;
			updateLastMove();
			MoveTaskManager.getInstance().addCreature(owner);
		}
	}
	
	public void moveToHome() {
		if (started.compareAndSet(false, true)) {
			if (owner.getAi2().isLogging()) {
				AI2Logger.moveinfo(owner, "MC: moveToHome started");
			}
			cachedPathValid = false;
			float x = owner.getSpawn().getX(), y = owner.getSpawn().getY(), z = owner.getSpawn().getZ();
			destination = Destination.HOME;
			pointX = x;
			pointY = y;
			pointZ = z;
			updateLastMove();
			MoveTaskManager.getInstance().addCreature(owner);
		}
	}
	
	public void moveToNextPoint() {
		if (started.compareAndSet(false, true)) {
			if (owner.getAi2().isLogging()) {
				AI2Logger.moveinfo(owner, "MC: moveToNextPoint started");
			}
			destination = Destination.POINT;
			updateLastMove();
			MoveTaskManager.getInstance().addCreature(owner);
		}
	}
	
	/**
	 * @return if destination reached
	 */
	@Override
	public void moveToDestination() {
		if (owner.getAi2().isLogging()) {
			AI2Logger.moveinfo(owner, "moveToDestination destination: " + destination);
		}
		if (NpcActions.isAlreadyDead(owner)) {
			abortMove();
			return;
		}
		if (!owner.canPerformMove() || (owner.getAi2().getSubState() == AISubState.CAST)) {
			if (owner.getAi2().isLogging()) {
				AI2Logger.moveinfo(owner, "moveToDestination can't perform move");
			}
			if (started.compareAndSet(true, false)) {
				setAndSendStopMove(owner);
			}
			updateLastMove();
			return;
		} else if (started.compareAndSet(false, true)) {
			movementMask = MovementMask.NPC_STARTMOVE;
			PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner));
		}
		
		if (!started.get()) {
			if (owner.getAi2().isLogging()) {
				AI2Logger.moveinfo(owner, "moveToDestination not started");
			}
		}
				
		switch (destination) {
			case TARGET_OBJECT:
				Npc npc = (Npc) owner;
				VisibleObject target = owner.getTarget();// todo no target
				if (target == null) { //This check is not needed, but I'll leave it for clarity.
					return;
				}
				if (!(target instanceof Creature)) { //instanceof returns false if target is null.
					return;
				}
				if (MathUtil.getDistance(target, pointX, pointY, pointZ) > MOVE_CHECK_OFFSET) {
					Creature creature = (Creature) target;
					offset = npc.getController().getAttackDistanceToTarget();
					pointX = target.getX();
					pointY = target.getY();
					pointZ = getTargetZ(npc, creature);
					cachedPathValid = false;
				}
				if (!cachedPathValid || cachedPath == null) {
					cachedPath = NavService.getInstance().navigateToTarget(npc, (Creature) target);
					if (cachedPath != null) { //Add a bit of randomness to the last point to prevent entities from stacking directly ontop of eachother.
						//TODO: Move to NavService and make sure this random point is on the navmesh!
						if (Rnd.nextBoolean()) {
							cachedPath[cachedPath.length - 1][0] += Rnd.nextDouble()*owner.getObjectTemplate().getBoundRadius().getSide();
						} else {
							cachedPath[cachedPath.length - 1][0] -= Rnd.nextDouble()*owner.getObjectTemplate().getBoundRadius().getSide();
						}
						if (Rnd.nextBoolean()) {
							cachedPath[cachedPath.length - 1][1] += Rnd.nextDouble()*owner.getObjectTemplate().getBoundRadius().getSide();
						} else {
							cachedPath[cachedPath.length - 1][1] -= Rnd.nextDouble()*owner.getObjectTemplate().getBoundRadius().getSide();
						}
					}
					cachedPathValid = true;
				}
				if (cachedPath != null && cachedPath.length > 0) {
					float[] p1 = cachedPath[0];
					assert p1.length == 3;
					moveToLocation(p1[0], p1[1], getTargetZ(npc, p1[0], p1[1], p1[2]), offset);
				} else {
					if (cachedPath != null) cachedPath = null;
					moveToLocation(pointX, pointY, pointZ, offset);
				}
				break;
			case POINT:
				offset = 0.1f;
				/*********************************************************************************************/
				if (owner.getKnownList().getVisiblePlayers().size() > 0
					&& owner.getAi2().getSubState() == AISubState.WALK_RANDOM /*|| owner.getAi2().getSubState() == AISubState.WALK_PATH*/) {
					//Smooth turning for random walking NPC's -- Note that for WALK_PATH, the path can break, so it's disabled.
					float ownerX = owner.getX();
					float ownerY = owner.getY();
					byte ownerHeading = owner.getHeading();
					
					byte targetDestHeading = (byte) (MathUtil.diamondAngle(pointX - ownerX, pointY - ownerY) * 30F);
					int headingChange = 0;
					
					if ((ownerHeading < 30 || targetDestHeading < 30) && (ownerHeading > 90 || targetDestHeading > 90)) {
						// If the headings are in Q1 and Q4 (the angle between them passes through 0 heading)
						if (ownerHeading > targetDestHeading) {
							headingChange = Math.abs(ownerHeading - (targetDestHeading + 120));
						} else {
							headingChange = Math.abs((ownerHeading + 120) - targetDestHeading);
						}
					} else {
						headingChange = Math.abs(ownerHeading - targetDestHeading);
					}
					
					float newX;
					float newY;
					/*
					 * NOTE: If using small changes in heading for this intermediate point,
					 * it may cause entities to walk off the map. This happens when the heading
					 * calculation in moveToLocation() isn't accurate enough to depict such a small change.
					 * The smallest value that should be considered is a change of 3 heading at a time.
					 * In this fashion, mobs should turn smoothly without wandering off the map.
					 */
					final int maxHeadingChange = 3;
					if (headingChange > maxHeadingChange) {
						//If we're here, then the heading has to change by more than 3 to move towards the target point.
						//The amount we'll move forward as we turn around
						final float scale = owner.getObjectTemplate().getBoundRadius().getSide();
						//Radius of the turn circle using arc length formula
						final float radius = (float) (scale/Math.toRadians(maxHeadingChange * 3));
						//2D distance to target point
						float dist = (float) MathUtil.getDistance(ownerX, ownerY, pointX, pointY);
						/*
						 * Use law of cosines with radius, dist, and the angle between to find sq distance of target point from center
						 * of the turning circle. The angle between is the angle between the current heading and the destination
						 * heading minus (maxHeadingChange + ((180 - maxHeadingChange)/2)). If the sq distance is larger than
						 * radius*radius, then the point is outside of the turning circle, and this algorithm will function.
						 * 
						 * We also check if the distance we're moving is larger than the radius of the turning circle,
						 * if it's not, then we may end up circling nearly the entire circle just to end up at our destination.
						 * That would result in mobs walking in circles to reach a point right next to them. Ideally, points like
						 * that would never be given to us, but the random walk function at the time of writing this is stupid.
						 */
						final double cosAngleBetween = Math.cos(Math.toRadians(headingChange - ((maxHeadingChange * 3) + ((180 - (maxHeadingChange * 3))/2))));
						boolean outsideCircle = (radius * radius) < ((radius * radius) + (dist * dist) - (2 * radius * dist * cosAngleBetween));
						if (outsideCircle && dist > radius) {
							//If we're here, we will attempt to smooth turn to the destination.
							if (((ownerHeading + 60) % 120) == targetDestHeading) {
								boolean right = Rnd.nextBoolean();
								if (right) {
									newX = (float) (scale*Math.cos(Math.toRadians(((ownerHeading + (120 - maxHeadingChange)) % 120) * 3)) + ownerX);
									newY = (float) (scale*Math.sin(Math.toRadians(((ownerHeading + (120 - maxHeadingChange)) % 120) * 3)) + ownerY);
								} else {
									newX = (float) (scale*Math.cos(Math.toRadians(((ownerHeading + maxHeadingChange) % 120) * 3)) + ownerX);
									newY = (float) (scale*Math.sin(Math.toRadians(((ownerHeading + maxHeadingChange) % 120) * 3)) + ownerY);
								}
							} else {
								//Find out which side would be closer to the target
								//Would prefer to avoid using cos and sin here, but for now it's fine.
								//Create a point that is directly in front of our mob
								float forwardX = (float) Math.cos(Math.toRadians(ownerHeading * 3));
								float forwardY = (float) Math.sin(Math.toRadians(ownerHeading * 3));
								//Use a partial cross product of the displacement vectors that move forwards and to the target to
								//determine sidedness via the right-hand rule
								boolean right = ((forwardX) * (pointY - ownerY) - (forwardY) * (pointX - ownerX)) < 0;
								if (right) {
									newX = (float) (scale*Math.cos(Math.toRadians(((ownerHeading + (120 - maxHeadingChange)) % 120) * 3)) + ownerX);
									newY = (float) (scale*Math.sin(Math.toRadians(((ownerHeading + (120 - maxHeadingChange)) % 120) * 3)) + ownerY);
								} else {
									newX = (float) (scale*Math.cos(Math.toRadians(((ownerHeading + maxHeadingChange) % 120) * 3)) + ownerX);
									newY = (float) (scale*Math.sin(Math.toRadians(((ownerHeading + maxHeadingChange) % 120) * 3)) + ownerY);
								}
							}
							
		//					offset = 0.1f;
							moveToLocation(newX, newY, pointZ, offset);
							break;
						}
					}
				}
				/*********************************************************************************************/
				
	//			offset = 0.1f;
				moveToLocation(pointX, pointY, pointZ, offset);
				break;
			case HOME:
			case ALIAS:
				if (!cachedPathValid || cachedPath == null) {
					cachedPath = NavService.getInstance().navigateToLocation(owner, pointX, pointY, pointZ);
					cachedPathValid = true;
				}
				if (cachedPath != null && cachedPath.length > 1) {
					float[] p1 = cachedPath[0];
					assert p1.length == 3;
					moveToLocation(p1[0], p1[1], getTargetZ(owner, p1[0], p1[1], p1[2]), offset);
				} else {
					if (cachedPath != null) cachedPath = null;
					moveToLocation(pointX, pointY, pointZ, offset);
				}
				break;
		}
		updateLastMove();
	}
	
	/**
	 * @param npc
	 * @param creature
	 * @return
	 */
	private float getTargetZ(Npc npc, Creature creature) {
		float targetZ = creature.getZ();
		if (GeoDataConfig.GEO_NPC_MOVE && creature.isFlying() && !npc.isFlying()) {
			if (npc.getGameStats().checkGeoNeedUpdate()) {
				cachedTargetZ = GeoService.getInstance().getZ(creature);
			}
			targetZ = cachedTargetZ;
		}
		return targetZ;
	}
	
	/**
	 * Adjusts the target Z-value to better approximate the position where the owner should move to.
	 * If {@link GeoDataConfig#GEO_NPC_MOVE} is enabled and the npc is not flying, the GeoService will
	 * be used to determine the true Z value (if the npc's geoZ has not been updated recently).
	 * 
	 * @param npc -- owner
	 * @param x -- Target x position
	 * @param y -- Target y position
	 * @param z -- Target z position (given by {@link NavService}), it's assumed to be within 1 of true z.
	 * @return The adjusted Z-value for this destination
	 */
	private float getTargetZ(Npc npc, float x, float y, float z) {
		float targetZ = z;
		if (GeoDataConfig.GEO_NPC_MOVE && !npc.isFlying()) {
			if (npc.getGameStats().checkGeoNeedUpdate()) {
				cachedTargetZ = GeoService.getInstance().getZ(npc.getWorldId(), x, y, z, 1.1F, npc.getInstanceId());
				targetZ = cachedTargetZ;
			}
		}
		return targetZ;
	}
	
	/**
	 * @param targetX
	 * @param targetY
	 * @param targetZ
	 * @param offset
	 * @return
	 */
	protected void moveToLocation(float targetX, float targetY, float targetZ, float offset) {
		boolean directionChanged = false;
		float ownerX = owner.getX();
		float ownerY = owner.getY();
		float ownerZ = owner.getZ();
		
		directionChanged = targetX != targetDestX || targetY != targetDestY || targetZ != targetDestZ;
		
		if (directionChanged) {
			heading = (byte) (Math.toDegrees(Math.atan2(targetY - ownerY, targetX - ownerX)) / 3);
			if (heading < 0) heading += 120;
//			heading = (byte) (MathUtil.diamondAngle(targetX - ownerX, targetY - ownerY) * 30F); //Not as accurate as above when using small vectors
		}
		
		if (owner.getAi2().isLogging()) {
			AI2Logger.moveinfo(owner, "OLD targetDestX: " + targetDestX + " targetDestY: " + targetDestY + " targetDestZ " + targetDestZ);
		}
		
		// to prevent broken walkers in case of activating/deactivating zones
		if (targetX == 0 && targetY == 0) {
			targetX = owner.getSpawn().getX();
			targetY = owner.getSpawn().getY();
			targetZ = owner.getSpawn().getZ();
		}
		
		targetDestX = targetX;
		targetDestY = targetY;
		targetDestZ = targetZ;
		
		if (owner.getAi2().isLogging()) {
			AI2Logger.moveinfo(owner, "ownerX=" + ownerX + " ownerY=" + ownerY + " ownerZ=" + ownerZ);
			AI2Logger.moveinfo(owner, "targetDestX: " + targetDestX + " targetDestY: " + targetDestY + " targetDestZ " + targetDestZ);
		}
		
		float currentSpeed = owner.getGameStats().getMovementSpeedFloat();
		float futureDistPassed = currentSpeed * (System.currentTimeMillis() - lastMoveUpdate) / 1000f;
		float dist = (float) MathUtil.getDistance(ownerX, ownerY, ownerZ, targetX, targetY, targetZ);
		
		if (owner.getAi2().isLogging()) {
			AI2Logger.moveinfo(owner, "futureDist: " + futureDistPassed + " dist: " + dist);
		}
		
		if (dist == 0) {
			if (owner.getAi2().getState() == AIState.RETURNING) {
				if (owner.getAi2().isLogging()) {
					AI2Logger.moveinfo(owner, "State RETURNING: abort move");
				}
				TargetEventHandler.onTargetReached((NpcAI2) owner.getAi2());
			}
			return;
		}
		
		if (futureDistPassed > dist) {
			futureDistPassed = dist;
		}
		
		if (futureDistPassed == dist
			&& (destination == Destination.TARGET_OBJECT || destination == Destination.HOME || destination == Destination.ALIAS)) {
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
		
		if ((ownerX == newX) && (ownerY == newY) && owner.getSpawn().getRandomWalk() > 0) {
			return;
		}
		if (GeoDataConfig.GEO_NPC_MOVE && GeoDataConfig.GEO_ENABLE && owner.getAi2().getSubState() != AISubState.WALK_PATH && owner.getAi2().getState() != AIState.RETURNING
				&& owner.getGameStats()./*getLastGeoZUpdate() < System.currentTimeMillis()*/checkGeoNeedUpdate()) {
			// fix Z if npc doesn't move to spawn point
			if (owner.getSpawn().getX() != targetDestX || owner.getSpawn().getY() != targetDestY || owner.getSpawn().getZ() != targetDestZ) {
				float geoZ = GeoService.getInstance().getZ(owner.getWorldId(), newX, newY, newZ, 0, owner.getInstanceId());
				if (Math.abs(newZ - geoZ) > 1) {
					directionChanged = true;
				}
				// add altitude
				//Why would we add altitude? We don't want mobs to float... upper BR and height should be the same, anyway.
				newZ = geoZ /*+ owner.getObjectTemplate().getBoundRadius().getUpper() - owner.getObjectTemplate().getHeight()*/;
			}
//			owner.getGameStats().setLastGeoZUpdate(System.currentTimeMillis() + 1000); //Delay is handled above with checkGeoNeedUpdate().
		}
		if (owner.getAi2().isLogging()) {
			AI2Logger.moveinfo(owner, "newX=" + newX + " newY=" + newY + " newZ=" + newZ + " mask=" + movementMask);
		}
		
		World.getInstance().updatePosition(owner, newX, newY, newZ, heading, false);
		
		byte newMask = getMoveMask(directionChanged);
		if (movementMask != newMask || directionChanged) {
			if (owner.getAi2().isLogging()) {
				AI2Logger.moveinfo(owner, "oldMask=" + movementMask + " newMask=" + newMask);
			}
			movementMask = newMask;
			PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner));
		}
	}
	
	private byte getMoveMask(boolean directionChanged) {
		if (directionChanged) {
//			return MovementMask.NPC_STARTMOVE; //TODO: Use this when NPC actually starts moving; currently direction can change mid movement
		} else if (owner.getAi2().getState() == AIState.RETURNING) {
			return MovementMask.NPC_RUN_FAST;
		} else if (owner.getAi2().getState() == AIState.FOLLOWING) {
			return MovementMask.NPC_WALK_SLOW;
		}
		
		byte mask = MovementMask.IMMEDIATE;
		final Stat2 stat = owner.getGameStats().getMovementSpeed();
		if (owner.isInState(CreatureState.WEAPON_EQUIPPED)) {
			mask = stat.getBonus() < 0 ? MovementMask.NPC_RUN_FAST : MovementMask.NPC_RUN_SLOW;
		} else if (owner.isInState(CreatureState.WALKING) || owner.isInState(CreatureState.ACTIVE)) {
			mask = stat.getBonus() < 0 ? MovementMask.NPC_WALK_FAST : MovementMask.NPC_WALK_SLOW;
		}
		if (owner.isFlyingOrGliding()) {
			mask |= MovementMask.GLIDE;
		}
		return mask;
	}
	
	@Override
	public void abortMove() {
		if (!started.get()) {
			return;
		}
		resetMove();
		setAndSendStopMove(owner);
	}
	
	/**
	 * Initialize values to default ones
	 */
	public void resetMove() {
		if (owner.getAi2().isLogging()) {
			AI2Logger.moveinfo(owner, "MC perform stop");
		}
		started.set(false);
		targetDestX = 0;
		targetDestY = 0;
		targetDestZ = 0;
		pointX = 0;
		pointY = 0;
		pointZ = 0;
	}
	
	/**
	 * Walker
	 *
	 * @param currentRoute
	 */
	public void setCurrentRoute(List<RouteStep> currentRoute) {
		if (currentRoute == null) {
			AI2Logger.info(owner.getAi2(), String.format("MC: setCurrentRoute is setting route to null (NPC id: {})!!!", owner.getNpcId()));
		} else {
			this.currentRoute = currentRoute;
		}
		this.currentPoint = 0;
	}
	
	public void setRouteStep(RouteStep step, RouteStep prevStep) {
		Point2D dest = null;
		if (owner.getWalkerGroup() != null) {
			dest = WalkerGroup.getLinePoint(new Point2D(prevStep.getX(), prevStep.getY()), new Point2D(step.getX(), step.getY()), owner.getWalkerGroupShift());
			this.pointZ = prevStep.getZ();
			if (GeoDataConfig.GEO_ENABLE && GeoDataConfig.GEO_NPC_MOVE) {
				if (!owner.isFlying())
					this.pointZ = GeoService.getInstance().getZ(owner.getWorldId(), dest.getX(), dest.getY(), step.getZ(), 0, owner.getInstanceId());
				else
					this.pointZ = step.getZ();
			}
			owner.getWalkerGroup().setStep(owner, step.getRouteStep());
		} else {
			this.pointZ = step.getZ();
		}
		this.currentPoint = step.getRouteStep() - 1;
		this.pointX = dest == null ? step.getX() : dest.getX();
		this.pointY = dest == null ? step.getY() : dest.getY();
		this.destination = Destination.POINT;
		this.walkPause = step.getRestTime();
	}
	
	public int getCurrentPoint() {
		return currentPoint;
	}
	
	public boolean isReachedPoint() {
		return MathUtil.getDistance(owner.getX(), owner.getY(), owner.getZ(), pointX, pointY, pointZ) < MOVE_OFFSET;
	}
	
	//Would be better as "has", but consistency.
	public boolean isReachedAlias() {
		if (aliasPosition == null) return true;
		float x = aliasPosition.x, y = aliasPosition.y, z = aliasPosition.z;
		boolean reached = MathUtil.getDistance(owner.getX(), owner.getY(), owner.getZ(), x, y, z) < MOVE_OFFSET;
		if (reached) {
			heading = aliasPosition.h;
			World.getInstance().updatePosition(owner, owner.getX(), owner.getY(), owner.getZ(), heading, false);
			
			byte newMask = getMoveMask(true);
			if (owner.getAi2().isLogging()) {
				AI2Logger.moveinfo(owner, "Alias reached: oldMask=" + movementMask + " newMask=" + newMask);
			}
			movementMask = newMask;
			PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner));
		}
		return reached;
	}
	
	public void chooseNextStep() {
		int oldPoint = currentPoint;
		if (currentRoute == null) {
			WalkManager.stopWalking((NpcAI2) owner.getAi2());
			log.warn("Bad Walker Id: " + owner.getNpcId() + " - point: " + oldPoint);
			return;
		}
		if (currentPoint < (currentRoute.size() - 1)) {
			currentPoint++;
		} else {
			currentPoint = 0;
		}
		setRouteStep(currentRoute.get(currentPoint), currentRoute.get(oldPoint));
	}
	
	public int getWalkPause() {
		return walkPause;
	}
	
	public boolean isChangingDirection() {
		return currentPoint == 0;
	}
	
	@Override
	public final float getTargetX2() {
		return started.get() ? targetDestX : owner.getX();
	}
	
	@Override
	public final float getTargetY2() {
		return started.get() ? targetDestY : owner.getY();
	}
	
	@Override
	public final float getTargetZ2() {
		return started.get() ? targetDestZ : owner.getZ();
	}
	
	/**
	 * @return
	 */
	public boolean isFollowingTarget() {
		return destination == Destination.TARGET_OBJECT;
	}
	
	@Deprecated
	public void storeStep() {
		if (owner.getAi2().getState() == AIState.RETURNING) {
			return;
		}
		if (lastSteps == null) {
			lastSteps = new LastUsedCache<Byte, Point3D>(10);
		}
		Point3D currentStep = new Point3D(owner.getX(), owner.getY(), owner.getZ());
		if (owner.getAi2().isLogging()) {
			AI2Logger.moveinfo(owner, "store back step: X=" + owner.getX() + " Y=" + owner.getY() + " Z=" + owner.getZ());
		}
		if (stepSequenceNr == 0 || MathUtil.getDistance(lastSteps.get(stepSequenceNr), currentStep) >= 10) {
			lastSteps.put(++stepSequenceNr, currentStep);
		}
	}
	
	@Deprecated
	public Point3D recallPreviousStep() {
		if (lastSteps == null) {
			lastSteps = new LastUsedCache<Byte, Point3D>(10);
		}
		
		Point3D result = stepSequenceNr == 0 ? null : lastSteps.get(stepSequenceNr--);
		
		if (result == null) {
			if (owner.getAi2().isLogging()) {
				AI2Logger.moveinfo(owner, "recall back step: spawn point");
			}
			targetDestX = owner.getSpawn().getX();
			targetDestY = owner.getSpawn().getY();
			targetDestZ = owner.getSpawn().getZ();
			result = new Point3D(targetDestX, targetDestY, targetDestZ);
		} else {
			if (owner.getAi2().isLogging()) {
				AI2Logger.moveinfo(owner, "recall back step: X=" + result.getX() + " Y=" + result.getY() + " Z=" + result.getZ());
			}
			targetDestX = result.getX();
			targetDestY = result.getY();
			targetDestZ = result.getZ();
		}
		
		return result;
	}
	
	@Deprecated
	public void clearBackSteps() {
		stepSequenceNr = 0;
		lastSteps = null;
		movementMask = MovementMask.IMMEDIATE;
	}
	
	@Override
	public void skillMovement() {
		this.movementMask = MovementMask.IMMEDIATE;
	}
}

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
package com.aionemu.gameserver.ai2.mechanics;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.mechanics.actions.SpawnAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SpawnOnMultiTargetAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SpawnOnTargetAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SpawnOnTargetByAttackerIndicatorAction;
import com.aionemu.gameserver.controllers.attack.AggroList;
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.geoEngine.math.FastMath;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.templates.walker.RouteStep;
import com.aionemu.gameserver.model.templates.walker.WalkerTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.geo.GeoService;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class MechanicSpawnGroup {
	
	private static final Logger LOG = LoggerFactory.getLogger(MechanicSpawnGroup.class);
	
	private static final byte ARBITRARY_SPAWN_CAP = 124;
	
	final Creature owner;
	
	private final TIntObjectHashMap<VisibleObject> spawns = new TIntObjectHashMap<VisibleObject>();
	
	private static final HashSet<String> REPORTED_MISSING_WALKER_ROUTE_SET = new HashSet<String>(10);
	
	public MechanicSpawnGroup(Creature owner) {
		this.owner = owner;
	}
	
	public void spawn(final SpawnAction spawnAction) {
		//Reject spawning new entities if too many have spawned already.
		if (spawns.size() > ARBITRARY_SPAWN_CAP) return;
		int npcId = spawnAction.npcId;
		int num = spawnAction.numToSpawn;
		float x, y, z;
		String pathname = spawnAction.pathname;
		switch (spawnAction.spawnLocationType) {
			case SPAWN_LOCATION_ABSOLUTE:
				x = spawnAction.x;
				y = spawnAction.y;
				z = spawnAction.z;
				break;
			case SPAWN_LOCATION_MY_POINT:
				x = owner.getX();
				y = owner.getY();
				if (spawnAction.spawnRange > 0) {
					//Calculate a random point within a circle of spawnRange radius, offset the target location
					float angle = (float) (2 * Rnd.get() * FastMath.PI);
					float radius = (float) (spawnAction.spawnRange * Math.sqrt(Rnd.get()));
					float dx = (float) (radius * Math.cos(angle));
					float dy = (float) (radius * Math.sin(angle));
					x += dx;
					y += dy;
					z = GeoService.getInstance().getZ(owner.getWorldId(), x, y, owner.getZ(), 0.5F, owner.getInstanceId());
				} else {
					z = owner.getZ();
				}
				break;
			case SPAWN_LOCATION_RELATIVE:
				x = owner.getX() + spawnAction.x;
				y = owner.getY() + spawnAction.y;
				z = owner.getZ() + spawnAction.z;
				break;
			case SPAWN_LOCATION_WAY_POINT_START:
				//TODO: Ensure all walker paths referenced here are valid
				WalkerTemplate route = DataManager.WALKER_DATA.getWalkerTemplate(pathname);
				if (route != null) {
					RouteStep step = route.getRouteStep(0);
					x = step.getX();
					y = step.getY();
					z = step.getZ();
				} else {
					LOG.error("Unable to find mechanic pathname: " + pathname);
					x = owner.getX(); y = owner.getY(); z = owner.getZ();
				}
				break;
			default:
				assert false:"Unsupported SpawnLocationType: " + spawnAction.spawnLocationType;
				LOG.error("Unsupported SpawnLocationType: " + spawnAction.spawnLocationType);
				x = owner.getX(); y = owner.getY(); z = owner.getZ();
		}
		
		int dir = spawnAction.dir;
		
		for (int i = 0; i < num; i++) {
			//TODO: Ensure all walker paths referenced here are valid
			final VisibleObject spawn = SpawnEngine.spawnObject(
						(pathname != null && !pathname.isEmpty() && DataManager.WALKER_DATA.getWalkerTemplate(pathname) != null) ? 
							SpawnEngine.addNewSingleTimeSpawn(owner.getWorldId(), npcId, x, y, z, (byte) (dir/3), pathname, 0):
							SpawnEngine.addNewSingleTimeSpawn(owner.getWorldId(), npcId, x, y, z, (byte) (dir/3)),
						owner.getInstanceId()
					);
			spawns.put(spawn.getObjectId(), spawn);
			
			/* FIXME: Remove Debug Code when the above TO-DO has been completed */ {
				if (pathname != null && !pathname.isEmpty() && DataManager.WALKER_DATA.getWalkerTemplate(pathname) == null) {
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							//"The Support Petition is already being processed."
//							String msg = "[tooltip:1300556]";
							String msg = "I'm having issues with my patrol route! Please report me!";
							PacketSendUtility.broadcastPacket(spawn, new SM_MESSAGE(spawn.getObjectId(), spawn.getName(), msg, ChatType.NORMAL));
						}
					}, 3000); //3-second delay; declare that the spawn location is not implemented!
					if (!REPORTED_MISSING_WALKER_ROUTE_SET.contains(pathname) && owner.getAi2() instanceof AbstractMechanicsAI2 && owner instanceof Npc) {
						REPORTED_MISSING_WALKER_ROUTE_SET.add(pathname);
						String mechanic = ((Npc) owner).getObjectTemplate().getMechanic();
						LOG.error("Missing Walker route: [" + pathname + "] for Mechanics System; Check handler: " + mechanic + ".");
					}
				}
			}
			
			try {
				if (spawnAction.isAerialSpawn) ((Creature) spawn).setState(CreatureState.FLYING);
				((Creature) spawn).getObserveController().addObserver(new ActionObserver(ObserverType.DEATH) {
					@Override
					public void died(Creature creature) {
						if (spawn != null) spawns.remove(spawn.getObjectId());
					}
				});
			} catch (ClassCastException e) {
				LOG.warn("Incorrect spawn ID or target passed into MechanicSpawnGroup#Spawn(SpawnAction)!", e);
			}
			if (spawnAction.liveTime > 0) ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					despawn(spawn.getObjectId(), spawnAction.despawnAtAttackState);
				}
			}, spawnAction.liveTime * 1000);
		}
	}
	
	public void spawn(final SpawnOnTargetAction spawnOnTargetAction, VisibleObject target) {
		//Reject spawning new entities if too many have spawned already.
		if (spawns.size() > ARBITRARY_SPAWN_CAP) return;
		if (!MathUtil.isIn3dRange(owner, target, spawnOnTargetAction.validDistance)) return;
		int npcId = spawnOnTargetAction.npcId;
		int num = spawnOnTargetAction.numToSpawn;
		float x = target.getX(), y = target.getY(), z = target.getZ();
		if (spawnOnTargetAction.spawnRange > 0) {
			//Calculate a random point within a circle of spawnRange radius, offset the target location
			float angle = (2 * Rnd.get() * FastMath.PI);
			float radius = (float) (spawnOnTargetAction.spawnRange * Math.sqrt(Rnd.get()));
			float dx = (float) (radius * Math.cos(angle));
			float dy = (float) (radius * Math.sin(angle));
			x += dx;
			y += dy;
			z = GeoService.getInstance().getZ(owner.getWorldId(), x, y, z, 0.5F, owner.getInstanceId());
		}
		
		byte h = MathUtil.estimateHeadingFrom(owner, target);
		
		for (int i = 0; i < num; i++) {
			final VisibleObject spawn = SpawnEngine.spawnObject(
					SpawnEngine.addNewSingleTimeSpawn(owner.getWorldId(), npcId, x, y, z, h),
					owner.getInstanceId()
				);
			spawns.put(spawn.getObjectId(), spawn);
			try {
				((Creature) spawn).getObserveController().addObserver(new ActionObserver(ObserverType.DEATH) {
					@Override
					public void died(Creature creature) {
						if (spawn != null) spawns.remove(spawn.getObjectId());
					}
				});
				if (spawnOnTargetAction.attackTargetAfterSpawn) {
					((Creature) spawn).getAggroList().addHate((Creature) target, spawnOnTargetAction.hatepointsToAdd);
				}
			} catch (ClassCastException e) {
				LOG.warn("Incorrect spawn ID or target passed into MechanicSpawnGroup#Spawn(SpawnOnTargetAction)!", e);
			}
			
			if (spawnOnTargetAction.liveTime > 0) ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					despawn(spawn.getObjectId(), spawnOnTargetAction.despawnAtAttackState);
				}
			}, spawnOnTargetAction.liveTime * 1000);
		}
	}
	
	public void spawn(final SpawnOnTargetByAttackerIndicatorAction spawnOnTargetByAttackerIndicatorAction, VisibleObject target) {
		//Reject spawning new entities if too many have spawned already.
		if (spawns.size() > ARBITRARY_SPAWN_CAP) return;
		if (!MathUtil.isIn3dRange(owner, target, spawnOnTargetByAttackerIndicatorAction.validDistance)) return;
		int npcId = spawnOnTargetByAttackerIndicatorAction.npcId;
		int num = spawnOnTargetByAttackerIndicatorAction.numToSpawn;
		float x = target.getX(), y = target.getY(), z = target.getZ();
		if (spawnOnTargetByAttackerIndicatorAction.spawnRange > 0) {
			//Calculate a random point within a circle of spawnRange radius, offset the target location
			float angle = (float) (2 * Rnd.get() * FastMath.PI);
			float radius = (float) (spawnOnTargetByAttackerIndicatorAction.spawnRange * Math.sqrt(Rnd.get()));
			float dx = (float) (radius * Math.cos(angle));
			float dy = (float) (radius * Math.sin(angle));
			x += dx;
			y += dy;
			z = GeoService.getInstance().getZ(owner.getWorldId(), x, y, z, 0.5F, owner.getInstanceId());
		}
		byte h = MathUtil.estimateHeadingFrom(owner, target);
		
		for (int i = 0; i < num; i++) {
			final VisibleObject spawn = SpawnEngine.spawnObject(
					SpawnEngine.addNewSingleTimeSpawn(owner.getWorldId(), npcId, x, y, z, h),
					owner.getInstanceId()
				);
			spawns.put(spawn.getObjectId(), spawn);
			try {
				((Creature) spawn).getObserveController().addObserver(new ActionObserver(ObserverType.DEATH) {
					@Override
					public void died(Creature creature) {
						if (spawn != null) spawns.remove(spawn.getObjectId());
					}
				});
				if (spawnOnTargetByAttackerIndicatorAction.attackTargetAfterSpawn) {
					((Creature) spawn).getAggroList().addHate((Creature) target, spawnOnTargetByAttackerIndicatorAction.hatepointsToAdd);
				}
			} catch (ClassCastException e) {
				LOG.warn("Incorrect spawn ID or target passed into MechanicSpawnGroup#Spawn(SpawnOnTargetByAttackerIndicatorAction)!", e);
			}
			
			if (spawnOnTargetByAttackerIndicatorAction.liveTime > 0) ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					despawn(spawn.getObjectId(), spawnOnTargetByAttackerIndicatorAction.despawnAtAttackState);
				}
			}, spawnOnTargetByAttackerIndicatorAction.liveTime * 1000);
		}
	}
	
	public void spawn(final SpawnOnMultiTargetAction spawnOnMultiTargetAction, AggroList aggroList) {
		//Reject spawning new entities if too many have spawned already.
		if (spawns.size() > ARBITRARY_SPAWN_CAP) return;
		int npcId = spawnOnMultiTargetAction.npcNameid;
		int num = spawnOnMultiTargetAction.numToSpawn;
		int count = 0;
		Creature[] targets = aggroList.getOrderedList(spawnOnMultiTargetAction.orderInAttackerList);
		if (targets != null) for (Creature target: targets) {
			if (target == null) continue;
			if (count > spawnOnMultiTargetAction.totalSetToSpawn) break;
			if (!MathUtil.isIn3dRange(owner, target, spawnOnMultiTargetAction.validDistance)) continue;
			
			float x = target.getX(), y = target.getY(), z = target.getZ();
			if (spawnOnMultiTargetAction.spawnRange > 0) {
				//Calculate a random point within a circle of spawnRange radius, offset the target location
				float angle = (float) (2 * Rnd.get() * FastMath.PI);
				float radius = (float) (spawnOnMultiTargetAction.spawnRange * Math.sqrt(Rnd.get()));
				float dx = (float) (radius * Math.cos(angle));
				float dy = (float) (radius * Math.sin(angle));
				x += dx;
				y += dy;
				z = GeoService.getInstance().getZ(owner.getWorldId(), x, y, z, 0.5F, owner.getInstanceId());
			}
			byte h = MathUtil.estimateHeadingFrom(owner, target);
			
			for (int i = 0; i < num; i++) {
				final VisibleObject spawn = SpawnEngine.spawnObject(
						SpawnEngine.addNewSingleTimeSpawn(owner.getWorldId(), npcId, x, y, z, h),
						owner.getInstanceId()
					);
				spawns.put(spawn.getObjectId(), spawn);
				try {
					((Creature) spawn).getObserveController().addObserver(new ActionObserver(ObserverType.DEATH) {
						@Override
						public void died(Creature creature) {
							if (spawn != null) spawns.remove(spawn.getObjectId());
						}
					});
					if (spawnOnMultiTargetAction.attackTargetAfterSpawn) {
						((Creature) spawn).getAggroList().addHate((Creature) target, spawnOnMultiTargetAction.hatepointsToAdd);
					}
				} catch (ClassCastException e) {
					LOG.warn("Incorrect spawn ID or target passed into MechanicSpawnGroup#Spawn(SpawnOnTargetByAttackerIndicatorAction)!", e);
				}
				
				if (spawnOnMultiTargetAction.liveTime > 0) ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawn(spawn.getObjectId(), spawnOnMultiTargetAction.despawnAtAttackState);
					}
				}, spawnOnMultiTargetAction.liveTime * 1000);
			}
			count++;
		}
	}
	
	private void despawn(int key, boolean whileAttacking) {
		VisibleObject npc = spawns.remove(key);
		if (npc != null) {
			if (whileAttacking) {
				npc.getController().onDelete();
			} else {
				if (npc instanceof Npc) {
					((Npc) npc).setDespawnDelayed(true);
				} else {
					npc.getController().onDelete();
				}
			}
		}
	}
	
	public void despawnAll() {
		for (int key: spawns.keys()) {
			VisibleObject npc = spawns.remove(key);
			if (npc != null) npc.getController().onDelete();
		}
	}
}

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
package com.aionemu.gameserver.spawnengine;

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.TemporarySpawn;

import javolution.util.FastList;

/**
 * @author xTz
 * @modified Yon (Aion Reconstruction Project) -- {@link #spawnAll()} modified to call {@link #despawn()}
 * for admins using the //time command, reworked to use {@link SpawnGroupHolder} within the internal data structure.
 */
public class TemporarySpawnEngine {
	
	/**
	 * Holds a SpawnGroup along with the instance ID it was spawned in.
	 * <p>
	 * This is just the quick and dirty method of keeping track of this information within
	 * the list structure being used for these temporary spawns.
	 * 
	 * @author Yon (Aion Reconstruction Project)
	 */
	private static class SpawnGroupHolder {
		
		final SpawnGroup2 spawnGroup;
		final int instanceId;
		
		SpawnGroupHolder(SpawnGroup2 spawnGroup, int instanceId) {
			this.spawnGroup = spawnGroup;
			this.instanceId = instanceId;
		}
		
	}
	
	//FIXME: Replace with a structure that allows for rapid removal, so we can use temporary spawns in Instance maps.
	private static final FastList<SpawnGroupHolder> temporarySpawns = new FastList<SpawnGroupHolder>();
//	private static final FastMap<SpawnGroup2, HashSet<Integer>> tempSpawnInstanceMap = new FastMap<SpawnGroup2, HashSet<Integer>>();
	
	public static void spawnAll() {
		despawn();
		spawn(true);
	}
	
	public static void onHourChange() {
		despawn();
		spawn(false);
	}
	
	private static void despawn() {
		for (SpawnGroupHolder spawnH : temporarySpawns) {
			SpawnGroup2 spawn = spawnH.spawnGroup;
			for (SpawnTemplate template : spawn.getSpawnTemplates()) {
				if (template.getTemporarySpawn().canDespawn()) {
					VisibleObject object = template.getVisibleObject(spawnH.instanceId);
					if (object == null) {
						continue;
					}
					if (object instanceof Npc) {
						Npc npc = (Npc) object;
						if (!npc.getLifeStats().isAlreadyDead() && template.hasPool()) {
							spawn.setTemplateUse(npc.getInstanceId(), template, false);
						}
						npc.getController().cancelTask(TaskId.RESPAWN);
					}
					if (object.isSpawned()) {
						object.getController().onDelete();
					}
				}
			}
		}
	}
	
	private static void spawn(boolean startCheck) {
		for (SpawnGroupHolder spawnH : temporarySpawns) {
			SpawnGroup2 spawn = spawnH.spawnGroup;
			int instanceId = spawnH.instanceId;
//			HashSet<Integer> instances = tempSpawnInstanceMap.get(spawn);
			if (spawn.hasPool()) {
				TemporarySpawn temporarySpawn = spawn.geTemporarySpawn();
				if (temporarySpawn.canSpawn() || (startCheck && spawn.getRespawnTime() != 0 && temporarySpawn.isInSpawnTime())) {
//					for (Integer instanceId : instances) {
						spawn.resetTemplates(instanceId);
						for (int pool = 0; pool < spawn.getPool(); pool++) {
							SpawnTemplate template = spawn.getRndTemplate(instanceId);
							SpawnEngine.spawnObject(template, instanceId);
						}
//					}
				}
			} else {
				for (SpawnTemplate template : spawn.getSpawnTemplates()) {
					TemporarySpawn temporarySpawn = template.getTemporarySpawn();
					if (temporarySpawn.canSpawn() || (startCheck && !template.isNoRespawn() && temporarySpawn.isInSpawnTime())) {
//						for (Integer instanceId : instances) {
							SpawnEngine.spawnObject(template, instanceId);
//						}
					}
				}
			}
		}
	}
	
	/**
	 * @param spawnTemplate
	 */
	public static void addSpawnGroup(SpawnGroup2 spawn, int instanceId) {
		temporarySpawns.add(new SpawnGroupHolder(spawn, instanceId));
//		HashSet<Integer> instances = tempSpawnInstanceMap.get(spawn);
//		if (instances == null) {
//			instances = new HashSet<Integer>();
//			tempSpawnInstanceMap.put(spawn, instances);
//		}
//		instances.add(instanceId);
	}
}

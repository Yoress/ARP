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
package com.aionemu.gameserver.services;

import java.util.Set;
import java.util.concurrent.Future;

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * @author ATracer, Source, xTz
 * @modified Yon (Aion Reconstruction Project) -- {@link #scheduleRespawnTask(VisibleObject)}
 * now sets the respawn task of Creature controllers instead of trying to cancel the contoller's
 * task upon running the {@link RespawnTask#run() RespawnTask}.
 */
public class RespawnService {
	
	private static final int IMMEDIATE_DECAY = 5 * 1000;
	private static final int WITHOUT_DROP_DECAY = (int) (1.5 * 60 * 1000);
	private static final int WITH_DROP_DECAY = 5 * 60 * 1000;
	
	/**
	 * @param npc
	 * @return Future<?>
	 */
	public static Future<?> scheduleDecayTask(Npc npc) {
		int decayInterval;
		Set<DropItem> drop = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		
		if (drop == null) {
			decayInterval = IMMEDIATE_DECAY;
		} else if (drop.isEmpty()) {
			decayInterval = WITHOUT_DROP_DECAY;
		} else {
			decayInterval = WITH_DROP_DECAY;
		}
		
		return scheduleDecayTask(npc, decayInterval);
	}
	
	public static Future<?> scheduleDecayTask(Npc npc, long decayInterval) {
		return ThreadPoolManager.getInstance().schedule(new DecayTask(npc.getObjectId()), decayInterval);
	}
	
	/**
	 * @param visibleObject
	 */
	public static final Future<?> scheduleRespawnTask(VisibleObject visibleObject) {
		final int interval = visibleObject.getSpawn().getRespawnTime();
		SpawnTemplate spawnTemplate = visibleObject.getSpawn();
		int instanceId = visibleObject.getInstanceId();
		if (visibleObject instanceof Creature) {
			((Creature) visibleObject).getController()
					.addTask(TaskId.RESPAWN, ThreadPoolManager.getInstance().schedule(new RespawnTask(spawnTemplate, instanceId), interval * 1000));
			return ((Creature) visibleObject).getController().getTask(TaskId.RESPAWN);
		}
		return ThreadPoolManager.getInstance().schedule(new RespawnTask(spawnTemplate, instanceId), interval * 1000);
	}
	
	/**
	 * @param spawnTemplate
	 * @param instanceId
	 */
	private static final VisibleObject respawn(SpawnTemplate spawnTemplate, final int instanceId) {
		if (spawnTemplate.isTemporarySpawn() && !spawnTemplate.getTemporarySpawn().canSpawn() && !spawnTemplate.getTemporarySpawn().isInSpawnTime()) {
			return null;
		}
		
		int worldId = spawnTemplate.getWorldId();
		boolean instanceExists = InstanceService.isInstanceExist(worldId, instanceId);
		if (spawnTemplate.isNoRespawn() || !instanceExists) {
			return null;
		}
		
		if (spawnTemplate.hasPool()) {
			spawnTemplate = spawnTemplate.changeTemplate(instanceId);
		}
		return SpawnEngine.spawnObject(spawnTemplate, instanceId);
	}
	
	private static class DecayTask implements Runnable {
		
		private final int npcId;
		
		DecayTask(int npcId) {
			this.npcId = npcId;
		}
		
		@Override
		public void run() {
			VisibleObject visibleObject = World.getInstance().findVisibleObject(npcId);
			if (visibleObject != null) {
				visibleObject.getController().onDelete();
			}
		}
	}
	
	private static class RespawnTask implements Runnable {
		
		private final SpawnTemplate spawn;
		private final int instanceId;
		
		RespawnTask(SpawnTemplate spawn, int instanceId) {
			this.spawn = spawn;
			this.instanceId = instanceId;
		}
		
		@Override
		public void run() {
			//Not needed anymore, moved to {@link #scheduleRespawnTask(VisibleObject)}
//			VisibleObject visibleObject = spawn.getVisibleObject();
//			if (visibleObject != null && visibleObject instanceof Npc) {
//				((Npc) visibleObject).getController().cancelTask(TaskId.RESPAWN);
//			}
			respawn(spawn, instanceId);
		}
	}
}

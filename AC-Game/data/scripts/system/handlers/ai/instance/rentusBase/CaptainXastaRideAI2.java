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
package ai.instance.rentusBase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.ai.AggressiveNpcAI2;
import com.aionemu.gameserver.model.actions.PlayerActions;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * 
 * @author Ranastic
 *
 */

@AIName("captainxastaride")
public class CaptainXastaRideAI2 extends AggressiveNpcAI2 {
	
	private Future<?> phaseTask;
	private boolean canThink = true;
	private AtomicBoolean isAggred = new AtomicBoolean(false);
	private AtomicBoolean isStartedEvent = new AtomicBoolean(false);
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isAggred.compareAndSet(false, true)) {
			sendMsg(1500388);
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	private void checkPercentage(int hpPercentage) {
		if (hpPercentage <= 90) {
			if (isStartedEvent.compareAndSet(false, true)) {
				startPhaseTask();
			}
		}
		if (hpPercentage <= 70) {
			if (isStartedEvent.compareAndSet(false, true)) {
				startPhaseTask();
			}
		}
		if (hpPercentage <= 50) {
			if (isStartedEvent.compareAndSet(false, true)) {
				startPhaseTask();
			}
		}
		if (hpPercentage <= 30) {
			if (isStartedEvent.compareAndSet(false, true)) {
				startPhaseTask();
			}
		}
		if (hpPercentage <= 10) {
			if (isStartedEvent.compareAndSet(false, true)) {
				startPhaseTask();
			}
		}
	}
	
	private void startPhaseTask() {
		phaseTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelPhaseTask();
				} else {
					SkillEngine.getInstance().getSkill(getOwner(), 19657, 10, getOwner()).useNoAnimationSkill(); // Dragon
																												 // Breath.
					sendMsg(1500389);
					List<Player> players = getLifedPlayers();
					if (!players.isEmpty()) {
						int size = players.size();
						if (players.size() < 6) {
							for (Player p : players) {
								spawnXastaTrap(p);
							}
						} else {
							int count = Rnd.get(1, size);
							for (int i = 0; i < count; i++) {
								if (players.isEmpty()) {
									break;
								}
								spawnXastaTrap(players.get(Rnd.get(players.size())));
							}
						}
					}
				}
			}
		}, 20000, 40000);
	}
	
	private void spawnXastaTrap(Player player) {
		final float x = player.getX();
		final float y = player.getY();
		final float z = player.getZ();
		if (x > 0 && y > 0 && z > 0) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				
				@Override
				public void run() {
					if (!isAlreadyDead()) {
						spawn(282444, x, y, z, (byte) 0); // Xasta's Trap.
					}
				}
			}, 3000);
		}
	}
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	private List<Player> getLifedPlayers() {
		List<Player> players = new ArrayList<Player>();
		for (Player player : getKnownList().getKnownPlayers().values()) {
			if (!PlayerActions.isAlreadyDead(player)) {
				players.add(player);
			}
		}
		return players;
	}
	
	private void cancelPhaseTask() {
		if (phaseTask != null && !phaseTask.isDone()) {
			phaseTask.cancel(true);
		}
	}
	
	private void deleteHelpers() {
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		if (instance != null) {
			deleteNpcs(instance.getNpcs(282444)); // Xasta's Trap.
		}
	}
	
	@Override
	protected void handleDied() {
		cancelPhaseTask();
		if (getNpcId() == 217309) { // Captain Xasta Ride.
			spawn(217310, 238.160f, 598.624f, 178.480f, (byte) 0); // Captain
																	 // Xasta
																	 // Stand Up.
			deleteHelpers();
			AI2Actions.deleteOwner(this);
		}
		super.handleDied();
	}
	
	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc : npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
	}
	
	private void sendMsg(int msg) {
		NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
	}
	
	@Override
	protected void handleDespawned() {
		cancelPhaseTask();
		super.handleDespawned();
	}
	
	@Override
	protected void handleBackHome() {
		canThink = true;
		cancelPhaseTask();
		deleteHelpers();
		isAggred.set(false);
		isStartedEvent.set(false);
		super.handleBackHome();
	}
}

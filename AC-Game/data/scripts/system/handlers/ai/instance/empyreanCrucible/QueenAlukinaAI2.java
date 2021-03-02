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
package ai.instance.empyreanCrucible;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.ai.AggressiveNpcAI2;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.SkillEngine;

/**
 * @author Luzien
 */
@AIName("alukina_emp")
public class QueenAlukinaAI2 extends AggressiveNpcAI2 {
	
	private List<Integer> percents = new ArrayList<Integer>();
	private Future<?> task;
	
	@Override
	public void handleSpawned() {
		super.handleSpawned();
		addPercents();
	}
	
	@Override
	public void handleDespawned() {
		cancelTask();
		percents.clear();
		super.handleDespawned();
	}
	
	@Override
	public void handleDied() {
		cancelTask();
		super.handleDied();
	}
	
	@Override
	public void handleBackHome() {
		addPercents();
		cancelTask();
		super.handleBackHome();
	}
	
	@Override
	public void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	private void startEvent(int percent) {
		
		SkillEngine.getInstance().getSkill(getOwner(), 17899, 41, getTarget()).useNoAnimationSkill();
		
		switch (percent) {
		case 75:
			scheduleSkill(17900, 4500);
			NpcShoutsService.getInstance().sendMsg(getOwner(), 340487, getObjectId(), 0, 10000);
			scheduleSkill(17899, 14000);
			scheduleSkill(17900, 18000);
			break;
		case 50:
			scheduleSkill(17280, 4500);
			scheduleSkill(17902, 8000);
			break;
		case 25:
			task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
				
				@Override
				public void run() {
					if (isAlreadyDead()) {
						cancelTask();
					} else {
						SkillEngine.getInstance().getSkill(getOwner(), 17901, 41, getTarget()).useNoAnimationSkill();
						scheduleSkill(17902, 5500);
						scheduleSkill(17902, 7500);
					}
				}
			}, 4500, 20000);
			break;
		}
	}
	
	private void cancelTask() {
		if (task != null && !task.isCancelled()) {
			task.cancel(true);
		}
	}
	
	private void scheduleSkill(final int skillId, int delay) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			
			@Override
			public void run() {
				if (!isAlreadyDead()) {
					SkillEngine.getInstance().getSkill(getOwner(), skillId, 41, getTarget()).useNoAnimationSkill();
					
				}
			}
		}, delay);
	}
	
	private void checkPercentage(int percentage) {
		for (Integer percent : percents) {
			if (percentage <= percent) {
				percents.remove(percent);
				startEvent(percent);
				break;
			}
		}
	}
	
	private void addPercents() {
		percents.clear();
		Collections.addAll(percents, new Integer[] {75, 50, 25});
	}
}

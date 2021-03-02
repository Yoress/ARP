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
package ai.worlds.tiamaranta;

import java.util.concurrent.Future;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.ai.AggressiveNpcAI2;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * @author xTz
 */
@AIName("golden_tatar_paralysis_eye")
public class GoldenTatarParalysisEyeAI2 extends AggressiveNpcAI2 {
	
	private Future<?> task;
	private int spawnCount;
	
	@Override
	public boolean canThink() {
		return false;
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		if (getNpcId() == 282744) {
			startSpawnTask();
		} else {
			SkillEngine.getInstance().getSkill(getOwner(), 20213, 60, getOwner()).useNoAnimationSkill();
			task = ThreadPoolManager.getInstance().schedule(new Runnable() {
				
				@Override
				public void run() {
					if (!isAlreadyDead()) {
						AI2Actions.deleteOwner(GoldenTatarParalysisEyeAI2.this);
					}
				}
			}, 2000);
			
		}
	}
	
	private void startSpawnTask() {
		task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelTask();
				} else {
					spawnCount++;
					WorldPosition p = getPosition();
					spawn(282745, p.getX(), p.getY(), p.getZ(), p.getHeading());
					if (spawnCount >= 10) {
						cancelTask();
						AI2Actions.deleteOwner(GoldenTatarParalysisEyeAI2.this);
					}
				}
			}
		}, 14000, 2000);
	}
	
	private void cancelTask() {
		if (task != null && !task.isDone()) {
			task.cancel(true);
		}
	}
	
	@Override
	public AIAnswer ask(AIQuestion question) {
		switch (question) {
		case CAN_ATTACK_PLAYER:
			return AIAnswers.POSITIVE;
		case CAN_RESIST_ABNORMAL:
			return AIAnswers.POSITIVE;
		default:
			return AIAnswers.NEGATIVE;
		}
	}
	
	@Override
	protected void handleDespawned() {
		cancelTask();
		super.handleDespawned();
	}
	
	@Override
	protected void handleDied() {
		cancelTask();
		super.handleDied();
		AI2Actions.deleteOwner(this);
	}
}

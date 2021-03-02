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
package ai.worlds.tiamaranta.ativasCristalline;

import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.ai.AggressiveNpcAI2;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * @author Ritsu
 */
@AIName("ativascristalline")
public class AtivasCristallineAI2 extends AggressiveNpcAI2 {
	
	private AtomicBoolean isStart90Event = new AtomicBoolean(false);
	private AtomicBoolean isStart60Event = new AtomicBoolean(false);
	private AtomicBoolean isStart30Event = new AtomicBoolean(false);
	private AtomicBoolean isStart10Event = new AtomicBoolean(false);
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	@Override
	protected void handleBackHome() {
		isStart90Event.set(false);
		isStart60Event.set(false);
		isStart30Event.set(false);
		isStart10Event.set(false);
		super.handleBackHome();
	}
	
	private void checkPercentage(int hpPercentage) {
		if (hpPercentage <= 90) {
			if (isStart90Event.compareAndSet(false, true)) {
				topazKomad();
			}
		} else if (hpPercentage <= 60) {
			if (isStart60Event.compareAndSet(false, true)) {
				garnetKomad();
			}
		} else if (hpPercentage <= 30) {
			if (isStart30Event.compareAndSet(false, true)) {
				topazKomad();
			}
		} else if (hpPercentage <= 10) {
			if (isStart10Event.compareAndSet(false, true)) {
				garnetKomad();
			}
		}
	}
	
	private void garnetKomad() {
		if (getPosition().isSpawned() && !isAlreadyDead()) {
			for (int i = 0; i < 1; i++) {
				int distance = Rnd.get(3, 5);
				int nrNpc = Rnd.get(1, 0);
				switch (nrNpc) {
				case 1:
					nrNpc = 282708; // Garnet Komad.
					break;
				}
				rndSpawnInRange(nrNpc, distance);
			}
		}
	}
	
	private void topazKomad() {
		if (getPosition().isSpawned() && !isAlreadyDead()) {
			for (int i = 0; i < 1; i++) {
				int distance = Rnd.get(3, 5);
				int nrNpc = Rnd.get(1, 0);
				switch (nrNpc) {
				case 1:
					nrNpc = 282709; // Topaz Komad.
					break;
				}
				rndSpawnInRange(nrNpc, distance);
			}
		}
	}
	
	private void rndSpawnInRange(int npcId, float distance) {
		float direction = Rnd.get(0, 199) / 100f;
		float x1 = (float) (Math.cos(Math.PI * direction) * distance);
		float y1 = (float) (Math.sin(Math.PI * direction) * distance);
		spawn(npcId, getPosition().getX() + x1, getPosition().getY() + y1, getPosition().getZ(), (byte) 0);
	}
}

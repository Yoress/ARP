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
package com.aionemu.gameserver.ai2.ai;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * @author ATracer
 * @modified Kashim
 * @Reworked Kill3r
 * @reworked Yon (Aion Reconstruction Project) -- Uncommented {@link #handleCustomEvent(int, Object...)} and tweaked it for safety,
 * changed {@link #tryActivateTrap(Creature)} to use {@link #trapRange} if available, and set it to protected synchronized,
 * made {@link #EVENT_SET_TRAP_RANGE} final. Added methods for subclasses to get trapRange, and schedule deletion.
 */
@AIName("trap")
public class TrapNpcAI2 extends NpcAI2 {
	
	public static final int EVENT_SET_TRAP_RANGE = 1;
	private int trapRange = -1;
	
	@Override
	protected void handleCreatureMoved(Creature creature) {
		tryActivateTrap(creature);
	}
	
	@Override
	protected void handleSpawned() {
		getKnownList().doUpdate();
		getKnownList().doOnAllObjects(new Visitor<VisibleObject>() {
			
			@Override
			public void visit(VisibleObject object) {
				if (!(object instanceof Creature)) {
					return;
				}
				Creature creature = (Creature) object;
				tryActivateTrap(creature);
			}
		});
		super.handleSpawned();
	}
	
	protected synchronized void tryActivateTrap(Creature creature) {
		if (trapRange == -2) {
			//If we are scheduled for deletion, do nothing. This is technically redundant, but I'm leaving it.
			return;
		}
		int time = 1000;
		if (this.getNpcId() == 749248 || this.getNpcId() == 749249) {
			if (setStateIfNot(AIState.FIGHT)) {
				AI2Actions.targetCreature(this, creature);
				AI2Actions.useSkill(this, getSkillList().getNextSkill().getSkillId());
				ThreadPoolManager.getInstance().schedule(new TrapDelete(this), 4500);
			}
		}
		if (!creature.getLifeStats().isAlreadyDead() && isInRange(creature, ((trapRange == -1) ? (getOwner().getAggroRange() + 2) : (trapRange)))) {
			
			Creature creator = (Creature) getCreator();
			if (!creator.isEnemy(creature)) {
				return;
			}
			
//			int npcId = this.getNpcId();
			
			if (this.getNpcId() == 749248 || this.getNpcId() == 749249 || this.getNpcId() == 749250 || this.getNpcId() == 749251) {
				time = 5000;
			}
			
			if (setStateIfNot(AIState.FIGHT)) {
				AI2Actions.targetCreature(this, creature);
				AI2Actions.useSkill(this, getSkillList().getNextSkill().getSkillId());
				ThreadPoolManager.getInstance().schedule(new TrapDelete(this), time);
			}
		}
	}
	
	@Override
	protected void handleCustomEvent(int eventId, Object... args) {
		try {
			if (eventId == EVENT_SET_TRAP_RANGE) {
				trapRange = (Integer) args[0];
			}
		} catch (ClassCastException e) {
			//TODO: Move into logger for something
			System.err.println("Incorrect use of " + this.getClass().getSimpleName() + "#handleCustomEvent(int, Object...)");
		}
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
	
	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
		case SHOULD_DECAY:
			return AIAnswers.NEGATIVE;
		case SHOULD_RESPAWN:
			return AIAnswers.NEGATIVE;
		case SHOULD_REWARD:
			return AIAnswers.NEGATIVE;
		default:
			return null;
		}
	}
	

	/**
	 * This method is for subclasses to access {@link TrapDelete TrapDelete Objects}.
	 * 
	 * @param timeMs -- The time, in milliseconds, to schedule the deletion for.
	 */
	protected final void scheduleForDeletion(int timeMs) {
		ThreadPoolManager.getInstance().schedule(new TrapDelete(this), timeMs);
	}
	
	/**
	 * This method is for subclasses to check if this AI has been scheduled for deletion via
	 * {@link TrapDelete}.
	 * 
	 * @return true if this trap has been scheduled for deletion, false otherwise.
	 */
	protected final boolean isScheduledForDeletion() {
		return trapRange == -2;
	}
	
	/**
	 * This method is for subclasses to access the {@link #trapRange} field if it's available.
	 * 
	 * @return {@link #trapRange} if available, otherwise the aggro range of this AI's owner + 2.
	 */
	protected final int getTrapRange() {
		return ((trapRange == -1) ? (getOwner().getAggroRange() + 2) : (trapRange));
	}
	
	/**
	 * @modified Yon (Aion Reconstruction Project) -- added change to trapRange of calling AI to -2
	 * for simple checking in {@link TrapNpcAI2#tryActivateTrap(Creature)} if the Trap is scheduled
	 * for deletion.
	 */
	private static final class TrapDelete implements Runnable {
		
		private TrapNpcAI2 ai;
		
		TrapDelete(TrapNpcAI2 ai) {
			this.ai = ai;
			ai.trapRange = -2;
		}
		
		@Override
		public void run() {
			AI2Actions.deleteOwner(ai);
			ai = null;
		}
	}
}

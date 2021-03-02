package com.aionemu.gameserver.ai2.ai.instance.fire_temple;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AttackIntention;
import com.aionemu.gameserver.ai2.ai.AggressiveNpcAI2;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * This AI is intended for use by Broken Wing Kutisen within the Fire Temple.
 * <p>
 * This AI provides access to simple mechanics using the following skills:
 * <BLOCKQUOTE><table border=1>
 * <col width="60%"/>
 * <col width="20%"/>
 * <thead>
 *   <tr><th Align="left">Skill Name</th><th Align="left">Skill ID</th></tr>
 * </thead>
 * <tbody>
 *   <tr><td>Snare</td><td>16981</td></tr>
 *   <tr><td>Mortal Blow</td><td>17038</td></tr>
 *   <tr><td>Powerful Will</td><td>17051</td></tr>
 * </tbody>
 * </table></BLOCKQUOTE>
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@AIName("ftkutisen")
public class BrokenWingKutisenAI2 extends AggressiveNpcAI2 {
	//TODO: Target self for chain -- bear in mind that if he can't use a skill, he should target the players and auto attack
	int phase = 0;
	
	final static int SNARE = 16981,
					 MORTAL_BLOW = 17038,
					 POWERFUL_WILL = 17051;
	
	long lastCycle = System.currentTimeMillis();
	
	private boolean isReadyForNextCycle() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastCycle > 25000) {
			lastCycle = currentTime;
			return true;
		}
		return false;
	}
	
	@Override
	protected void handleRespawned() {
		phase = 0;
		super.handleRespawned();
	}
	
	@Override
	protected void handleDied() {
		phase = 0;
		super.handleDied();
	}
	
	@Override
	protected void handleBackHome() {
		phase = 0;
		super.handleBackHome();
	}
	
	@Override
	protected void handleAttackComplete() {
		if (skillId == SNARE) {
			if (phase == 0) phase = 1;
			if (phase == 1) phase = 2;
		}
		if (skillId == POWERFUL_WILL) {
			phase = 3;
		}
		if (skillId == MORTAL_BLOW) {
			phase = 1;
		}
		super.handleAttackComplete();
	}
	
	@Override
	protected void handleSkillAttackIntention(int delay, Creature creature) {
		if (skillId == MORTAL_BLOW) {
			if (delay == 0) AI2Actions.targetSelf(this);
			super.handleSkillAttackIntention(delay, getOwner());
		} else {
			super.handleSkillAttackIntention(delay, creature);
		}
	}
	
	@Override
	protected void handleSkillBuffIntention(int delay, Creature creature) {
		if (delay == 0) AI2Actions.targetSelf(this);
		super.handleSkillBuffIntention(delay, getOwner());
	}
	
	@Override
	public AttackIntention chooseAttackIntention(Creature creature) {
		if (getTarget() == null || getAggroList().getMostHated() == null || ((Creature) getTarget()).getLifeStats().isAlreadyDead()) {
			return AttackIntention.FINISH_ATTACK;
		}
		
		if (phase == 0) {
			if (getSkillList().isSkillPresent(SNARE)) {
				skillId = SNARE;
				skillLevel = getSkillList().getSkillLevel(SNARE);
				return AttackIntention.SKILL_ATTACK;
			} else {
				phase = 1;
			}
		} else if (getOwner().getLifeStats().getHpPercentage() < 90 && (phase != 1 || isReadyForNextCycle())) {
			if (phase == 1 && getSkillList().isSkillPresent(SNARE)) {
				skillId = SNARE;
				skillLevel = getSkillList().getSkillLevel(SNARE);
				return AttackIntention.SKILL_ATTACK;
			} else {
				if (phase == 1) phase = 2;
			}
			
			if (phase == 2 && getSkillList().isSkillPresent(POWERFUL_WILL)) {
				skillId = POWERFUL_WILL;
				skillLevel = getSkillList().getSkillLevel(POWERFUL_WILL);
				return AttackIntention.SKILL_BUFF;
			} else {
				if (phase == 2) phase = 3;
			}
			
			if (phase == 3 && getSkillList().isSkillPresent(MORTAL_BLOW)) {
				skillId = MORTAL_BLOW;
				skillLevel = getSkillList().getSkillLevel(MORTAL_BLOW);
				return AttackIntention.SKILL_ATTACK;
			} else {
				phase = 1;
			}
		}
		
		skillId = 0;
		skillLevel = 0;
		return AttackIntention.SIMPLE_ATTACK;
	}
}

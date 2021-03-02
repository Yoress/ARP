package com.aionemu.gameserver.ai2.ai.instance.fire_temple;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AttackIntention;
import com.aionemu.gameserver.ai2.ai.AggressiveNpcAI2;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * This AI is intended for use by Chalice Guard Denlavis within the Fire Temple.
 * <p>
 * This AI provides access to mechanics using the following skills:
 * <BLOCKQUOTE><table border=1>
 * <col width="60%"/>
 * <col width="20%"/>
 * <thead>
 *   <tr><th Align="left">Skill Name</th><th Align="left">Skill ID</th></tr>
 * </thead>
 * <tbody>
 *   <tr><td>Jab</td><td>16837</td></tr>
 *   <tr><td>Wide Successive Strike</td><td>17048</td></tr>
 *   <tr><td>Ranged Triple Strikes</td><td>17049</td></tr>
 *   <tr><td>Blessing of Rock</td><td>17052</td></tr>
 *   <tr><td>Last Struggle</td><td>17054</td></tr>
 * </tbody>
 * </table></BLOCKQUOTE>
 * <p>
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@AIName("ftchaliceguard")
public class ChaliceGuardDenlavisAI2 extends AggressiveNpcAI2 {
	//TODO: decide if he should reapply Blessing of Rock and make him do so
	//TODO: Make him target himself for the entire chain -- unless he can't use skills
	
	int phase = 0;
	
	final static int JAB = 16837,
					 WIDE_SUCCESSIVE_STRIKE = 17048,
					 RANGED_TRIPLE_STRIKES = 17049,
					 BLESSING_OF_ROCK = 17052,
					 LAST_STRUGGLE = 17054;
	
	@Override
	protected void handleSpawned() {
		phase = 0;
		applyBlessing();
		super.handleSpawned();
	}
	
	@Override
	protected void handleRespawned() {
		phase = 0;
		applyBlessing();
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
		applyBlessing();
		super.handleBackHome();
	}
	
	private void applyBlessing() {
		if (getSkillList().isSkillPresent(BLESSING_OF_ROCK) && !getOwner().getEffectController().isAbnormalPresentBySkillId(BLESSING_OF_ROCK)) {
			EmoteManager.emoteStartAttacking(getOwner());
			AI2Actions.targetSelf(this);
			AI2Actions.useSkill(this, BLESSING_OF_ROCK, getSkillList().getSkillLevel(BLESSING_OF_ROCK));
			AI2Actions.targetCreature(this, null);
			EmoteManager.emoteStopAttacking(getOwner());
			setStateIfNot(AIState.IDLE);
		}
	}
	
	@Override
	protected void handleAttackComplete() {
		if (skillId == JAB) {
			if (phase == 0) phase = 1;
			if (phase == 2) phase = 3;
			if (phase == 5) phase = 6;
		}
		if (skillId == WIDE_SUCCESSIVE_STRIKE) {
			if (phase == 1) phase = 2;
			if (phase == 3) phase = 4;
			if (phase == 6) phase = 7;
		}
		if (skillId == LAST_STRUGGLE) {
			if (phase == 4) phase = 5;
		}
		if (skillId == RANGED_TRIPLE_STRIKES) {
			if (phase == 7) phase = 8;
		}
		super.handleAttackComplete();
	}
	
	@Override
	protected void handleSkillAttackIntention(int delay, Creature creature) {
		if (delay == 0) AI2Actions.targetSelf(this);
		super.handleSkillAttackIntention(delay, getOwner());
	}
	
	@Override
	public AttackIntention chooseAttackIntention(Creature creature) {
		if (getTarget() == null || getAggroList().getMostHated() == null || ((Creature) getTarget()).getLifeStats().isAlreadyDead()) {
			return AttackIntention.FINISH_ATTACK;
		}
		
		int hp = getOwner().getLifeStats().getHpPercentage();
		switch (phase) {
		case 0:
			if (getSkillList().isSkillPresent(JAB)) {
				skillId = JAB;
				skillLevel = getSkillList().getSkillLevel(JAB);
				return AttackIntention.SKILL_ATTACK;
			} else {
				phase = 1;
			}
			break;
		case 2:
			if (getSkillList().isSkillPresent(JAB) && hp < 50) {
				skillId = JAB;
				skillLevel = getSkillList().getSkillLevel(JAB);
				return AttackIntention.SKILL_ATTACK;
			} else {
				if (hp < 50) phase = 3;
			}
			break;
		case 5:
			if (getSkillList().isSkillPresent(JAB) && hp < 35) {
				skillId = JAB;
				skillLevel = getSkillList().getSkillLevel(JAB);
				return AttackIntention.SKILL_ATTACK;
			} else {
				if (hp < 35) phase = 6;
			}
			break;
		case 1:
			if (getSkillList().isSkillPresent(WIDE_SUCCESSIVE_STRIKE)) {
				skillId = WIDE_SUCCESSIVE_STRIKE;
				skillLevel = getSkillList().getSkillLevel(WIDE_SUCCESSIVE_STRIKE);
				return AttackIntention.SKILL_ATTACK;
			} else {
				phase = 2;
			}
			break;
		case 3:
			if (getSkillList().isSkillPresent(WIDE_SUCCESSIVE_STRIKE)) {
				skillId = WIDE_SUCCESSIVE_STRIKE;
				skillLevel = getSkillList().getSkillLevel(WIDE_SUCCESSIVE_STRIKE);
				return AttackIntention.SKILL_ATTACK;
			} else {
				phase = 4;
			}
			break;
		case 6:
			if (getSkillList().isSkillPresent(WIDE_SUCCESSIVE_STRIKE)) {
				skillId = WIDE_SUCCESSIVE_STRIKE;
				skillLevel = getSkillList().getSkillLevel(WIDE_SUCCESSIVE_STRIKE);
				return AttackIntention.SKILL_ATTACK;
			} else {
				phase = 7;
			}
			break;
		case 4:
			if (getSkillList().isSkillPresent(LAST_STRUGGLE)) {
				skillId = LAST_STRUGGLE;
				skillLevel = getSkillList().getSkillLevel(LAST_STRUGGLE);
				return AttackIntention.SKILL_ATTACK;
			} else {
				phase = 5;
			}
			break;
		case 7:
			if (getSkillList().isSkillPresent(RANGED_TRIPLE_STRIKES)) {
				skillId = RANGED_TRIPLE_STRIKES;
				skillLevel = getSkillList().getSkillLevel(RANGED_TRIPLE_STRIKES);
				return AttackIntention.SKILL_ATTACK;
			} else {
				phase = 8;
			}
			break;
		}
		
		skillId = 0;
		skillLevel = 0;
		return AttackIntention.SIMPLE_ATTACK;
	}
	
}

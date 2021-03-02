package com.aionemu.gameserver.ai2.ai.instance.fire_temple;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AttackIntention;
import com.aionemu.gameserver.ai2.ai.AggressiveNpcAI2;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * This AI is intended for use by the Temple Caryatid mobs within Fire Temple. It may be moved into the open world AI list
 * in the future.
 * <p>
 * This AI provides access to simple mechanics using the following skills:
 * <BLOCKQUOTE><table border=1>
 * <col width="60%"/>
 * <col width="20%"/>
 * <thead>
 *   <tr><th Align="left">Skill Name</th><th Align="left">Skill ID</th></tr>
 * </thead>
 * <tbody>
 *   <tr><td>Reflect</td><td>16699</td></tr>
 *   <tr><td>Flame</td><td>16909</td></tr>
 * </tbody>
 * </table></BLOCKQUOTE>
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@AIName("ftcaryatid")
public class CaryatidAI2 extends AggressiveNpcAI2 {

	int phase = 0;
	
	final static int REFLECT = 16699,
					 FLAME = 16909;
	
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
		if (skillId == FLAME) phase = 1;
		if (skillId == REFLECT) phase = 2;
		super.handleAttackComplete();
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
		
		if (getOwner().getGameStats().canUseNextSkill()) {
			int hp = getOwner().getLifeStats().getHpPercentage();
			if (phase == 0 && getSkillList().isSkillPresent(FLAME) && hp > 50) {
				skillId = FLAME;
				skillLevel = getSkillList().getSkillLevel(FLAME);
				return AttackIntention.SKILL_ATTACK;
			}
			
			if (phase == 0) phase = 1;
			
			if (phase == 1 && getSkillList().isSkillPresent(REFLECT) && hp < 40) {
				skillId = REFLECT;
				skillLevel = getSkillList().getSkillLevel(REFLECT);
				return AttackIntention.SKILL_BUFF;
			}
		}
		
		return AttackIntention.SIMPLE_ATTACK;
	}
	
}

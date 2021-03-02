/**
 * This file is part of the Aion Reconstruction Project Server.
 *
 * The Aion Reconstruction Project Server is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * The Aion Reconstruction Project Server is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with the Aion Reconstruction Project Server. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * @AionReconstructionProjectTeam
 */
package com.aionemu.gameserver.ai2.ai.instance.fire_temple;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AttackIntention;
import com.aionemu.gameserver.ai2.ai.AggressiveNpcAI2;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * This AI is intended for use by the large Fire Spirit mobs within Fire Temple. It may be moved into the open world AI list
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
 *   <tr><td>Temporarily Iron-Clad</td><td>16522</td></tr>
 *   <tr><td>Flame Attack</td><td>16592</td></tr>
 *   <tr><td>Strike Heat</td><td>16723</td></tr>
 * </tbody>
 * </table></BLOCKQUOTE>
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@AIName("ftlargefirespirit")
public class InfernoFireSpiritAI2 extends AggressiveNpcAI2 {
	
	int phase = 0;
	
	final static int TEMPORARILY_IRON_CLAD = 16522,
					 FLAME_ATTACK = 16592,
					 STRIKE_HEAT = 16723;
	
	@Override
	protected void handleDied() {
		phase = 0;
		super.handleDied();
	}
	
	@Override
	protected void handleRespawned() {
		phase = 0;
		super.handleRespawned();
	}
	
	@Override
	protected void handleBackHome() {
		phase = 0;
		super.handleBackHome();
	}
	
	@Override
	protected void handleSkillAttackIntention(int delay, Creature creature) {
		if (delay == 0 && skillId == FLAME_ATTACK) {
			AI2Actions.targetSelf(this);
		}
		if (skillId == FLAME_ATTACK) {
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
	protected void handleAttackComplete() {
		if (skillId == STRIKE_HEAT) phase = 1;
		if (skillId == TEMPORARILY_IRON_CLAD) phase = 2;
		if (skillId == FLAME_ATTACK) phase = 3;
		super.handleAttackComplete();
	}
	
	@Override
	public AttackIntention chooseAttackIntention(Creature creature) {
		if (getTarget() == null || getAggroList().getMostHated() == null || ((Creature) getTarget()).getLifeStats().isAlreadyDead()) {
			return AttackIntention.FINISH_ATTACK;
		}
		
		if (getOwner().getGameStats().canUseNextSkill()) {
			int hp = getOwner().getLifeStats().getHpPercentage();
			if (phase == 0 && getSkillList().isSkillPresent(STRIKE_HEAT) && hp > 50 && hp < 95) {
				skillId = STRIKE_HEAT;
				skillLevel = getSkillList().getSkillLevel(STRIKE_HEAT);
				return AttackIntention.SKILL_ATTACK;
			}
			if (phase == 0 && hp < 95) phase = 1;
			
			if (phase == 1 && getSkillList().isSkillPresent(TEMPORARILY_IRON_CLAD) && hp < 50 && hp > 25) {
				skillId = TEMPORARILY_IRON_CLAD;
				skillLevel = getSkillList().getSkillLevel(TEMPORARILY_IRON_CLAD);
				return AttackIntention.SKILL_BUFF;
			}
			if (phase == 1 && hp < 50) phase = 2;
			
			if (phase == 2 && getSkillList().isSkillPresent(FLAME_ATTACK)) {
				skillId = FLAME_ATTACK;
				skillLevel = getSkillList().getSkillLevel(FLAME_ATTACK);
				return AttackIntention.SKILL_ATTACK;
			}
		}
		
		skillId = 0;
		skillLevel = 0;
		return AttackIntention.SIMPLE_ATTACK;
	}
	
}

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

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AttackIntention;
import com.aionemu.gameserver.ai2.ai.AggressiveNpcAI2;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * This AI is intended for use by the red-coloured Kalgolem mobs within Fire Temple. It may be moved into the open world AI list
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
 *   <tr><td>Fire Burst</td><td>16576</td></tr>
 *   <tr><td>Fire Strike</td><td>16589</td></tr>
 * </tbody>
 * </table></BLOCKQUOTE>
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@AIName("ftredkalgolem")
public class RedKalgolemAI2 extends AggressiveNpcAI2 {

	int phase = 0;
	
	final static int FIRE_BURST = 16576,
					 FIRE_STRIKE = 16589;
	
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
	protected void handleAttackComplete() {
		if (skillId == FIRE_BURST) phase = 1;
		if (skillId == FIRE_STRIKE) phase = 2;
		super.handleAttackComplete();
	}
	
	@Override
	public AttackIntention chooseAttackIntention(Creature creature) {
		if (getTarget() == null || getAggroList().getMostHated() == null || ((Creature) getTarget()).getLifeStats().isAlreadyDead()) {
			return AttackIntention.FINISH_ATTACK;
		}
		
		if (getOwner().getGameStats().canUseNextSkill()) {
			if (phase == 0 && getSkillList().isSkillPresent(FIRE_BURST) && getOwner().getLifeStats().getHpPercentage() > 70) {
				skillId = FIRE_BURST;
				skillLevel = getSkillList().getSkillLevel(FIRE_BURST);
				return AttackIntention.SKILL_ATTACK;
			}
			
			if (phase == 0) phase = 1;
			
			if (phase == 1 && getSkillList().isSkillPresent(FIRE_STRIKE)) {
				skillId = FIRE_STRIKE;
				skillLevel = getSkillList().getSkillLevel(FIRE_STRIKE);
				return AttackIntention.SKILL_ATTACK;
			}
		}
		
		skillId = 0;
		skillLevel = 0;
		return AttackIntention.SIMPLE_ATTACK;
	}
	
}

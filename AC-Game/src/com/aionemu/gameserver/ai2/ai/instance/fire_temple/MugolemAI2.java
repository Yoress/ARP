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
 * This AI is intended for use by the Mugolem mobs within Fire Temple. It may be moved into the open world AI list
 * in the future (if Mugolem outside of Fire Temple also use the same AI).
 * <p>
 * This AI provides access to simple mechanics using the following skills:
 * <BLOCKQUOTE><table border=1>
 * <col width="60%"/>
 * <col width="20%"/>
 * <thead>
 *   <tr><th Align="left">Skill Name</th><th Align="left">Skill ID</th></tr>
 * </thead>
 * <tbody>
 *   <tr><td>Crash</td><td>16904</td></tr>
 *   <tr><td>Strong Magical Blow</td><td>16907</td></tr>
 * </tbody>
 * </table></BLOCKQUOTE>
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@AIName("ftmugolem")
public class MugolemAI2 extends AggressiveNpcAI2 {
	/**
	 * If this AI has used {@link #CRASH} or not.
	 */
	boolean crashed = false;
	
	/**
	 * The last time {@link #STRONG_MAGICAL_BLOW} was used.
	 */
	long lastStrongHit = System.currentTimeMillis();
	
	/**
	 * Skill ID available to this AI.
	 */
	final static int CRASH = 16904,
					 STRONG_MAGICAL_BLOW = 16907;
	
	@Override
	protected void handleRespawned() {
		crashed = false;
		super.handleRespawned();
	}
	
	@Override
	protected void handleDied() {
		crashed = false;
		super.handleDied();
	}
	
	@Override
	protected void handleBackHome() {
		crashed = false;
		super.handleBackHome();
	}
	
	@Override
	protected void handleAttackComplete() {
		if (skillId == CRASH) crashed = true;
		if (skillId == STRONG_MAGICAL_BLOW) lastStrongHit = System.currentTimeMillis();
		super.handleAttackComplete();
	}
	
	@Override
	public AttackIntention chooseAttackIntention(Creature creature) {
		if (getTarget() == null || getAggroList().getMostHated() == null || ((Creature) getTarget()).getLifeStats().isAlreadyDead()) {
			return AttackIntention.FINISH_ATTACK;
		}
		
		if (getOwner().getGameStats().canUseNextSkill()) {
			int hp = getOwner().getLifeStats().getHpPercentage();
			if (!crashed && getSkillList().isSkillPresent(CRASH) && hp > 60) {
				skillId = CRASH;
				skillLevel = getSkillList().getSkillLevel(CRASH);
				return AttackIntention.SKILL_ATTACK;
			}
			if (System.currentTimeMillis() - lastStrongHit > 10000 && getSkillList().isSkillPresent(STRONG_MAGICAL_BLOW)) {
				skillId = STRONG_MAGICAL_BLOW;
				skillLevel = getSkillList().getSkillLevel(STRONG_MAGICAL_BLOW);
				return AttackIntention.SKILL_ATTACK;
			}
		}
		
		skillId = 0;
		skillLevel = 0;
		return AttackIntention.SIMPLE_ATTACK;
	}
	
}

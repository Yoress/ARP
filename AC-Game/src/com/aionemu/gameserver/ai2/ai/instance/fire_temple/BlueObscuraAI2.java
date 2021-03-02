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
 * This AI is intended for use by the blue-coloured Obscura mobs within Fire Temple. It may be moved into the open world AI list
 * in the future (if Obscura outside of Fire Temple also use the same AI).
 * <p>
 * This AI provides access to simple mechanics using the following skills:
 * <BLOCKQUOTE><table border=1>
 * <col width="60%"/>
 * <col width="20%"/>
 * <thead>
 *   <tr><th Align="left">Skill Name</th><th Align="left">Skill ID</th></tr>
 * </thead>
 * <tbody>
 *   <tr><td>Blindside Attack</td><td>16556</td></tr>
 *   <tr><td>Hide Shape</td><td>16677</td></tr>
 *   <tr><td>Redirect Attack</td><td>16794</td></tr>
 * </tbody>
 * </table></BLOCKQUOTE>
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@AIName("ftblueobscura")
public class BlueObscuraAI2 extends AggressiveNpcAI2 {
	//TODO: When this AI uses #HIDE_SHAPE, it should move around while hidden.
	
	int phase = 0;
	
	final static int BLINDSIDE_ATTACK = 16556,
					 HIDE_SHAPE = 16677,
					 REDIRECT_ATTACK = 16794;
	
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
		if (skillId == REDIRECT_ATTACK) phase = 1;
		if (skillId == BLINDSIDE_ATTACK) phase = 3;
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
			if (phase == 0 && getSkillList().isSkillPresent(REDIRECT_ATTACK) && hp < 45) {
				skillId = REDIRECT_ATTACK;
				skillLevel = getSkillList().getSkillLevel(REDIRECT_ATTACK);
				return AttackIntention.SKILL_ATTACK;
			}
			
			if (phase == 1 && getSkillList().isSkillPresent(HIDE_SHAPE)) {
				phase = 2;
				skillId = HIDE_SHAPE;
				skillLevel = getSkillList().getSkillLevel(HIDE_SHAPE);
				return AttackIntention.SKILL_BUFF;
			}
			
			if (phase == 2 && getSkillList().isSkillPresent(BLINDSIDE_ATTACK)) {
				skillId = BLINDSIDE_ATTACK;
				skillLevel = getSkillList().getSkillLevel(BLINDSIDE_ATTACK);
				return AttackIntention.SKILL_ATTACK;
			}
		}
		
		skillId = 0;
		skillLevel = 0;
		return AttackIntention.SIMPLE_ATTACK;
	}
	
}

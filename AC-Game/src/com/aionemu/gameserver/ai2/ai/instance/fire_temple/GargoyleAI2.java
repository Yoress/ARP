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
 * This AI is intended for use by the Gargoyle mobs within Fire Temple. It may be moved into the open world AI list
 * in the future (if Gargoyles outside of Fire Temple also use the same AI).
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
 *   <tr><td>Enervating Feeling</td><td>16523</td></tr>
 * </tbody>
 * </table></BLOCKQUOTE>
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@AIName("ftgargoyle")
public class GargoyleAI2 extends AggressiveNpcAI2 {
	
	/**
	 * This value will be used to track which stage of the mechanics this AI is on.
	 * <p>
	 * A value of 0 means no mechanics have been used.
	 * <p>
	 * A value of 1 means this AI has used, or given up on using {@link #ENERVATING_FEELING}.
	 * <p>
	 * A value of 2 means this AI has used {@link #TEMPORARILY_IRONCLAD}.
	 */
	int phase = 0;
	
	/**
	 * Skill ID available to this AI.
	 */
	final static int ENERVATING_FEElING = 16523,
					 TEMPORARILY_IRONCLAD = 16522;
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
		if (skillId == ENERVATING_FEElING) phase = 1;
		if (skillId == TEMPORARILY_IRONCLAD) phase = 2;
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
		
		if (phase == 0 && getSkillList().isSkillPresent(ENERVATING_FEElING) && getOwner().getLifeStats().getHpPercentage() > 65) {
			skillId = ENERVATING_FEElING;
			skillLevel = getSkillList().getSkillLevel(ENERVATING_FEElING);
			return AttackIntention.SKILL_ATTACK;
		}
		
		if (phase == 0) {
			phase = 1;
		}
		
		if (phase == 1 && getSkillList().isSkillPresent(TEMPORARILY_IRONCLAD) && getOwner().getLifeStats().getHpPercentage() < 50) {
			skillId = TEMPORARILY_IRONCLAD;
			skillLevel = getSkillList().getSkillLevel(TEMPORARILY_IRONCLAD);
			return AttackIntention.SKILL_BUFF;
		}
		
		skillId = 0;
		skillLevel = 0;
		return AttackIntention.SIMPLE_ATTACK;
	}
	
}

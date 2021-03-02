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
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AttackIntention;
import com.aionemu.gameserver.ai2.ai.AggressiveNpcAI2;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * This AI is intended for use by the red-coloured Obscura mobs within Fire Temple. It may be moved into the open world AI list
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
 *   <tr><td>Flame</td><td>16574</td></tr>
 *   <tr><td>Fire Sparkle</td><td>16921</td></tr>
 * </tbody>
 * </table></BLOCKQUOTE>
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@AIName("ftredobscura")
public class RedObscuraAI2 extends AggressiveNpcAI2 {
	
	boolean flamed = false;
	
	final static int FLAME = 16574,
					 FIRE_SPARKLE = 16921;
	
	@Override
	protected void handleSpawned() {
		flamed = false;
		applyFireSparkle();
		super.handleSpawned();
	}
	
	@Override
	protected void handleRespawned() {
		flamed = false;
		applyFireSparkle();
		super.handleRespawned();
	}
	
	@Override
	protected void handleDied() {
		flamed = false;
		super.handleDied();
	}
	
	@Override
	protected void handleBackHome() {
		flamed = false;
		applyFireSparkle();
		super.handleBackHome();
	}
	
	private void applyFireSparkle() {
		if (getSkillList().isSkillPresent(FIRE_SPARKLE) && !getOwner().getEffectController().isAbnormalPresentBySkillId(FIRE_SPARKLE)) {
			EmoteManager.emoteStartAttacking(getOwner());
			AI2Actions.targetSelf(this);
			AI2Actions.useSkill(this, FIRE_SPARKLE, getSkillList().getSkillLevel(FIRE_SPARKLE));
			AI2Actions.targetCreature(this, null);
			EmoteManager.emoteStopAttacking(getOwner());
			setStateIfNot(AIState.IDLE);
		}
	}
	
	@Override
	protected void handleAttackComplete() {
		if (skillId == FLAME) flamed = true;
		super.handleAttackComplete();
	}
	
	@Override
	public AttackIntention chooseAttackIntention(Creature creature) {
		if (getTarget() == null || getAggroList().getMostHated() == null || ((Creature) getTarget()).getLifeStats().isAlreadyDead()) {
			return AttackIntention.FINISH_ATTACK;
		}
		
		if (!flamed && getSkillList().isSkillPresent(FLAME)) {
			skillId = FLAME;
			skillLevel = getSkillList().getSkillLevel(FLAME);
			return AttackIntention.SKILL_ATTACK;
		}
		
		return AttackIntention.SIMPLE_ATTACK;
	}
	
}

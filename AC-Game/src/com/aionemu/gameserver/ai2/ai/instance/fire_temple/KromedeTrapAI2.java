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
import com.aionemu.gameserver.ai2.ai.TrapNpcAI2;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;

/**
 * This AI is intended only to be used with the "Suspicious Object" (ID: 280501) Trap spawned by the Kromede AI.
 * To my knowledge, there is no skill to summon this trap, so they will be manually spawned with Kromede
 * as the creator via her AI.
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@AIName("ftkromedetrap")
public class KromedeTrapAI2 extends TrapNpcAI2 {
	
	@Override
	protected synchronized void tryActivateTrap(Creature creature) {
		if (!(creature instanceof Player) || super.isScheduledForDeletion()
			|| creature.getLifeStats().isAlreadyDead() || !(getCreator() instanceof Npc/* && ((Npc) getCreator()).isEnemy(creature)*/)
			|| !isInRange(creature, super.getTrapRange())
			|| ((Player) creature).isInVisualState(CreatureVisualState.HIDE20)
			|| ((((Player) creature).getAdminNeutral() & 0x1) == 1 && !((Npc) getCreator()).getAggroList().isHating(creature))) {
			/*
			 * We don't want to target mobs, only players.
			 * If we've already been triggered, don't trigger again.
			 * If the target is dead, don't waste our one trigger on them.
			 * This trap is for Kromede only, so our creator should be an Npc.
			 * If we've spawned from Kromede, the players in our area are likely hostile ~ don't bother checking.
			 * If the target is out of our range, don't prematurely trigger.
			 * Don't target hidden GMs!
			 * Don't target neutral GMs! GM's that interfered with us are not neutral!
			 */
			return;
		}
		
		if (setStateIfNot(AIState.FIGHT)) {
			AI2Actions.targetSelf(this);
			/*
			 * I don't like this method of getting the skill (randomly); but there's only one
			 * skill available unless the Server owner modifies the skills for this AI's owner.
			 * 
			 * The intended skill is 17050 (Area Aggravate Wound) at skill level 28.
			 */
			if (getSkillList() != null && getSkillList().size() > 0) {
				int skillId = getSkillList().getNextSkill().getSkillId();
				int skillLevel = getSkillList().getSkillLevel(skillId);
				getOwner().getController().useSkill(skillId, skillLevel);
			}
			super.scheduleForDeletion(5000);
		}
	}
	
}

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
package com.aionemu.gameserver.ai2.mechanics.actions;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.context.ObjIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.SkillIndex;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.model.skill.SkillEntry;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class UseSkillAction extends Action {
	
	public final ObjIndicator target;
	
	public final SkillIndex skill;
	
	public UseSkillAction(ObjIndicator target, SkillIndex skill) {
		super(ActionType.use_skill);
		this.target = target;
		this.skill = skill;
	}
	
	/**
	 * This method is supported by this Action, but should only be used while the AI is idle and
	 * there is only one skill being used; the AI should handle this Action internally using the
	 * information within this Action otherwise.
	 * <p>
	 * The reason for this is to allow the AI to properly move within range of the target
	 * before it just uses the associated skill, and to allow for cast time to be considered when
	 * multiple skills are used in succession.
	 */
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		//Maybe this needs more restrictions.
		if (!ai.isNonFightingState() || target != ObjIndicator.OBJI_SELF)
			throw new IllegalStateException("UseSkillAction#performAction(MechanicEvent, AbstractMechanicsAI2) called in an illegal context!"
										  + " AIState: " + ai.getState() + ", Target: " + target);
		SkillEntry skill = ai.getOwner().getSkillList().getSkill(this.skill);
		if (skill != null) {
			EmoteManager.emoteStartAttacking(ai.getOwner());
			AI2Actions.targetSelf(ai);
			AI2Actions.useSkill(ai, skill.getSkillId(), skill.getSkillLevel());
			AI2Actions.targetCreature(ai, null);
			EmoteManager.emoteStopAttacking(ai.getOwner());
		}
	}
	
	@Override
	public int hashCode() {
		return 3*target.ordinal() + 5*skill.ordinal();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UseSkillAction) {
			return (((UseSkillAction) obj).target == target && ((UseSkillAction) obj).skill == skill);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + "[" + skill + "] --> [" + target + "]";
	}
	
}

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

import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.context.AttackerIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.SkillIndex;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class UseSkillByAttackerIndicatorAction extends Action {
	
	public final AttackerIndicator target;
	
	public final SkillIndex skill;
	
	public final boolean restrictedRange;
	
	public UseSkillByAttackerIndicatorAction(AttackerIndicator target, SkillIndex skill, boolean restrictedRange) {
		super(ActionType.use_skill_by_attacker_indicator);
		this.target = target;
		this.skill = skill;
		this.restrictedRange = restrictedRange;
	}
	
	/**
	 * This method is not supported by this Action; the AI should handle this Action
	 * internally using the information within this Action.
	 * <p>
	 * The reason for this is to allow the AI to properly move within range of the target
	 * before it just uses the associated skill.
	 */
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {}
	
	@Override
	public int hashCode() {
		return 3*target.ordinal() + 5*skill.ordinal() + (restrictedRange ? 7 : 0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UseSkillByAttackerIndicatorAction) {
			UseSkillByAttackerIndicatorAction o = (UseSkillByAttackerIndicatorAction) obj;
			return (o.target == target && o.skill == skill && o.restrictedRange == restrictedRange);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + "[" + skill + "] --> [" + target + "] " + restrictedRange;
	}
	
}

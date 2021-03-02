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
package com.aionemu.gameserver.ai2.mechanics.conditions;

import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.ai2.mechanics.events.SkillEvent;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class IsEventSkillIdCondition extends Condition {
	
	public final int skillId;
	
	public IsEventSkillIdCondition(int skillId) {
		super(ConditionType.is_event_skill_id);
		this.skillId = skillId;
	}
	
	@Override
	public boolean check(MechanicEvent event, AbstractMechanicsAI2 ai) {
		if (event instanceof SkillEvent) {
			return ((SkillEvent) event).skillId == skillId;
		}
		throw new IllegalStateException("Unsupported Event Class (" + event.type + "): " + event.getClass().getSimpleName());
	}
	
	@Override
	public int hashCode() {
		return 3*skillId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IsEventSkillIdCondition) {
			IsEventSkillIdCondition o = (IsEventSkillIdCondition) obj;
			return (o.skillId == skillId);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + skillId + "]";
	}
	
}

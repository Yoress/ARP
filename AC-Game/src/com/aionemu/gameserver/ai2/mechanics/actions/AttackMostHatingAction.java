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
import com.aionemu.gameserver.ai2.mechanics.context.SkillIndex;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class AttackMostHatingAction extends Action {
	
	final public SkillIndex skill;
	
	public AttackMostHatingAction(SkillIndex skill) {
		super(ActionType.attack_most_hating);
		this.skill = skill;
	}
	
	/**
	 * This method is not supported for this Action -- The AI should handle this internally
	 * based on this Action's information.
	 */
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {}
	
	@Override
	public int hashCode() {
		return 3*skill.ordinal();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AttackMostHatingAction) {
			if (((AttackMostHatingAction) obj).skill == skill) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " --> [" + skill + "]";
	}
	
}

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
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class SetConditionSpawnVariableAction extends Action {
	
	public final String string;
	
	public final int set;
	
	public final int modify;
	
	public SetConditionSpawnVariableAction(String string, int set, int modify) {
		super(ActionType.set_condition_spawn_variable);
		this.string = string;
		this.set = set;
		this.modify = modify;
	}
	
	/**
	 * This method is not currently implemented for this Action.
	 */
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		int Unused_So_I_Know_This_Class_Is_Not_In_An_Acceptable_State = 0;
		//TODO: Auto-generated method stub
	}
	
	@Override
	public int hashCode() {
		return 3*string.hashCode() + 5*set + 7*modify;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SetConditionSpawnVariableAction) {
			return (((SetConditionSpawnVariableAction) obj).string.equals(string)
					&& ((SetConditionSpawnVariableAction) obj).set == set
					&& ((SetConditionSpawnVariableAction) obj).modify == modify);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + string + "] --> <" + set + ", " + modify + ">";
	}
	
}

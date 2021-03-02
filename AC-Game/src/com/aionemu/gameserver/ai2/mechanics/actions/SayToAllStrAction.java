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


public class SayToAllStrAction extends Action {
	
	public final String string;
	
	public SayToAllStrAction(String string) {
		super(ActionType.say_to_all_str);
		this.string = string;
	}
	
	/**
	 * This method is not currently supported for this Action.
	 */
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		//TODO: Not sure what to do with this; most of the data is garbage.
		int Unused_So_I_Know_This_Class_Is_Not_In_An_Acceptable_State = 0;
	}
	
	@Override
	public int hashCode() {
		return string.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SayToAllStrAction) {
			return ((SayToAllStrAction) obj).string.equals(string);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " --> " + string;
	}
	
}

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
import com.aionemu.gameserver.ai2.mechanics.context.AreaName;
import com.aionemu.gameserver.ai2.mechanics.context.AreaType;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;


/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class EnableAreaAction extends Action {
	
	final public AreaType areaType;
	
	final public AreaName areaName;
	
	final public int opCode;
	
	public EnableAreaAction(AreaType areaType, AreaName areaName, int opCode) {
		super(ActionType.enable_area);
		this.areaType = areaType;
		this.areaName = areaName;
		this.opCode = opCode;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		int Unused_So_I_Know_This_Class_Is_Not_In_An_Acceptable_State = 0;
		//TODO: Determine what this should do.
	}
	
	@Override
	public int hashCode() {
		return 3*areaType.ordinal() + 5*areaName.ordinal() + 7*opCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EnableAreaAction) {
			EnableAreaAction o = (EnableAreaAction) obj;
			return (o.areaType == areaType && o.areaName == areaName && o.opCode == opCode);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + areaName + "] --> <" + areaType + ", " + opCode + ">";
	}
	
}

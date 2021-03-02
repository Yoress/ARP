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
import com.aionemu.gameserver.ai2.mechanics.context.ObjIndicator;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class BroadcastMessageToPartyAction extends Action {
	
	final public int messageType;
	
	final public int param1;
	
	final public int param2;
	
	final public ObjIndicator paramObj;
	
	public BroadcastMessageToPartyAction(int messageType, int param1, int param2, ObjIndicator paramObj) {
		super(ActionType.broadcast_message_to_party);
		this.messageType = messageType;
		this.param1 = param1;
		this.param2 = param2;
		this.paramObj = paramObj;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		int Unused_So_I_Know_This_Class_Is_Not_In_An_Acceptable_State = 0;
		//TODO: Implement; determine what mob parties are
	}
	
	@Override
	public int hashCode() {
		return 3*messageType + 5*param1 + 7*param2 + 11*paramObj.ordinal();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BroadcastMessageToPartyAction) {
			BroadcastMessageToPartyAction o = (BroadcastMessageToPartyAction) obj;
			return (o.messageType == messageType && o.param1 == param1 && o.param2 == param2 && o.paramObj == paramObj);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " " + messageType + ": [" + paramObj + "] --> <" + param1 + ", " + param2 + ">";
	}
	
}

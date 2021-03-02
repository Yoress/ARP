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

import com.aionemu.gameserver.ai2.AI2;
import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.MechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.context.ObjIndicator;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class SendMessageAction extends Action {
	
	public final ObjIndicator target;
	
	public final int messageType;
	
	public final int param1;
	
	public final int param2;
	
	public final ObjIndicator paramObj;
	
	public SendMessageAction(ObjIndicator target, int messageType, int param1, int param2, ObjIndicator paramObj) {
		super(ActionType.send_message);
		this.target = target;
		this.messageType = messageType;
		this.param1 = param1;
		this.param2 = param2;
		this.paramObj = paramObj;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		try {
			AI2 targetAI = ((Creature) event.getObjectIndicator(target, ai)).getAi2();
			if (targetAI instanceof MechanicsAI2) {
				targetAI.onCustomEvent(messageType, this, event.getObjectIndicator(paramObj, ai), ai.getOwner());
			}
		} catch (ClassCastException e) {
			//TODO: Maybe log that this happened.
			//Do nothing.
		}
	}
	
	@Override
	public int hashCode() {
		return 3*target.ordinal() + 5*messageType + 7*param1 + 11*param2 + 13*paramObj.ordinal();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SendMessageAction) {
			SendMessageAction o = (SendMessageAction) obj;
			return (o.target == target && o.messageType == messageType && o.param1 == param1 && o.param2 == param2 && o.paramObj == paramObj);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " " + messageType + " --> [" + target + "]: [" + paramObj + "] --> <" + param1 + "," + param2 + ">";
	}
	
}

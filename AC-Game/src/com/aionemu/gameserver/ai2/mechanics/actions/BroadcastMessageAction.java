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
import com.aionemu.gameserver.ai2.mechanics.context.ObjIndicator;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class BroadcastMessageAction extends Action {
	
	final public int messageType;
	
	final public int param1;
	
	final public int param2;
	
	final public int range;
	
	final public ObjIndicator paramObj;
	
	public BroadcastMessageAction(int messageType, int param1, int param2, int range, ObjIndicator paramObj) {
		super(ActionType.broadcast_message);
		this.messageType = messageType;
		this.param1 = param1;
		this.param2 = param2;
		this.range = range;
		this.paramObj = paramObj;
	}
	
	@Override
	public void performAction(final MechanicEvent event, final AbstractMechanicsAI2 ai) {
		final Action thisAction = this;
		ai.getOwner().getKnownList().doOnAllObjects(new Visitor<VisibleObject>() {
			@Override
			public void visit(VisibleObject object) {
				if (object instanceof Creature) {
					AI2 objectAI = ((Creature) object).getAi2();
					if (objectAI instanceof AbstractMechanicsAI2) {
						if (MathUtil.isIn3dRange(ai.getOwner(), object, range)) {
							objectAI.onCustomEvent(messageType, thisAction, event.getObjectIndicator(paramObj, ai), ai.getOwner());
						}
					}
				}
			}
		});
	}
	
	@Override
	public int hashCode() {
		return 3*messageType + 5*param1 + 7*param2 + 11*range + 13*paramObj.ordinal();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BroadcastMessageAction) {
			BroadcastMessageAction o = (BroadcastMessageAction) obj;
			return (o.messageType == messageType && o.param1 == param1 && o.param2 == param2 && o.range == range && o.paramObj == paramObj);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " " + messageType + "@" + range + "m: [" + paramObj + "] --> <" + param1 + ", " + param2 + ">";
	}
	
}

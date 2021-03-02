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
package com.aionemu.gameserver.ai2.mechanics.events;

import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.MechanicEventType;
import com.aionemu.gameserver.ai2.mechanics.context.ObjIndicator;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;

public abstract class MechanicEvent {
	
	public final MechanicEventType type;
	
	public MechanicEvent(MechanicEventType type) {
		this.type = type;
	}
	
	/**
	 * Identifies the {@link VisibleObject} known to the AI this event is happening to that best matches
	 * the given {@link ObjIndicator} and returns it.
	 * 
	 * @param obj -- the {@link ObjIndicator} to identify the object with
	 * @param ai -- the {@link AbstractMechanicsAI2} that this event is happening to
	 * @return The {@link VisibleObject} corresponding to the {@link ObjIndicator} given.
	 */
	public abstract VisibleObject getObjectIndicator(ObjIndicator obj, AbstractMechanicsAI2 ai);
	
	public VisibleObject getDefaultObjectIndicator(ObjIndicator obj, AbstractMechanicsAI2 ai) {
		switch (obj) {
			case OBJI_SELF:
				return ai.getOwner();
			case OBJI_CUR_TARGET:
				VisibleObject target = ai.getTarget();
				return (target == null ? ai.getOwner() : target);
			case OBJI_MASTER:
				if (ai.getOwner().getMaster() != null) return ai.getOwner().getMaster();
			case OBJI_ATTACKER:
			case OBJI_CASTER:
			case OBJI_EVENT_TARGET:
			case OBJI_FLEE_FROM:
			case OBJI_FRIEND:
			case OBJI_KILLER:
			case OBJI_MESSAGE_PARAM:
			case OBJI_SEEN:
			case OBJI_TALKER:
			case OBJI_EVENT_MAKER:
			case OBJI_MESSAGE_SENDER:
			case OBJI_PARTY_MEMBER:
			default:
				throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not currently support [" + obj + "] ObjIndicator calls (" + type + ")!");
		}
	}
	
	@Override
	public String toString() {
		return "Event [" + type + "]";
	}
	
}

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
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class CreatureEvent extends MechanicEvent {
	
	public final Creature eventTarget, friend, foe;
	
	public CreatureEvent(MechanicEventType type, Creature eventTarget, Creature friend, Creature foe) {
		super(type);
		this.eventTarget = eventTarget;
		this.friend = friend;
		this.foe = foe;
	}
	
	@Override
	public VisibleObject getObjectIndicator(ObjIndicator obj, AbstractMechanicsAI2 ai) {
		switch (obj) {
			case OBJI_SELF:
				return ai.getOwner();
			case OBJI_CUR_TARGET:
				VisibleObject target = ai.getTarget();
				return (target == null ? ai.getOwner() : target);
			case OBJI_ATTACKER:
			case OBJI_KILLER:
				return foe;
			case OBJI_EVENT_TARGET:
			case OBJI_FLEE_FROM:
			case OBJI_SEEN:
			case OBJI_TALKER:
				return eventTarget;
			case OBJI_FRIEND:
				return friend;
			case OBJI_CASTER:
			case OBJI_EVENT_MAKER:
			case OBJI_MASTER:
			case OBJI_MESSAGE_PARAM:
			case OBJI_MESSAGE_SENDER:
			case OBJI_PARTY_MEMBER:
			default: return getDefaultObjectIndicator(obj, ai);
		}
	}
	
}

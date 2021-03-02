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
import com.aionemu.gameserver.ai2.mechanics.context.BtimerIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.ObjIndicator;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;

public class BattleTimerEvent extends MechanicEvent {
	
	final public BtimerIndicator indicator;
	
	public BattleTimerEvent(MechanicEventType type, BtimerIndicator indicator) {
		super(MechanicEventType.on_battle_timer);
		this.indicator = indicator;
	}
	
	@Override
	public VisibleObject getObjectIndicator(ObjIndicator obj, AbstractMechanicsAI2 ai) {
		if (obj == ObjIndicator.OBJI_EVENT_TARGET) return (ai.getTarget() == null ? ai.getOwner(): ai.getTarget());
		return getDefaultObjectIndicator(obj, ai);
	}
	
}

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
package com.aionemu.gameserver.ai2.mechanics.conditions;

import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.context.ObjIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.RaceType;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class IsRaceCondition extends Condition {
	
	final public ObjIndicator from;
	
	final public RaceType raceType;
	
	public IsRaceCondition(ObjIndicator from, RaceType raceType) {
		super(ConditionType.is_race);
		this.from = from;
		this.raceType = raceType;
	}
	
	@Override
	public boolean check(MechanicEvent event, AbstractMechanicsAI2 ai) {
		try {
			Creature creature = (Creature) event.getObjectIndicator(from, ai);
			if (creature == null) return false;
			return raceType.isRace(creature.getRace());
		} catch (ClassCastException e) {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return 3*from.ordinal() + 5*raceType.ordinal();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IsRaceCondition) {
			IsRaceCondition o = (IsRaceCondition) obj;
			return (o.from == from && o.raceType == raceType);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + raceType + "] --> [" + from + "]";
	}
	
}

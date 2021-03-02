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
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class IsHpInBoundaryCondition extends Condition {
	
	public final ObjIndicator who;
	
	public final int largerThan;
	
	public final int lessThan;
	
	public IsHpInBoundaryCondition(ObjIndicator who, int largerThan, int lessThan) {
		super(ConditionType.is_hp_in_boundary);
		this.who = who;
		this.largerThan = largerThan;
		this.lessThan = lessThan;
	}
	
	@Override
	public boolean check(MechanicEvent event, AbstractMechanicsAI2 ai) {
		try {
			int currentHp = ((Creature) event.getObjectIndicator(who, ai)).getLifeStats().getHpPercentage();
			return (currentHp > largerThan && currentHp < lessThan);
		} catch (ClassCastException e) {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return 3*who.ordinal() + 5*largerThan + 7*lessThan;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IsHpInBoundaryCondition) {
			IsHpInBoundaryCondition o = (IsHpInBoundaryCondition) obj;
			return (o.who == who && o.largerThan == largerThan && o.lessThan == lessThan);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + who + "] --> (" + largerThan + ", " + lessThan + ")";
	}
	
}

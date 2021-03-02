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
import com.aionemu.gameserver.ai2.mechanics.context.IntvarIndicator;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class AddIntvarCondition extends Condition {
	
	final public IntvarIndicator intvarIndicator;
	
	final public int varToAdd;
	
	final public int lowerBound;
	
	final public int upperBound;
	
	final public boolean beTrueOnlyWhenHitTheBound;
	
	public AddIntvarCondition(IntvarIndicator intvarIndicator, int varToAdd, int lowerBound, int upperBound, boolean beTrueOnlyWhenHitTheBound) {
		super(ConditionType.add_intvar);
		this.intvarIndicator = intvarIndicator;
		this.varToAdd = varToAdd;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.beTrueOnlyWhenHitTheBound = beTrueOnlyWhenHitTheBound;
	}
	
	@Override
	public boolean check(MechanicEvent event, AbstractMechanicsAI2 ai) {
		return ai.addIntvar(intvarIndicator, varToAdd, lowerBound, upperBound, beTrueOnlyWhenHitTheBound);
	}
	
	@Override
	public int hashCode() {
		return 3*intvarIndicator.ordinal() + 5*varToAdd + 7*lowerBound + 11*upperBound + (beTrueOnlyWhenHitTheBound ? 13 : 0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AddIntvarCondition) {
			AddIntvarCondition o = (AddIntvarCondition) obj;
			return (o.intvarIndicator == intvarIndicator
					&& o.varToAdd == varToAdd
					&& o.lowerBound == lowerBound
					&& o.upperBound == upperBound
					&& o.beTrueOnlyWhenHitTheBound == beTrueOnlyWhenHitTheBound);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + intvarIndicator + "] --> " + varToAdd + " [" + lowerBound + ", " + upperBound + "]: " + beTrueOnlyWhenHitTheBound;
	}
	
}

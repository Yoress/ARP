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

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.context.ClassIndicator;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.controllers.attack.AggroInfo;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class SwitchTargetByClassIndicatorAction extends Action {
	
	public final ClassIndicator target;
	
	public final int percentToAdd;
	
	public final int pointsToAdd;
	
	public final boolean restrictedRange;
	
	public SwitchTargetByClassIndicatorAction(ClassIndicator target, int percentToAdd, int pointsToAdd, boolean restrictedRange) {
		super(ActionType.switch_target_by_class_indicator);
		this.target = target;
		this.percentToAdd = percentToAdd;
		this.pointsToAdd = pointsToAdd;
		this.restrictedRange = restrictedRange;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		Creature targ = getClassIndicator(target, ai);
		if (targ != null && (!restrictedRange || MathUtil.isIn3dRange(ai.getOwner(), targ, 30))) { //Not sure what range to use, so going with 30.
			AggroInfo aggroInfo = ai.getOwner().getAggroList().getAggroInfo(targ);
			aggroInfo.addHate((int) (aggroInfo.getHate()*(percentToAdd/100F)));
			aggroInfo.addHate(pointsToAdd);
			AI2Actions.targetCreature(ai, targ);
		} else if (targ == null) {
			throw new NullPointerException("Target was null for mechanic indicator: <[" + event + ": " + this + "]>");
		}
	}
	
	@Override
	public int hashCode() {
		return 3*target.ordinal() + 5*percentToAdd + 7*pointsToAdd + (restrictedRange ? 11 : 0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SwitchTargetByClassIndicatorAction) {
			SwitchTargetByClassIndicatorAction o = (SwitchTargetByClassIndicatorAction) obj;
			return (o.target == target && o.percentToAdd == percentToAdd && o.pointsToAdd == pointsToAdd && o.restrictedRange == restrictedRange);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + target + "] --> <" + percentToAdd + ", " + pointsToAdd + ", " + restrictedRange + ">";
	}
	
}

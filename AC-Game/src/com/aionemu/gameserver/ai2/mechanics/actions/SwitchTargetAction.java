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
import com.aionemu.gameserver.ai2.mechanics.context.ObjIndicator;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.controllers.attack.AggroInfo;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class SwitchTargetAction extends Action {
	
	public final ObjIndicator target;
	
	public final int percentToAdd;
	
	public final int pointsToAdd;
	
	public SwitchTargetAction(ObjIndicator target, int percentToAdd, int pointsToAdd) {
		super(ActionType.switch_target);
		this.target = target;
		this.percentToAdd = percentToAdd;
		this.pointsToAdd = pointsToAdd;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		try {
			Creature targ = (Creature) event.getObjectIndicator(target, ai);
			if (targ != null && MathUtil.isInRange(ai.getOwner(), targ, 70)) { //Using a capped range, just in case.
				AggroInfo aggroInfo = ai.getOwner().getAggroList().getAggroInfo(targ);
				aggroInfo.addHate((int) (aggroInfo.getHate()*(percentToAdd/100F)));
				aggroInfo.addHate(pointsToAdd);
				AI2Actions.targetCreature(ai, targ);
			} else {
				throw new NullPointerException("Target was null for mechanic indicator: <[" + event + ": " + this + "]>");
			}
		} catch (ClassCastException e) {
			//TODO: Maybe log that this happened.
			//Do nothing.
		}
	}
	
	@Override
	public int hashCode() {
		return 3*target.ordinal() + 5*percentToAdd + 7*pointsToAdd;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SwitchTargetAction) {
			return (((SwitchTargetAction) obj).target == target
					&& ((SwitchTargetAction) obj).percentToAdd == percentToAdd
					&& ((SwitchTargetAction) obj).pointsToAdd == pointsToAdd);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + target + "] --> <" + percentToAdd + ", " + pointsToAdd + ">";
	}
	
}

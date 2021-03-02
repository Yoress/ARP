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

import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.context.ObjIndicator;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class AddHatePointAction extends Action {
	
	public final ObjIndicator target;
	
	public final int pointToAdd;
	
	public AddHatePointAction(ObjIndicator target, int pointToAdd) {
		super(ActionType.add_hate_point);
		this.target = target;
		this.pointToAdd = pointToAdd;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		try {
			Creature targetCreature = (Creature) event.getObjectIndicator(target, ai);
			if (targetCreature == null) targetCreature = ai.getOwner().getAggroList().getMostHated();
			ai.getOwner().getAggroList().addHate(targetCreature, pointToAdd);
		} catch (ClassCastException e) {
			//TODO: Maybe log this happening
			//Do nothing; if it's not a Creature, then the AI cannot hate it.
		}
	}
	
	@Override
	public int hashCode() {
		return 3*target.ordinal() + 11*pointToAdd;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AddHatePointAction) {
			return (((AddHatePointAction) obj).target == target && ((AddHatePointAction) obj).pointToAdd == pointToAdd);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " --> " + pointToAdd;
	}
	
}

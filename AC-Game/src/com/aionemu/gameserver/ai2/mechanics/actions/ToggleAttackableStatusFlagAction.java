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
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.model.gameobjects.CreatureType;
import com.aionemu.gameserver.model.gameobjects.Npc;


/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class ToggleAttackableStatusFlagAction extends Action {
	
	final public boolean attakable;
	
	public ToggleAttackableStatusFlagAction(boolean attakable) {
		super(ActionType.toggle_attackable_status_flag);
		this.attakable = attakable;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		Npc npc = ai.getOwner(); //Check SM_NPC_INFO packet
		npc.setType(attakable ? CreatureType.NULL : CreatureType.INVULNERABLE);
	}
	
	@Override
	public int hashCode() {
		return (attakable ? 3 : 0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ToggleAttackableStatusFlagAction) {
			ToggleAttackableStatusFlagAction o = (ToggleAttackableStatusFlagAction) obj;
			return (o.attakable == attakable);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + attakable + "]";
	}
	
}

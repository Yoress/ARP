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

import com.aionemu.gameserver.ai2.mechanics.MechanicEventType;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.model.gameobjects.Creature;


/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class AttackedEvent extends CreatureEvent {
	
	final public AttackStatus attackStatus;
	
	public AttackedEvent(MechanicEventType type, Creature eventTarget, Creature friend, Creature foe, AttackStatus attackStatus) {
		super(type, eventTarget, friend, foe);
		this.attackStatus = attackStatus;
	}
	
}

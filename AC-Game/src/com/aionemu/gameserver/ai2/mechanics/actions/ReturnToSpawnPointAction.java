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

import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;


/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class ReturnToSpawnPointAction extends Action {
	
	public ReturnToSpawnPointAction() {
		super(ActionType.return_to_spawn_point);
	}

	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		ai.onGeneralEvent(AIEventType.NOT_AT_HOME);
	}

	@Override
	public int hashCode() {
		//These objects are all the same.
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		//These objects are all the same.
		return obj instanceof ReturnToSpawnPointAction;
	}
	
}

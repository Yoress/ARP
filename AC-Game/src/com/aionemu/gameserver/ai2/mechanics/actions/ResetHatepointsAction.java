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

import java.util.Collection;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.controllers.attack.AggroInfo;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class ResetHatepointsAction extends Action {
	
	public final boolean isExceptMostHating;
	
	public final boolean volatileHatepointOnly;
	
	public ResetHatepointsAction(boolean isExceptMostHating, boolean volatileHatePointOnly) {
		super(ActionType.reset_hatepoints);
		this.isExceptMostHating = isExceptMostHating;
		this.volatileHatepointOnly = volatileHatePointOnly;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		int Unused_So_I_Know_This_Class_Is_Not_In_An_Acceptable_State = 0;
		//FIXME: This ignores the two boolean values (mainly because all known values are false -- but not sure what they mean yet, either).
		synchronized (ai.getOwner().getAggroList()) {
			Collection<AggroInfo> aggroList = ai.getOwner().getAggroList().getList();
			/*
			 * This is sorta oddly implemented, but it ensures that at least one
			 * random player on the hate list holds more hate than the others.
			 * 
			 * This is done because the order of the AggroList; if the tank is the first
			 * to be placed on the hate list, then they are likely to be the first one
			 * attacked again after aggro resets (well, that's technically unknown --
			 * dependent on the implementation of the underlying map); it's better
			 * for the mechanic if the next target after reset is actually random.
			 */
			int size = aggroList.size();
			int c = 0;
			boolean rnd = false;
			boolean randomTargetSet = false;
			AggroInfo lastPlayer = null;
			for (AggroInfo aggro: aggroList) {
				boolean isPlayer = false;
				if (aggro.getAttacker() instanceof Player) {
					lastPlayer = aggro;
					isPlayer = true;
				}
				if (!rnd) rnd = Rnd.nextBoolean();
				if (!randomTargetSet && rnd && isPlayer) {
					aggro.setHate(1);
					randomTargetSet = true;
				} else {
					aggro.setHate(0);
				}
				c++;
				if (!rnd && c == size) {
					if (lastPlayer != null) {
						lastPlayer.setHate(1);
					} else aggro.setHate(1);
				}
			}
		}
	}
	
	@Override
	public int hashCode() {
		return (isExceptMostHating ? 3 : 0) + (volatileHatepointOnly ? 5 : 0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ResetHatepointsAction) {
			return (((ResetHatepointsAction) obj).isExceptMostHating == isExceptMostHating
					&& ((ResetHatepointsAction) obj).volatileHatepointOnly == volatileHatepointOnly);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " <" + isExceptMostHating + ", " + volatileHatepointOnly + ">";
	}
	
}

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
import com.aionemu.gameserver.ai2.mechanics.context.UserIndicator;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;


/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class GiveExpAction extends Action {
	
	final public UserIndicator target;
	
	final public int exp;
	
	public GiveExpAction(UserIndicator target, int exp) {
		super(ActionType.give_exp);
		this.target = target;
		this.exp = exp;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		int Unused_So_I_Know_This_Class_Is_Not_In_An_Acceptable_State = 0;
		//TODO: Investigate context and fix if needed.
		VisibleObject obj = event.getObjectIndicator(target.getObjIndicator(), ai);
		if (obj instanceof Player) {
			Player player = (Player) obj;
			player.getCommonData().addExp(exp, RewardType.QUEST);
		}
	}
	
	@Override
	public int hashCode() {
		return 3*target.ordinal() + 5*exp;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GiveExpAction) {
			GiveExpAction o = (GiveExpAction) obj;
			return (o.target == target && o.exp == exp);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + target + "] --> (" + exp + ")";
	}
	
}

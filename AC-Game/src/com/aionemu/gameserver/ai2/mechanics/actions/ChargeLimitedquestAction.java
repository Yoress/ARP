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


/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class ChargeLimitedquestAction extends Action {
	
	final public int questId;
	
	final public boolean chargeMaxCount;
	
	public ChargeLimitedquestAction(int questId, boolean chargeMaxCount) {
		super(ActionType.charge_limitedquest);
		this.questId = questId;
		this.chargeMaxCount = chargeMaxCount;
	}
	
	/**
	 * It's unclear what this Action should do; so it's not implemented.
	 * <p>
	 * From the data, it looks like this is only used for some sort of test quest, specifically
	 * Q9645. For now, I will let this Action do nothing, and hope the quest engine handles it
	 * properly.
	 */
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		//TODO: Determine what this does
	}
	
	@Override
	public int hashCode() {
		return 3*questId + (chargeMaxCount ? 5 : 0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ChargeLimitedquestAction) {
			ChargeLimitedquestAction o = (ChargeLimitedquestAction) obj;
			return (o.questId == questId && o.chargeMaxCount == chargeMaxCount);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " Q" + questId + " --> " + chargeMaxCount;
	}
	
}

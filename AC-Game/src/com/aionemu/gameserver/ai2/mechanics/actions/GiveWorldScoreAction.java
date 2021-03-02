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


/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class GiveWorldScoreAction extends Action {
	
	final public UserIndicator target;
	
	final public int scoreMin;
	
	final public int scoreMax;
	
	public GiveWorldScoreAction(UserIndicator target, int scoreMin, int scoreMax) {
		super(ActionType.give_world_score);
		this.target = target;
		this.scoreMin = scoreMin;
		this.scoreMax = scoreMax;
	}
	
	/**
	 * This action is currently handled by instance handlers, so this method does nothing.
	 */
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {}
	
	@Override
	public int hashCode() {
		return 3*target.ordinal() + 5*scoreMin + 7*scoreMax;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GiveWorldScoreAction) {
			GiveWorldScoreAction o = (GiveWorldScoreAction) obj;
			return (o.target == target && o.scoreMin == scoreMin && o.scoreMax == scoreMax);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + target + "] --> <" + scoreMin + ", " + scoreMax + ">";
	}
	
}

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
import com.aionemu.gameserver.ai2.mechanics.context.MovingCollisionType;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;


/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class OnOffMovingCollisionAction extends Action {
	
	final public MovingCollisionType movingCollisionType;
	
	final public int sunzoneid;
	
	final public boolean onoff;
	
	public OnOffMovingCollisionAction(MovingCollisionType movingCollisionType, int sunzoneid, boolean onoff) {
		super(ActionType.on_off_moving_collision);
		this.movingCollisionType = movingCollisionType;
		this.sunzoneid = sunzoneid;
		this.onoff = onoff;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		int Unused_So_I_Know_This_Class_Is_Not_In_An_Acceptable_State = 0;
		//TODO: Toggle the thing
	}
	
	@Override
	public int hashCode() {
		return 3*movingCollisionType.ordinal() + 5*sunzoneid + (onoff ? 7 : 0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OnOffMovingCollisionAction) {
			OnOffMovingCollisionAction o = (OnOffMovingCollisionAction) obj;
			return (o.movingCollisionType == movingCollisionType && o.sunzoneid == sunzoneid && o.onoff == onoff);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + movingCollisionType + "] --> <" + sunzoneid + ", " + onoff + ">";
	}
	
}

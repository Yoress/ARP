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
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class TeleportTargetAction extends Action {
	
	public final ObjIndicator target;
	
	public final float x;
	
	public final float y;
	
	public final float z;
	
	public final int dir;
	
	public final boolean showFX;
	
	public TeleportTargetAction(ObjIndicator target, float x, float y, float z, int dir, boolean showFX) {
		super(ActionType.teleport_target);
		this.target = target;
		this.x = x;
		this.y = y;
		this.z = z;
		this.dir = dir;
		this.showFX = showFX;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		try {
			Player player = (Player) event.getObjectIndicator(target, ai);
			Npc owner = ai.getOwner();
			if (showFX) {
				TeleportService2
					.teleportTo(player, owner.getWorldId(), owner.getInstanceId(), x, y, z, (byte) (dir/3), TeleportAnimation.BEAM_ANIMATION);
			} else {
				TeleportService2.teleportTo(player, owner.getWorldId(), owner.getInstanceId(), x, y, z, (byte) (dir/3));
			}
		} catch (ClassCastException e) {
			//TODO: Maybe log that this happened.
			//Should never be a non-player getting tp'd here.
		}
	}
	
	@Override
	public int hashCode() {
		return 3*target.ordinal() + 5*((int) x) + 7*((int) y) + 11*((int) z) + 13*dir + ((showFX) ? 17 : 0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TeleportTargetAction) {
			TeleportTargetAction o = (TeleportTargetAction) obj;
			return (o.target == target && o.x == x && o.y == y && o.z == z  && o.dir == dir  && o.showFX == showFX);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + target + "] --> (" + x + ", " + y + ", " + z + ", " + dir + ") " + showFX;
	}
	
}

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
public class GiveScoreAction extends Action {
	
	final public UserIndicator target;
	
	public GiveScoreAction(UserIndicator target) {
		super(ActionType.give_score);
		this.target = target;
	}
	
	/**
	 * This action is currently handled by instance handlers... while an implementation exists,
	 * this method is currently commented out and does nothing.
	 */
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
//		int worldId = ai.getOwner().getWorldId();
//		int instanceId = ai.getOwner().getInstanceId();
//		InstanceHandler instanceHandler = World.getInstance().getWorldMap(worldId).getWorldMapInstanceById(instanceId).getInstanceHandler();
//		if (instanceHandler != null) {
//			InstanceReward<?> reward = instanceHandler.getInstanceReward();
//			Player player = (Player) event.getObjectIndicator(target.getObjIndicator(), ai);
//			if (reward.containPlayer(player.getObjectId())) {
//				reward.getPlayerReward(player.getObjectId()).addPoints(0); //FIXME: Give score based on AI owner
//			}
//		}
	}
	
	@Override
	public int hashCode() {
		return 3*target.ordinal();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GiveScoreAction) {
			GiveScoreAction o = (GiveScoreAction) obj;
			return (o.target == target);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + target + "]";
	}
	
}

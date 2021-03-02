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
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INSTANCE_STAGE_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;


/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class ChangeWorldSceneStatusAction extends Action {
	
	final public int scenestatus;
	
	public ChangeWorldSceneStatusAction(int scenestatus) {
		super(ActionType.change_world_scene_status);
		this.scenestatus = scenestatus;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		final SM_INSTANCE_STAGE_INFO smSceneStatus = new SM_INSTANCE_STAGE_INFO(scenestatus);
		World.getInstance()
			.getWorldMap(ai.getOwner().getWorldId()).getWorldMapInstanceById(ai.getOwner().getInstanceId()).doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player player) {
					if (player.isOnline()) PacketSendUtility.sendPacket(player, smSceneStatus);
				}
			}
		);
	}
	
	@Override
	public int hashCode() {
		return 3*scenestatus;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ChangeWorldSceneStatusAction) {
			ChangeWorldSceneStatusAction o = (ChangeWorldSceneStatusAction) obj;
			return (o.scenestatus == scenestatus);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + scenestatus + "]";
	}
	
}

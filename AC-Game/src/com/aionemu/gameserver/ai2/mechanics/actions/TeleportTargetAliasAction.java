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
import com.aionemu.gameserver.ai2.mechanics.context.Alias;
import com.aionemu.gameserver.ai2.mechanics.context.Alias.AliasPosition;
import com.aionemu.gameserver.ai2.mechanics.context.ObjIndicator;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class TeleportTargetAliasAction extends Action {
	
	public final ObjIndicator target;
	
	public final Alias alias;
	
	public final boolean showFX;
	
	public TeleportTargetAliasAction(ObjIndicator target, Alias alias, boolean showFX) {
		super(ActionType.teleport_target_alias);
		this.target = target;
		this.alias = alias;
		this.showFX = showFX;
	}
	
	/**
	 * This method is not currently implemented.
	 */
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		try {
			Player player = (Player) event.getObjectIndicator(target, ai);
			AliasPosition pos = alias.getPosition();
			WorldMap map = World.getInstance().getWorldMap(pos.worldId);
			
			final int instanceId;
			if (map.isInstanceType()) {
				instanceId = getInstanceId(pos.worldId, player);
			} else {
				if (player.getWorldId() == pos.worldId) {
					instanceId = player.getInstanceId();
				} else {
					instanceId = map.getMainWorldMapInstance().getInstanceId();
				}
			}
			
			if (showFX) {
				TeleportService2.teleportTo(player, pos.worldId, instanceId, pos.x, pos.y, pos.z, pos.h, TeleportAnimation.BEAM_ANIMATION);
			} else {
				TeleportService2.teleportTo(player, pos.worldId, instanceId, pos.x, pos.y, pos.z, pos.h, TeleportAnimation.NO_ANIMATION);
			}
		} catch (ClassCastException e) {
			//TODO: Maybe log that this happened.
			//Should never be a non-player getting tp'd here.
		}
	}
	
	/*
	 * Code taken from GoTo admincommand.
	 */
	private static int getInstanceId(int worldId, Player player) {
		if (player.getWorldId() == worldId) {
			WorldMapInstance registeredInstance = InstanceService.getRegisteredInstance(worldId, player.getObjectId());
			if (registeredInstance != null) {
				return registeredInstance.getInstanceId();
			}
		}
		WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(worldId);
		InstanceService.registerPlayerWithInstance(newInstance, player);
		return newInstance.getInstanceId();
	}
	
	@Override
	public int hashCode() {
		return 3*target.ordinal() + 5*alias.ordinal() + (showFX ? 7 : 0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TeleportTargetAliasAction) {
			TeleportTargetAliasAction o = (TeleportTargetAliasAction) obj;
			return (o.target == target && o.alias == alias && o.showFX == showFX);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + "[" + target + "] --> [" + alias + "] " + showFX;
	}
	
}

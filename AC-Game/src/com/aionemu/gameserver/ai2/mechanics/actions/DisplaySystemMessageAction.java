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
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.aionemu.gameserver.world.zone.ZoneService;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class DisplaySystemMessageAction extends Action {
	
	public final int stringId;
	
	public final String areaName;
	
	public final String param1;
	
	public final String param2;
	
	public final String param3;
	
	public DisplaySystemMessageAction(int stringId, String areaName, String param1, String param2, String param3) {
		super(ActionType.display_system_message);
		this.stringId = stringId;
		
		if (areaName == null) areaName = "";
		this.areaName = areaName;
		
		if (param1 == null) param1 = "";
		this.param1 = param1;
		
		if (param2 == null) param2 = "";
		this.param2 = param2;
		
		if (param3 == null) param3 = "";
		this.param3 = param3;
	}
	
	@Override
	public void performAction(MechanicEvent event, final AbstractMechanicsAI2 ai) {
		final int worldId = ai.getOwner().getWorldId();
		final int worldInstanceId = ai.getOwner().getInstanceId();
		World.getInstance().getWorldMap(worldId).getWorldMapInstanceById(worldInstanceId).doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline() && player.getWorldId() == worldId && player.getInstanceId() == worldInstanceId) {
					if (areaName.isEmpty()) {
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(stringId, param1, param2, param3));
					} else {
						ZoneInstance zone = ZoneService.getInstance().getZoneInstancesByWorldId(worldId).get(ZoneName.get(areaName));
						if (zone == null) {
							//Could log an error here about missing Zone, but the ZoneService will do it.
							if (World.getInstance().getWorldMap(worldId).isInstanceType()) {
								PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(stringId, param1, param2, param3));
							} else {
								if (ai.getOwner().getKnownList().knowns(player))
									PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(stringId, param1, param2, param3));
							}
							return;
						}
						if (zone.isInsideCreature(player)) {
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(stringId, param1, param2, param3));
						}
					}
				}
			}
		});
	}
	
	@Override
	public int hashCode() {
		return 3*stringId + 5*areaName.hashCode() + 7*param1.hashCode() + 11*param2.hashCode() + 13*param3.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DisplaySystemMessageAction) {
			DisplaySystemMessageAction o = (DisplaySystemMessageAction) obj;
			return (o.stringId == stringId && o.areaName == areaName && o.param1 == param1 && o.param2 == param2 && o.param3 == param3);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " " + stringId + ": [" + areaName + "] --> <" + param1 + ", " + param2 + ", " + param3 + ">";
	}
	
}

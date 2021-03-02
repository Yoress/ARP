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
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.flypath.FlyPathType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.windstreams.Location2D;
import com.aionemu.gameserver.model.templates.windstreams.WindstreamTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_WINDSTREAM_ANNOUNCE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;


/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class OnOffWindpathAction extends Action {
	
	final public int groupid;
	
	final public boolean onoff;
	
	public OnOffWindpathAction(int groupid, boolean onoff) {
		super(ActionType.on_off_windpath);
		this.groupid = groupid;
		this.onoff = onoff;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		WindstreamTemplate template = DataManager.WINDSTREAM_DATA.getStreamTemplate(ai.getPosition().getMapId());
		Location2D windpath = null;
		for (Location2D wind : template.getLocations().getLocation()) {
			if (wind.getId() == groupid) {
				windpath = wind;
				break;
			}
		}
		
		final int worldId = ai.getOwner().getWorldId();
		if (windpath == null) {
			LOG.error("Missing Windstream Id: " + groupid + ", in world: " + worldId + ".");
			return;
		}
		final FlyPathType pathType = windpath.getFlyPathType();
		windpath.setState(onoff ? 1 : 0);
		ai.getPosition().getWorld().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_WINDSTREAM_ANNOUNCE(pathType.getId(), worldId, groupid, (onoff ? 1 : 0)));
			}
		});
	}
	
	@Override
	public int hashCode() {
		return 3*groupid + (onoff ? 5 : 0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OnOffWindpathAction) {
			OnOffWindpathAction o = (OnOffWindpathAction) obj;
			return (o.groupid == groupid && o.onoff == onoff);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + groupid + "] --> " + onoff;
	}
	
}

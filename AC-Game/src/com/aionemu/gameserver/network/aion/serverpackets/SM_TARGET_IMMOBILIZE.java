/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 * Aion-Lightning is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Aion-Lightning is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. *
 *
 * You should have received a copy of the GNU General Public License along with Aion-Lightning. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * Credits goes to all Open Source Core Developer Groups listed below Please do not change here something, ragarding the developer credits, except the
 * "developed by XXXX". Even if you edit a lot of files in this source, you still have no rights to call it as "your Core". Everybody knows that this
 * Emulator Core was developed by Aion Lightning
 * 
 * @-Aion-Unique-
 * @-Aion-Lightning
 * @Aion-Engine
 * @Aion-Extreme
 * @Aion-NextGen
 * @Aion-Core Dev.
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.PacketLoggerService;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * @author Sweetkr
 * @modified Yon (Aion Reconstruction Project) -- Added a geoZ check for non player entities to avoid floating mobs.
 */
public class SM_TARGET_IMMOBILIZE extends AionServerPacket {
	
	private Creature creature;
	
	public SM_TARGET_IMMOBILIZE(Creature creature) {
		this.creature = creature;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		if (!(creature instanceof Player)) {
			if (GeoDataConfig.GEO_ENABLE && !creature.isFlyingOrGliding() && creature.getGameStats().checkGeoNeedUpdate()) {
				float z = GeoService.getInstance().getZ(creature.getWorldId(), creature.getX(), creature.getY(), creature.getZ(), 0.5F, creature.getInstanceId());
				creature.setXYZH(null, null, z, null);
			}
		}
		PacketLoggerService.getInstance().logPacketSM(this.getPacketName());
		writeD(creature.getObjectId());
		writeF(creature.getX());
		writeF(creature.getY());
		writeF(creature.getZ());
		writeC(creature.getHeading());
	}
}

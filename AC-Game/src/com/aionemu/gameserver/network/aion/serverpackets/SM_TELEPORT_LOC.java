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

import com.aionemu.gameserver.network.PacketLoggerService;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is used to teleport player and port animation
 *
 * @author Luno, orz, xTz
 * @modified Yon (Aion Reconstruction Project) -- Player target is now removed (server side) on teleporting.
 */
public class SM_TELEPORT_LOC extends AionServerPacket {
	
	private int portAnimation;
	private int mapId;
	private int instanceId;
	private float x, y, z;
	private byte heading;
	private boolean isInstance;
	
	public SM_TELEPORT_LOC(boolean isInstance, int instanceId, int mapId, float x, float y, float z, byte heading, int portAnimation) {
		this.isInstance = isInstance;
		this.instanceId = instanceId;
		this.mapId = mapId;
		this.x = x;
		this.y = y;
		this.z = z;
		this.heading = heading;
		this.portAnimation = portAnimation;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		// When teleporting to a different map the target is cleared client side, server side should clear, too.
		if (con.getActivePlayer() != null) con.getActivePlayer().setTarget(null);
		PacketLoggerService.getInstance().logPacketSM(this.getPacketName());
		writeC(portAnimation); // portAnimation
		writeD(mapId);// new 4.3 NA -->old //writeH(mapId & 0xFFFF);
		writeD(isInstance ? instanceId : mapId); // mapId | instanceId
		writeF(x); // x
		writeF(y); // y
		writeF(z); // z
		writeC(heading); // headling
	}
}

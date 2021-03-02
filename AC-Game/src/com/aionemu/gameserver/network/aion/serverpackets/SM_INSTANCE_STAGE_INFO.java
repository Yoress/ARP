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
 * Note that this should be called SM_SCENE_STATUS ~ Yon
 * 
 * @author xTz
 * @modified Yon (Aion Reconstruction Project) -- Added another constructor for mechanics system.
 */
public class SM_INSTANCE_STAGE_INFO extends AionServerPacket {
	
	private int type;
	private int event;
	private int unk;
	
	public SM_INSTANCE_STAGE_INFO(int type, int event, int unk) {
		this.type = type;
		this.event = event;
		this.unk = unk;
	}
	
	public SM_INSTANCE_STAGE_INFO(final int sceneStatus) {
		this.type = 2; //Not sure what this is, but all other references to this packet use a value of 2.
		
		/*
		 * To keep compatibility with the initial constructor, and not add any extra fields, I'm gonna do some weird shit.
		 * The two shorts being sent in #writeImpl() for #event, and #unk are actually just one int value (sceneStatus).
		 * 
		 * To maintain validity, the bytes have to be rearranged a little to match the byte order of the two shorts written
		 * to the buffer.
		 */
		byte[] bytes = new byte[4];
		//int to byte casts truncate
		bytes[0] = (byte) sceneStatus;
		bytes[1] = (byte) (sceneStatus >>> 8);
		bytes[2] = (byte) (sceneStatus >>> 16);
		bytes[3] = (byte) (sceneStatus >>> 24);
		event = ((bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8)); //& 0xFF changes from byte to int without signed casting
		unk = ((bytes[2] & 0xFF) | ((bytes[3] & 0xFF) << 8));
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		PacketLoggerService.getInstance().logPacketSM(this.getPacketName());
		writeC(type);
		writeD(0);
		writeH(event);
		writeH(unk);
	}
}

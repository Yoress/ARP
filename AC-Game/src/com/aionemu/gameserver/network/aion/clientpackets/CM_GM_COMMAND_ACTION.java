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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.PacketLoggerService;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

public class CM_GM_COMMAND_ACTION extends AionClientPacket {
	
	private int action;
	@SuppressWarnings("unused")
	private int targetObjectId;
	
	/**
	 * @param opcode
	 * @param state
	 * @param restStates
	 */
	public CM_GM_COMMAND_ACTION(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aionemu.commons.network.packet.BaseClientPacket#readImpl()
	 */
	@Override
	protected void readImpl() {
		PacketLoggerService.getInstance().logPacketCM(this.getPacketName());
		action = readC();
		switch (action) {
		case 0:
			targetObjectId = readD();
			break;
		}
//		StringBuilder str = new StringBuilder();
//		if (getRemainingBytes() > 0) {
//			for (int i = 0; i < getRemainingBytes(); i++) {
//				str.append(readC());
//			}
//			str.append("\n");
//		}
//		PacketSendUtility.sendPacket(getConnection().getActivePlayer(), new SM_MESSAGE(getConnection().getActivePlayer(), str.toString(), ChatType.LEGION));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aionemu.commons.network.packet.BaseClientPacket#runImpl()
	 */
	@Override
	protected void runImpl() {
		// TODO Auto-generated method stub
		
	}
	
}

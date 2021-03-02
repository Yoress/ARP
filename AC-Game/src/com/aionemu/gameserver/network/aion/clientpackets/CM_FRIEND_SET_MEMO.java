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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.FriendListDAO;
import com.aionemu.gameserver.model.gameobjects.player.Friend;
import com.aionemu.gameserver.model.gameobjects.player.FriendList;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.PacketLoggerService;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_LIST;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * The packet sent by the client when the user tries to set the memo for a player on their friend list.
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class CM_FRIEND_SET_MEMO extends AionClientPacket {
	
	public CM_FRIEND_SET_MEMO(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	private String friendName;
	private String memo;
	
	@Override
	protected void readImpl() {
		PacketLoggerService.getInstance().logPacketCM(this.getPacketName());
		friendName = readS();
		memo = readS();
	}
	
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null) return;
		FriendList flist = player.getFriendList();
		if (flist != null) {
			Friend friend = flist.getFriend(friendName);
			if (friend != null) {
				friend.setMemo(memo);
				DAOManager.getDAO(FriendListDAO.class).updateMemo(player, friend, memo);
				PacketSendUtility.sendPacket(player, new SM_FRIEND_LIST()); //Not sure if there is a better update option!
			}
		}
	}
	
}

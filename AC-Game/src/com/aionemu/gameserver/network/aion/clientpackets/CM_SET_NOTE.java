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

import com.aionemu.gameserver.model.gameobjects.player.Friend;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.PacketLoggerService;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_NOTE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Received when a player sets his note
 *
 * @author Ben
 * @modified Yon (Aion Reconstruction Project) -- Added a note update packet so players can see the change.
 */
public class CM_SET_NOTE extends AionClientPacket {
	
	private String note;
	
	public CM_SET_NOTE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		PacketLoggerService.getInstance().logPacketCM(this.getPacketName());
		note = readS();
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Player activePlayer = getConnection().getActivePlayer();
		
		if (!note.equals(activePlayer.getCommonData().getNote())) {
			
			activePlayer.getCommonData().setNote(note);
			
			//Update the player's note for everyone in the area
			PacketSendUtility.broadcastPacketAndReceive(activePlayer, new SM_UPDATE_NOTE(activePlayer.getObjectId(), note));
			
			for (Friend friend : activePlayer.getFriendList()) // For all my friends
			{
				Player frienPlayer = friend.getPlayer();
				if (friend.isOnline() && frienPlayer != null) // If the player is online
				{
					friend.getPlayer().getClientConnection().sendPacket(new SM_FRIEND_LIST()); // Send him a new friend list packet
				}
			}
			
		}
	}
}

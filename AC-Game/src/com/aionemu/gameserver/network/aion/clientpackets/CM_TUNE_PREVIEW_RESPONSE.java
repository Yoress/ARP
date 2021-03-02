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

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javolution.util.FastMap;

/**
 * This is the client response to {@link com.aionemu.gameserver.network.aion.serverpackets.SM_TUNE_PREVIEW SM_TUNE_PREVIEW}.
 * <p>
 * This class will maintain a Map of any ongoing tuning actions passed to it by
 * {@link com.aionemu.gameserver.network.aion.serverpackets.SM_TUNE_PREVIEW SM_TUNE_PREVIEW}.
 * An ongoing tune is any tune that can be rejected by the player. If the tune cannot be rejected,
 * then it should not be added to the map.
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class CM_TUNE_PREVIEW_RESPONSE extends AionClientPacket {
	
	private enum TunePreviewResponse {
		
		ACCEPT(1401910), //STR_MSG_ITEM_REIDENTIFY_APPLY_YES; "The returned results have been applied to %0."
		REJECT(1401911); //STR_MSG_ITEM_REIDENTIFY_APPLY_NO; "You aborted the action of applying returned results."
		
		public final int tooltipId;
		
		TunePreviewResponse(int tooltipId) {
			this.tooltipId = tooltipId;
		}
		
		void sendMessage(Player player, int tunedItemNameId) {
			if (this == ACCEPT) {
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(tooltipId, new DescriptionId(tunedItemNameId)));
			} else {
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(tooltipId));
			}
		}
	}
	
	private static FastMap<Player, Item.TuneResults> ongoingTunes;
	
	int tunedItemObjectId;
	TunePreviewResponse response;
	
	public CM_TUNE_PREVIEW_RESPONSE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		tunedItemObjectId = readD();
		response = readC() == 0 ? TunePreviewResponse.REJECT : TunePreviewResponse.ACCEPT; 
	}
	
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		/*
		 * If active player has gone null before we get here, then we won't apply the tune -- method will throw null pointer.
		 * If active player has somehow changed, same thing -- it will be assumed that the result was already applied (list returns null).
		 * The player and their connection shouldn't be null, but if it is we don't have anything we can do.
		 * Ideally, we would return their tuning scroll, I guess?
		 */
		Item tunedItem = player.getInventory().getItemByObjId(tunedItemObjectId);
		if (tunedItem == null) {
			tunedItem = player.getEquipment().getEquippedItemByObjId(tunedItemObjectId);
			if (tunedItem == null) {
				response.sendMessage(player, 0); //Hopefully id 0 doesn't crash the client; this should never happen, anyway. 
				return;
			}
		}
		Item.TuneResults results = removeFromOngoingTunes(player);
		if (response == TunePreviewResponse.ACCEPT) {
			if (results == null) {
//				response.sendMessage(player, tunedItem.getNameId()); //This message would be unclear in this case, don't send
				return; //Job done; client couldn't reject the tune, so it was pre-applied in SM_TUNE_PREVIEW.
			}
			tunedItem.applyTuneResults(results, player); //This will update item in inventory for player
		} else {
			//For some reason (wrong blob in SM packet?), the client won't deduct the tune count if rejected, so update the item
			PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, tunedItem));
		}
		response.sendMessage(player, tunedItem.getNameId());
	}
	
	public static synchronized void addToOngoingTunes(Player player, Item.TuneResults results) {
		if (ongoingTunes == null) {
			ongoingTunes = new FastMap<Player, Item.TuneResults>();
		}
		synchronized (ongoingTunes) {
			ongoingTunes.put(player, results);
		}
	}
	
	public static Item.TuneResults removeFromOngoingTunes(Player player) {
		if (ongoingTunes == null) {
			return null;
		}
		synchronized (ongoingTunes) {
			Item.TuneResults ret = null;
			if (ongoingTunes.containsKey(player)) {
				ret = ongoingTunes.remove(player);
				if (ongoingTunes.size() == 0) {
					ongoingTunes.clear();
					ongoingTunes = null;
				}
			}
			return ret;
		}
	}
}

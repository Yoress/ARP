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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.clientpackets.CM_TUNE_PREVIEW_RESPONSE;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Shows a window to the player detailing the results of item retuning.
 * <p>
 * If the player used an enduring tuning scroll, then the window will only have a confirm button.
 * If the player used a normal tuning scroll, then the window will allow the player to reject the tune.
 * <p>
 * When the player selects a button in the window on the client side,
 * {@link com.aionemu.gameserver.network.aion.clientpackets.CM_TUNE_PREVIEW_RESPONSE CM_TUNE_PREVIEW_RESPONSE}
 * is sent back to the server.
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class SM_TUNE_PREVIEW extends AionServerPacket {
	
	private final Player player;
	private final int itemObjectId;
	private final int tuningScrollId;
	private final Item.TuneResults tuneResults;
	
	public SM_TUNE_PREVIEW(Player player, Item tunedItem, int tuningScrollId) {
		this.player = player;
		this.itemObjectId = tunedItem.getObjectId();
		this.tuningScrollId = tuningScrollId;
		tuneResults = tunedItem.createTuneResults();
		switch (tuningScrollId) {
			case 166200011:
			case 166200012:
			case 166200013:
			case 166200014:
				break;
			default:
				CM_TUNE_PREVIEW_RESPONSE.addToOngoingTunes(player, tuneResults);
		}
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		Item item = player.getInventory().getItemByObjId(itemObjectId);
		if (item == null) {
			item = player.getEquipment().getEquippedItemByObjId(itemObjectId);
			if (item == null) return;
		}
		
		writeD(itemObjectId); //Item to preview results of
		writeD(tuningScrollId); //Item to place in cancel confirmation message
		
		writeH(tuneResults.bonusNumber); //Which tune to display as result
		
		writeC(item.getEnchantLevel()); //Enchant Level of result
		
		writeD(0); //What is this?
		
		writeC(tuneResults.optionalSocket); //Extra Manastones on result
		writeC(tuneResults.bonusEnchantLevel); //Extra Enchant Levels on result
		
		
		//Manastones
		for (ManaStone m: item.getItemStones()) {
			writeD(m.getItemId());
		}
		for (int i = item.getItemStonesSize(); i < tuneResults.getSockets(item); i++) {
			writeD(0);
		}
		switch (tuningScrollId) {
			case 166200011:
			case 166200012:
			case 166200013:
			case 166200014:
				//Enduring Scrolls don't allow the client to reject the tune.
				//Not sure what this data is, but it works for the client?
				//It's likely that not all items work with this, but several do after light testing
				writeB(new byte[126 - (4*tuneResults.getSockets(item))]); //Might need to be a BLOB for updating the item, but I don't know which.
				PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item.applyTuneResults(tuneResults, player)));
				return;
			default: break;
		}
		/*
		 * Not sure if this is the correct data, don't have a retail server to compare to; works okay for the client.
		 * The client rejects the full blob, but might want the data of the tuned result instead of the untuned result.
		 * The currently implemented system does not support changing the item here, as it changes in CM_TUNE_PREVIEW_RESPONSE
		 */
		ItemInfoBlob blob = new ItemInfoBlob(player, item);
		blob.addBlobEntry(ItemBlobType.MANA_SOCKETS);
		if (item.getConditioningInfo() != null) {
			blob.addBlobEntry(ItemBlobType.CONDITIONING_INFO);
		}
		
		blob.addBlobEntry(ItemBlobType.PREMIUM_OPTION);
		if (item.getItemTemplate().isCanPolish()) {
			blob.addBlobEntry(ItemBlobType.POLISH_INFO);
		}
		
		blob.writeMe(getBuf());
	}
}

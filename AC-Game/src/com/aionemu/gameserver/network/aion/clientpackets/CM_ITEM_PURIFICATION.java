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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.PacketLoggerService;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.item.ItemPurificationService;
import com.aionemu.gameserver.services.item.ItemService;

/**
 * @author FinalNovas
 * @rework Navyan
 * @rework Blackfire
 */
public class CM_ITEM_PURIFICATION extends AionClientPacket {
	
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(CM_ITEM_PURIFICATION.class);
	int playerObjectId;
	int upgradedItemObjectId;
	int resultItemId;
	int requireItemObjectId1;
	int requireItemObjectId2;
	int requireItemObjectId3;
	int requireItemObjectId4;
	int requireItemObjectId5;
	Item baseItem;
	
	/**
	 * @param opcode
	 */
	public CM_ITEM_PURIFICATION(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		PacketLoggerService.getInstance().logPacketCM(this.getPacketName());
		Player player = getConnection().getActivePlayer();
		playerObjectId = readD();
		upgradedItemObjectId = readD();
		resultItemId = readD();
		
		requireItemObjectId1 = readD();
		requireItemObjectId2 = readD();
		requireItemObjectId3 = readD();
		requireItemObjectId4 = readD();
		requireItemObjectId5 = readD();
		
		Storage inventory = player.getInventory();
		baseItem = inventory.getItemByObjId(upgradedItemObjectId);
	}
	
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null) return;
		if (!ItemPurificationService.checkItemUpgrade(player, baseItem, resultItemId)) {
			return;
		}
		Item resultItem = ItemService.newItem(resultItemId, 1, null, 0, 0, 0);
		
		if (!ItemPurificationService.decreaseMaterial(player, baseItem, resultItemId)) {
			return;
		}
		ItemService.makeUpgradeItem(player, baseItem, resultItem);
	}
}

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
package com.aionemu.gameserver.services.item;

import static com.aionemu.gameserver.services.item.ItemPacketService.sendItemDeletePacket;
import static com.aionemu.gameserver.services.item.ItemPacketService.sendStorageUpdatePacket;

import java.util.Collection;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.IStorage;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.ItemCategory;
import com.aionemu.gameserver.services.ExchangeService;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemDeleteType;

/**
 * @author ATracer
 * @modified Yon (Aion Reconstruction Project) -- Power shards and arrows now correctly stack with any equipped stacks first
 * when {@link #moveItem(Player, int, byte, byte, short) moved}.
 */
public class ItemMoveService {
	
	public static void moveItem(Player player, int itemObjId, byte sourceStorageType, byte destinationStorageType, short slot) {
		if (ExchangeService.getInstance().isPlayerInExchange(player)) {
			return;
		}
		
		IStorage sourceStorage = player.getStorage(sourceStorageType);
		Item item = player.getStorage(sourceStorageType).getItemByObjId(itemObjId);
		
		if (item == null) {
			return;
		}
		
		if (sourceStorageType == destinationStorageType) {
			if (item.getEquipmentSlot() != slot) {
				moveInSameStorage(sourceStorage, item, slot);
			}
			return;
		}
		
		if (sourceStorageType != destinationStorageType
				&& (ItemRestrictionService.isItemRestrictedTo(player, item, destinationStorageType) || ItemRestrictionService.isItemRestrictedFrom(player, item, sourceStorageType))) {
			sendStorageUpdatePacket(player, StorageType.getStorageTypeById(sourceStorageType), item);
			return;
		}
		IStorage targetStorage = player.getStorage(destinationStorageType);
		LegionService.getInstance().addWHItemHistory(player, item.getItemId(), item.getItemCount(), sourceStorage, targetStorage);
		boolean allEquipped = false;
		if (slot == -1) {
			if (item.getItemTemplate().isStackable()) {
				
				if ((item.getItemTemplate().getCategory() == ItemCategory.SHARD || item.getItemTemplate().getArmorType() == ArmorType.ARROW) && destinationStorageType == 0) {
					Equipment equipment = player.getEquipment();
					Collection<Item> items = equipment.getEquippedItemsByItemId(item.getItemId());
					for (Item i : items) {
						if (item.getItemCount() == 0) {
							break;
						}
						item.setItemCount(equipment.increaseEquippedItemCount(i, item.getItemCount()));
						if (item.getItemCount() == 0) allEquipped = true;
					}
				}
				
				List<Item> sameItems = targetStorage.getItemsByItemId(item.getItemId());
				for (Item sameItem : sameItems) {
					long itemCount = item.getItemCount();
					if (itemCount == 0) {
						break;
					}
					// we can merge same stackable items
					ItemSplitService.mergeStacks(sourceStorage, targetStorage, item, sameItem, itemCount);
				}
			}
		}
		if (!targetStorage.isFull() && (item.getItemCount() > 0 || allEquipped)) {
			sourceStorage.remove(item);
			sendItemDeletePacket(player, StorageType.getStorageTypeById(sourceStorageType), item, ItemDeleteType.MOVE);
			if (!allEquipped) {
				item.setEquipmentSlot(slot);
				targetStorage.add(item);
			}
		}
	}
	
	/**
	 * @param storage
	 * @param item
	 * @param slot
	 */
	private static void moveInSameStorage(IStorage storage, Item item, short slot) {
		storage.setPersistentState(PersistentState.UPDATE_REQUIRED);
		item.setEquipmentSlot(slot);
		item.setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	public static void switchItemsInStorages(Player player, byte sourceStorageType, int sourceItemObjId, byte replaceStorageType, int replaceItemObjId) {
		IStorage sourceStorage = player.getStorage(sourceStorageType);
		IStorage replaceStorage = player.getStorage(replaceStorageType);
		
		Item sourceItem = sourceStorage.getItemByObjId(sourceItemObjId);
		if (sourceItem == null) {
			return;
		}
		
		Item replaceItem = replaceStorage.getItemByObjId(replaceItemObjId);
		if (replaceItem == null) {
			return;
		}
		
		// restrictions checks
		if (ItemRestrictionService.isItemRestrictedFrom(player, sourceItem, sourceStorageType) || ItemRestrictionService.isItemRestrictedFrom(player, replaceItem, replaceStorageType)
				|| ItemRestrictionService.isItemRestrictedTo(player, sourceItem, replaceStorageType) || ItemRestrictionService.isItemRestrictedTo(player, replaceItem, sourceStorageType)) {
			return;
		}
		
		long sourceSlot = sourceItem.getEquipmentSlot();
		long replaceSlot = replaceItem.getEquipmentSlot();
		
		sourceItem.setEquipmentSlot(replaceSlot);
		replaceItem.setEquipmentSlot(sourceSlot);
		
		sourceStorage.remove(sourceItem);
		replaceStorage.remove(replaceItem);
		
		// correct UI update order is 1)delete items 2) add items
		sendItemDeletePacket(player, StorageType.getStorageTypeById(sourceStorageType), sourceItem, ItemDeleteType.MOVE);
		sendItemDeletePacket(player, StorageType.getStorageTypeById(replaceStorageType), replaceItem, ItemDeleteType.MOVE);
		sourceStorage.add(replaceItem);
		replaceStorage.add(sourceItem);
	}
}

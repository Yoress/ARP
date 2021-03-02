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
package admincommands;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.stats.listeners.ItemEquipmentListener;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.services.item.ItemSocketService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;


/**
 * A simple command to socket manastones into equipped gear.
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class Manastone extends AdminCommand {
	
	public Manastone() {
		super("manastone");
	}
	
	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 1) {
			onFail(admin, "Syntax: //manastone <ManastoneId | 0>"
				+ "\nThis command simply flood fills all empty equipped sockets with the given manastone (or removes them if ManastoneId is zero).");
		}
		int manastone = 0;
		try {
			manastone = Integer.parseInt(params[0]);
		} catch (NumberFormatException e) {
			onFail(admin, "Unable to parse input: " + e.getMessage());
			return;
		}
		
		if (manastone != 0 && (manastone < 167000000 || manastone > 168000000)) {
			onFail(admin, "You are suposed to give the item id for a Manastone or 0 to remove all manastones.");
			return;
		}
		
		for (Item targetItem : admin.getEquipment().getEquippedItemsWithoutStigmaOld()) {
			if (isUpgradeble(targetItem)) {
				if (manastone == 0) {
					ItemEquipmentListener.removeStoneStats(targetItem.getItemStones(), admin.getGameStats());
					ItemSocketService.removeAllManastone(admin, targetItem);
				} else {
					int counter = getMaxSlots(targetItem);
					while (counter > 0) {
						ManaStone manaStone = ItemSocketService.addManaStone(targetItem, manastone);
						ItemEquipmentListener.addStoneStats(targetItem, manaStone, admin.getGameStats());
						counter--;
					}
				}
				if (targetItem.hasFusionedItem()) {
					if (manastone == 0) {
						ItemEquipmentListener.removeStoneStats(targetItem.getFusionStones(), admin.getGameStats());
						ItemSocketService.removeAllFusionStone(admin, targetItem);
					} else {
						int counter = targetItem.getSockets(true) - targetItem.getFusionStonesSize();
						while (counter > 0) {
							ManaStone manaStone = ItemSocketService.addFusionStone(targetItem, manastone);
							ItemEquipmentListener.addStoneStats(targetItem, manaStone, admin.getGameStats());
							counter--;
						}
					}
				}
				PacketSendUtility.sendPacket(admin, new SM_STATS_INFO(admin));
				ItemPacketService.updateItemAfterInfoChange(admin, targetItem);
				targetItem.setPersistentState(PersistentState.UPDATE_REQUIRED);
			}
			
		}
	}
	
	/**
	 * Verify if the item is enchantble and/or socketble
	 *
	 * @param item
	 * @author Tago modified by Wakizashi
	 */
	public static boolean isUpgradeble(Item item) {
		if (item.getItemTemplate().isNoEnchant()) {
			return false;
		}
		if (item.getItemTemplate().isWeapon()) {
			return true;
		}
		if (item.getItemTemplate().isArmor()) {
			int at = item.getItemTemplate().getItemSlot();
			if (at == 1 || /* Main Hand */ at == 2 || /* Sub Hand */ at == 8 || /* Jacket */ at == 16 || /* Gloves */ at == 32 || /* Boots */ at == 2048 || /* Shoulder */ at == 4096
					|| /* Pants */ at == 131072 || /* Main Off Hand */ at == 262144) /* Sub Off Hand */ {
				return true;
			}
		}
		return false;
	}
	
	public static int getMaxSlots(Item item) {
		return item.getSockets(false) - item.getItemStonesSize();
	}
	
}

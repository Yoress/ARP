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
package com.aionemu.gameserver.network.aion.gmhandler;

import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemQuality;
import com.aionemu.gameserver.network.aion.clientpackets.CM_GM_COMMAND_SEND;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemDeleteType;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This is a GM command to remove items from their own inventory. To avoid any potential abuse,
 * the ability to interfere with other player's items has not been implemented.
 * <p>
 * This command requires a numeric parameter in the form of a String. This number must fall within
 * the range [0, 6]. If an unsupported parameter is passed in, the GM will receive a notification.
 * The parameter is passed into {@link ItemQuality#getItemQuality(int)}, and the resulting
 * {@link ItemQuality} is used as a {@link ItemQuality#equalOrHigherTierThan(ItemQuality) filter}
 * to delete items in the inventory. All items of the given {@link ItemQuality} and lower are deleted.
 * <p>
 * This is not meant to be used outside of {@link CM_GM_COMMAND_SEND}.
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class CmdDeleteItems extends AbstractGMHandler {
	
	/**
	 * Constructor for {@link CmdDeleteItems}. Calls {@link #run()}.
	 * <p>
	 * This is not meant to be used outside of {@link CM_GM_COMMAND_SEND}.
	 * 
	 * @param admin -- the GM executing the command.
	 * @param params -- a numeric parameter in the form of a String.
	 */
	public CmdDeleteItems(Player admin, String params) {
		super(admin, params);
		run();
	}
	
	/**
	 * This method executes the command. Refer to the {@link CmdDeleteItems Class Documentation}.
	 */
	private void run() {
		ThreadPoolManager.getInstance().submit(new Runnable() {
			@Override
			public void run() {
				try {
				ItemQuality rarity = ItemQuality.getItemQuality(Integer.parseInt(params));
				if (rarity == null) {
					PacketSendUtility.sendMessage(admin, "Invalid command parameter: " + params);
					return;
				}
				for (Item i: admin.getInventory().getItems()) {
					if (rarity.equalOrHigherTierThan(i.getItemTemplate().getItemQuality())) {
						admin.getInventory().delete(i, ItemDeleteType.DISCARD);
					}
				}
				} catch (NumberFormatException e) {
					LoggerFactory.getLogger("ADMINAUDIT_LOG")
						.error("Missuse of " + CmdDeleteItems.class.getName() + ", expected numeric parameter. Got: " + params);
				}
			}
		});
	}
	
}

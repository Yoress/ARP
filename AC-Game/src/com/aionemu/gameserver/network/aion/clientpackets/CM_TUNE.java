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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.actions.TuningAction;
import com.aionemu.gameserver.network.PacketLoggerService;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author xTz
 * @modified Yon (Aion Reconstruction Project) -- For lack of a better option (no retail server to reverse from),
 * added a very hackish way to make the client play the tuning animation; allowed equipped items to be retuned.
 */
public class CM_TUNE extends AionClientPacket {
	
	private int itemObjectId, tuningScrollId;
	
	public CM_TUNE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		PacketLoggerService.getInstance().logPacketCM(this.getPacketName());
		itemObjectId = readD();
		tuningScrollId = readD();
	}
	
	@Override
	protected void runImpl() {
		//FIXME: Check if player is in combat; cannot tune or retune in combat (or weapon is drawn).
		final Player player = getConnection().getActivePlayer();
		if (player == null) {
			return;
		}
		Storage inventory = player.getInventory();
		Item fitem = inventory.getItemByObjId(itemObjectId);
		if (fitem == null && tuningScrollId != 0) {
			fitem = player.getEquipment().getEquippedItemByObjId(itemObjectId);
			if (fitem == null)
				return;
		}
		final Item item = fitem;
		if (tuningScrollId != 0) {
			final Item tuningItem = inventory.getItemByObjId(tuningScrollId);
			if (tuningItem == null) {
				return;
			}
			TuningAction action = tuningItem.getItemTemplate().getActions().getTuningAction();
			if (action != null && action.canAct(player, tuningItem, item)) {
				action.act(player, tuningItem, item);
			}
			return;
		}
		if (item.getOptionalSocket() != -1) {
			return;
		}
		//166200022 == Mythic Armor Tuning Scroll, client will use the tuning animation from it.
		final int itemId = 166200022; //item.getItemId(); //FIXME: Very hackish way to make the client play the tuning animation!
		final ItemTemplate template = item.getItemTemplate();
		final int nameId = template.getNameId();
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), itemId, 5000, 0, 0), true);
		final ItemUseObserver observer = new ItemUseObserver() {
			//TODO: Fix System message to check if they are retune or tune
			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.removeItemCoolDown(template.getUseLimits().getDelayId());
				//STR_ITEM_REIDENTIFY_CANCELED "Canceled reidentification of %0.": 1401603 (yeah, bad L10N) TODO: Add to SM_SYSTEM_MESSAGE as a method
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401603, new DescriptionId(nameId)));
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjectId, itemId, 0, 3, 0), true);
				player.getObserveController().removeObserver(this);
			}
		};
		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
			//TODO: Fix System message to check if they are retune or tune
			@Override
			public void run() {
				if (item.getOptionalSocket() != -1) {
					return;
				}
				player.getObserveController().removeObserver(observer);
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjectId, itemId, 0, 1, 1), true);
				
				item.setOptionalSocket(Rnd.get(0, item.getItemTemplate().getOptionSlotBonus()));
				item.setRndBonus();
				item.setPersistentState(PersistentState.UPDATE_REQUIRED);
				PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
				//STR_ITEM_REIDENTIFY_SUCCEED "You identified %0.": 1401604 TODO: Add to SM_SYSTEM_MESSAGE as a method
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401604, new DescriptionId(nameId)));
			}
		}, 5000));
		
	}
}

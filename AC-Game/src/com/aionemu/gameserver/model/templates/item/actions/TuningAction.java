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
package com.aionemu.gameserver.model.templates.item.actions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TUNE_PREVIEW;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author Rolandas
 * @reworked Yon (Aion Reconstruction Project) -- changed the abort method's animation packet to use end state 3 instead of 2.
 * Prevented the removal of filled manastone slots on retune, prevented tune count decrement when using an Enduring-type scroll.
 * Reworked {@link #canAct(Player, Item, Item)} to check all conditions, reworked {@link #act(Player, Item, Item)} to use
 * {@link SM_TUNE_PREVIEW}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TuningAction")
public class TuningAction extends AbstractItemAction {
	
	@XmlAttribute
	UseTarget target;
	
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (!targetItem.getItemTemplate().isWeapon() && !targetItem.getItemTemplate().isArmor()) {
			return false;
		}
		
		if (target.equals(UseTarget.WEAPON) && !targetItem.getItemTemplate().isWeapon()) {
			//Wrong scroll type for item
			//STR_MSG_ITEM_REIDENTIFY_WRONG_SELECT "%0 cannot retune %1.": 1401633 TODO: Add to SM_SYSTEM_MESSAGE as a method
			PacketSendUtility.sendPacket(player,
					new SM_SYSTEM_MESSAGE(1401633, new DescriptionId(parentItem.getNameId()), new DescriptionId(targetItem.getNameId())));
			return false;
		}
		
		if (target.equals(UseTarget.ARMOR) && !targetItem.getItemTemplate().isArmor()) {
			//Wrong scroll type for item
			//STR_MSG_ITEM_REIDENTIFY_WRONG_SELECT "%0 cannot retune %1.": 1401633 TODO: Add to SM_SYSTEM_MESSAGE as a method
			PacketSendUtility.sendPacket(player,
					new SM_SYSTEM_MESSAGE(1401633, new DescriptionId(parentItem.getNameId()), new DescriptionId(targetItem.getNameId())));
			return false;
		}
		
		if (targetItem.hasFusionedItem()) {
			//Tune count should be removed upon fusion!
			return false;
		}
		
		if (targetItem.getItemStonesSize() > 0) {
			//Tune count should be removed upon socketing!
			return false;
		}
		
		if (targetItem.getEnchantLevel() > 0) {
			//Tune count should be removed upon enchantment!
			return false;
		}
		
		if (!parentItem.getItemTemplate().getItemQuality().equalOrHigherTierThan(targetItem.getItemTemplate().getItemQuality())) {
			//Wrong scroll rarity for item
			//STR_MSG_ITEM_REIDENTIFY_WRONG_QUALITY "You cannot retune %1 because %0 has a lower rank.": 1401634 TODO: Add to SM_SYSTEM_MESSAGE as a method
			PacketSendUtility.sendPacket(player,
					new SM_SYSTEM_MESSAGE(1401634, new DescriptionId(parentItem.getNameId()), new DescriptionId(targetItem.getNameId())));
			return false;
		}
		
		if (parentItem.getItemTemplate().getLevel() < targetItem.getItemTemplate().getLevel()) {
			//Wrong scroll level range
			//STR_MSG_ITEM_REIDENTIFY_WRONG_LEVEL "You cannot retune %1 because %0 has a lower level.": 1401635 TODO: Add to SM_SYSTEM_MESSAGE as a method
			PacketSendUtility.sendPacket(player,
					new SM_SYSTEM_MESSAGE(1401635, new DescriptionId(parentItem.getNameId()), new DescriptionId(targetItem.getNameId())));
			return false;
		}
		
		if (targetItem.getRandomCount() >= targetItem.getItemTemplate().getRandomBonusCount()) {
			//No more tunes available -- Should be handled client side, send message anyway.
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_REIDENTIFY_CANNOT_REIDENTIFY(targetItem.getNameId()));
			return false;
		}
		
		return true;
	}
	
	@Override
	public void act(final Player player, final Item parentItem, final Item targetItem) {
		final int parentItemId = parentItem.getItemId();
		final int parentObjectId = parentItem.getObjectId();
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItemId, 5000, 0, 0), true);
		final ItemUseObserver observer = new ItemUseObserver() {
			
			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.removeItemCoolDown(parentItem.getItemTemplate().getUseLimits().getDelayId());
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_REIDENTIFY_CANCELED(targetItem.getNameId()));
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentObjectId, parentItemId, 0, 3, 0), true);
				player.getObserveController().removeObserver(this);
			}
		};
		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
			
			@Override
			public void run() {
				player.getObserveController().removeObserver(observer);
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentObjectId, parentItemId, 0, 1, 1), true);
				if (!player.getInventory().decreaseByObjectId(parentObjectId, 1)) {
					return;
				}
				switch (parentItem.getItemId()) {
					case 166200011:
					case 166200012:
					case 166200013:
					case 166200014:
						//Enduring Scrolls; don't reduce number of available tunes or allow the client to reject the results.
						break;
					default:
						targetItem.setRandomCount(targetItem.getRandomCount() + 1);
						targetItem.setPersistentState(PersistentState.UPDATE_REQUIRED);
				}
				//Send message for completion
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_REIDENTIFY_SUCCEED(targetItem.getNameId()));
				//All item modifications are handled in the packet because normal scrolls allow rejection of the result.
				PacketSendUtility.sendPacket(player, new SM_TUNE_PREVIEW(player, targetItem, parentItemId));
			}
		}, 5000));
	}
}

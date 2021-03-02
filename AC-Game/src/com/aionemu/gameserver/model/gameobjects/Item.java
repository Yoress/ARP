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
package com.aionemu.gameserver.model.gameobjects;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.IExpirable;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ChargeInfo;
import com.aionemu.gameserver.model.items.GodStone;
import com.aionemu.gameserver.model.items.IdianStone;
import com.aionemu.gameserver.model.items.ItemMask;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.items.RandomBonusResult;
import com.aionemu.gameserver.model.items.RandomStats;
import com.aionemu.gameserver.model.items.storage.IStorage;
import com.aionemu.gameserver.model.items.storage.ItemStorage;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.templates.item.EquipType;
import com.aionemu.gameserver.model.templates.item.Improvement;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.actions.DyeAction;
import com.aionemu.gameserver.model.templates.item.actions.ItemActions;
import com.aionemu.gameserver.model.templates.item.bonuses.StatBonusType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer, Wakizashi, xTz
 * @author GiGatR00n
 * @reworked Blackfire
 * @modified Yon (Aion Reconstruction Project) -- {@link #setRndBonus()} modified to adjust random max enchant level.
 * {@link #setFusionedItem(ItemTemplate)} modified to remove all remaining retune attempts on the item.
 * Added {@link #maxEnchantLevel} (and adjusted constructors and callers), {@link #getMaxEnchantLevel()},
 * {@link #getBonusEnchantLevel()}, {@link #createTuneResults()}, and
 * {@link #applyTuneResults(TuneResults, Player)}. Created {@link Item.TuneResults TuneResults}. Updated database constructor
 * so that it cannot throw null pointer exceptions.
 */
public class Item extends AionObject implements IExpirable, StatOwner {
	
	private final Logger log = LoggerFactory.getLogger(Item.class);
	private long itemCount = 1;
	private int itemColor = 0;
	private int colorExpireTime = 0;
	private String itemCreator;
	private ItemTemplate itemTemplate;
	private ItemTemplate itemSkinTemplate;
	private ItemTemplate fusionedItemTemplate;
	private boolean isEquipped = false;
	private long equipmentSlot = ItemStorage.FIRST_AVAILABLE_SLOT;
	private PersistentState persistentState;
	private Set<ManaStone> manaStones;
	private Set<ManaStone> fusionStones;
	private int optionalSocket;
	private int optionalFusionSocket;
	private GodStone godStone;
	private IdianStone idianStone;
	private boolean isSoulBound = false;
	private int itemLocation;
	private int enchantLevel;
	/**
	 * This item's real max enchant level.
	 * The value found in the {@link ItemTemplate#getMaxEnchantLevel() item template}
	 * is only the base value and does not include the bonus levels acquired
	 * from tuning the item.
	 */
	private int maxEnchantLevel;
	private int expireTime = 0;
	private int temporaryExchangeTime = 0;
	private long repurchasePrice;
	private int activationCount = 0;
	private ChargeInfo conditioningInfo;
	private int bonusNumber = 0;
	private List<StatFunction> currentModifiers;
	private RandomStats randomStats;
	private int rndCount;
	public static int MAX_BASIC_STONES = 6;
	private int packCount;
	private int authorize;
	private boolean isPacked = false;
	private boolean isAmplified = false;
	private int buffSkill;
	
	/**
	 * Create simple item with minimum information
	 */
	public Item(int objId, ItemTemplate itemTemplate) {
		super(objId);
		this.itemTemplate = itemTemplate;
		this.activationCount = itemTemplate.getActivationCount();
		if (itemTemplate.getExpireTime() != 0) {
			expireTime = ((int) (System.currentTimeMillis() / 1000) + itemTemplate.getExpireTime() * 60) - 1;
		}
		int optionSlotBonus = itemTemplate.getOptionSlotBonus();
		if (optionSlotBonus != 0) {
			optionalSocket = -1;
		}
		this.maxEnchantLevel = itemTemplate.getMaxEnchantLevel();
		this.persistentState = PersistentState.NEW;
		updateChargeInfo(0);
	}
	
	/**
	 * This constructor should be called from ItemService for newly created items and loadedFromDb
	 */
	public Item(int objId, ItemTemplate itemTemplate, long itemCount, boolean isEquipped, long equipmentSlot) {
		this(objId, itemTemplate);
		this.itemCount = itemCount;
		this.isEquipped = isEquipped;
		this.equipmentSlot = equipmentSlot;
	}
	
	/**
	 * This constructor should be called only from DAO while loading from DB
	 */
	public Item(int objId, int itemId, long itemCount, int itemColor, int colorExpires, String itemCreator, int expireTime, int activationCount, boolean isEquipped, boolean isSoulBound, long equipmentSlot, int itemLocation, int enchant, int maxEnchant, int itemSkin, int fusionedItem, int optionalSocket, int optionalFusionSocket, int charge, int randomBonus, int rndCount, int packCount, int authorize, boolean isPacked, boolean isAmplified, int buffSkill) {
		super(objId);
		/*
		 * Do not implement code that allows for a null pointer to be thrown.
		 */
		this.itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		this.itemCount = itemCount;
		this.itemColor = itemColor;
		this.colorExpireTime = colorExpires;
		this.itemCreator = itemCreator;
		this.expireTime = expireTime;
		this.activationCount = activationCount;
		this.isEquipped = isEquipped;
		this.isSoulBound = isSoulBound;
		this.equipmentSlot = equipmentSlot;
		this.itemLocation = itemLocation;
		this.enchantLevel = enchant;
		if (itemTemplate != null && maxEnchant < itemTemplate.getMaxEnchantLevel()) {
			//Old item in database
			this.maxEnchantLevel = itemTemplate.getMaxEnchantLevel();
		} else {
			this.maxEnchantLevel = maxEnchant;
		}
		this.fusionedItemTemplate = DataManager.ITEM_DATA.getItemTemplate(fusionedItem);
		this.itemSkinTemplate = DataManager.ITEM_DATA.getItemTemplate(itemSkin);
		this.optionalSocket = optionalSocket;
		this.optionalFusionSocket = optionalFusionSocket;
		this.bonusNumber = randomBonus;
		this.rndCount = rndCount;
		this.packCount = packCount;
		this.authorize = authorize;
		this.isPacked = isPacked;
		this.isAmplified = isAmplified;
		this.buffSkill = buffSkill;
		if (itemTemplate != null && itemTemplate.getRandomBonusId() != 0 && bonusNumber > 0) {
			randomStats = new RandomStats(itemTemplate.getRandomBonusId(), bonusNumber);
		}
		if (itemTemplate != null && fusionedItemTemplate != null) {
			if (!itemTemplate.isCanFuse() || !itemTemplate.isTwoHandWeapon() || !fusionedItemTemplate.isCanFuse() || !fusionedItemTemplate.isTwoHandWeapon()) {
				this.fusionedItemTemplate = null;
				this.optionalFusionSocket = 0;
			}
		}
		updateChargeInfo(charge);
	}
	
	public final boolean setRndBonus() {
		int setId = itemTemplate.getRandomBonusId();
		if (setId > 0) {
			//Tuning can also have random enchant levels
			if (itemTemplate.getMaxEnchantBonus() > 0 && !isAmplified) {
				int ol; //Overleveled enchant -- Not amplified, but based on bonus enchants compared to base enchants
				if ((ol = enchantLevel - itemTemplate.getMaxEnchantLevel()) > 0) {
					this.maxEnchantLevel = itemTemplate.getMaxEnchantLevel() + Rnd.get(ol, itemTemplate.getMaxEnchantBonus());
				} else {
					this.maxEnchantLevel = itemTemplate.getMaxEnchantLevel() + Rnd.get(0, itemTemplate.getMaxEnchantBonus());
				}
			}
			RandomBonusResult bonus = DataManager.ITEM_RANDOM_BONUSES.getRandomModifiers(StatBonusType.INVENTORY, setId);
			if (bonus != null) {
				bonusNumber = bonus.getTemplateNumber();
				randomStats = new RandomStats(itemTemplate.getRandomBonusId(), bonusNumber);
				return true;
			}
		}
		return false;
	}
	
	public static class TuneResults {
		
		public final int itemId;
		public final int bonusEnchantLevel;
		public final int optionalSocket;
		public final int bonusNumber;
		public final RandomStats randomStats;
		
		private TuneResults(int itemId, int bonusEnchantLevel, int optionalSocket, int bonusNumber, RandomStats randomStats) {
			this.itemId = itemId;
			this.bonusEnchantLevel = bonusEnchantLevel;
			this.optionalSocket = optionalSocket;
			this.bonusNumber = bonusNumber;
			this.randomStats = randomStats;
		}
		
		public int getSockets(Item item) {
			if (item.getItemId() != itemId) {
				throw new IllegalArgumentException("Item/TuneResults mismatch: " + itemId + " vs " + item.getItemId());
			}
			return item.itemTemplate.getManastoneSlots() + optionalSocket;
		}
		
	}
	
	public final TuneResults createTuneResults() {
		if (itemTemplate.getRandomBonusId() == 0) {
			throw new IllegalArgumentException("Cannot create TuneResults for this item.");
		}
		int itemId = getItemId();
		int bonusEnchantLevel = getBonusEnchantLevel();
		int optionalSocket;
		int bonusNumber = 0;
		RandomStats randomStats = null;
		//Tuning can also have random enchant levels
		if (itemTemplate.getMaxEnchantBonus() > 0 && !isAmplified) {
			int ol; //Overleveled enchant -- Not amplified, but based on bonus enchants compared to base enchants
			if ((ol = enchantLevel - itemTemplate.getMaxEnchantLevel()) > 0) {
				bonusEnchantLevel = Rnd.get(ol, itemTemplate.getMaxEnchantBonus());
			} else {
				bonusEnchantLevel = Rnd.get(0, itemTemplate.getMaxEnchantBonus());
			}
		}
		RandomBonusResult bonus = DataManager.ITEM_RANDOM_BONUSES.getRandomModifiers(StatBonusType.INVENTORY, itemTemplate.getRandomBonusId());
		if (bonus != null) {
			bonusNumber = bonus.getTemplateNumber();
			randomStats = new RandomStats(itemTemplate.getRandomBonusId(), bonusNumber);
		}
		int extraSlotedSlotCount = getItemStonesSize() - itemTemplate.getManastoneSlots();
		if (extraSlotedSlotCount > 0) {
			//If the player slotted manastones into extra slots, don't chance at losing any slots
			optionalSocket = Rnd.get(extraSlotedSlotCount, itemTemplate.getOptionSlotBonus());
		} else {
			optionalSocket = Rnd.get(0, itemTemplate.getOptionSlotBonus());
		}
		return new TuneResults(itemId, bonusEnchantLevel, optionalSocket, bonusNumber, randomStats);
	}
	
	public final Item applyTuneResults(TuneResults results, Player player) {
		if (this.getItemId() != results.itemId) {
			throw new IllegalArgumentException("Cannot apply TuneResults, item ID conflict: " + getItemId() + " vs " + results.itemId);
		}
		this.maxEnchantLevel = itemTemplate.getMaxEnchantLevel() + results.bonusEnchantLevel;
		this.optionalSocket = results.optionalSocket;
		this.bonusNumber = results.bonusNumber;
		this.randomStats = null;
		this.randomStats = results.randomStats;
		this.setPersistentState(PersistentState.UPDATE_REQUIRED);
		PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, this));
		return this;
	}
	
	private void updateChargeInfo(int charge) {
		int chargeLevel = getChargeLevelMax();
		if (conditioningInfo == null && chargeLevel > 0) {
			this.conditioningInfo = new ChargeInfo(charge, this);
		}
		// when break fusioned item and second item has conditioned info - set to null
		if (conditioningInfo != null && chargeLevel == 0) {
			this.conditioningInfo = null;
		}
	}
	
	/**
	 * TODO: Fix shit name -- who wrote this crap? (doc added by me for clarity)
	 * <p>
	 * Checks if this item needs to be tuned or not, and returns true if so.
	 * 
	 * @return true if this item has not been tuned, false otherwise.
	 */
	public boolean hasTune() {
		// Way #1
		// if (!this.itemTemplate.isWeapon() || !this.itemTemplate.isArmor()) {
		// return false;
		// }
		// return getRandomCount() < this.itemTemplate.getRandomBonusCount() &&
		// !isEquipped();
		// Way #2
		if (getOptionalSocket() == -1) {
			return true;
		}
		return false;
	}
	
	@Override
	public String getName() {
		//TODO: item description should return probably string and not id
		return String.valueOf(itemTemplate.getName());
	}
	
	/**
	 * @param return itemCreator
	 */
	public String getItemCreator() {
		if (itemCreator == null) {
			return StringUtils.EMPTY;
		}
		return itemCreator;
	}
	
	/**
	 * @param itemCreator the itemCreator to set
	 */
	public void setItemCreator(String itemCreator) {
		this.itemCreator = itemCreator;
	}
	
	public String getItemName() {
		return itemTemplate.getName();
	}
	
	public int getOptionalSocket() {
		return optionalSocket;
	}
	
	public void setOptionalSocket(int optionalSocket) {
		this.optionalSocket = optionalSocket;
	}
	
	public boolean hasOptionalSocket() {
		return optionalSocket != 0;
	}
	
	public int getOptionalFusionSocket() {
		return optionalFusionSocket;
	}
	
	public boolean hasOptionalFusionSocket() {
		return optionalFusionSocket != 0;
	}
	
	public void setOptionalFusionSocket(int optionalFusionSocket) {
		this.optionalFusionSocket = optionalFusionSocket;
	}
	
	/**
	 * @return the itemTemplate
	 */
	public ItemTemplate getItemTemplate() {
		return itemTemplate;
	}
	
	/**
	 * @return the itemAppearanceTemplate
	 */
	public ItemTemplate getItemSkinTemplate() {
		if (this.itemSkinTemplate == null) {
			return this.itemTemplate;
		}
		return this.itemSkinTemplate;
	}
	
	public void setItemSkinTemplate(ItemTemplate newTemplate) {
		this.itemSkinTemplate = newTemplate;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	public boolean isSkinnedItem() {
		return getItemSkinTemplate() != this.itemTemplate;
	}
	
	/**
	 * @return the itemColor - color value if negative
	 */
	public int getItemColor() {
		DyeAction dyeAction = getDyeAction();
		return dyeAction != null ? dyeAction.getColor() : itemColor;
	}
	
	private DyeAction getDyeAction() {
		if (itemColor < 0) {
			return null;
		}
		ItemTemplate dyeTemplate = DataManager.ITEM_DATA.getItemTemplate(itemColor);
		if (dyeTemplate == null) {
			return null;
		}
		
		ItemActions actions = dyeTemplate.getActions();
		if (actions == null) {
			return null;
		}
		
		return actions.getDyeAction();
	}
	
	/**
	 * @param itemColor the itemColor to set (color value or coloring item id)
	 */
	public void setItemColor(int coloringItemId) {
		this.itemColor = coloringItemId;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * @return positive values if not expired, 0 for not expirable, negative for expired
	 */
	public int getColorTimeLeft() {
		if (colorExpireTime == 0) {
			return 0;
		}
		return (int) (colorExpireTime - System.currentTimeMillis() / 1000);
	}
	
	public int getColorExpireTime() {
		return colorExpireTime;
	}
	
	public void setColorExpireTime(int dyeRemainsUntil) {
		this.colorExpireTime = dyeRemainsUntil;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * @return the itemCount Number of this item in stack. Should be not more than template maxstackcount ?
	 */
	public long getItemCount() {
		return itemCount;
	}
	
	public long getFreeCount() {
		return itemTemplate.getMaxStackCount() - itemCount;
	}
	
	/**
	 * @param itemCount the itemCount to set
	 */
	public void setItemCount(long itemCount) {
		this.itemCount = itemCount;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * This method should be called ONLY from Storage class In all other ways it is not guaranteed to be udpated in a regular update service It is
	 * allowed to use this method for newly created items which are not yet in any storage
	 *
	 * @param count
	 * @param left count
	 */
	public long increaseItemCount(long count) {
		if (count <= 0) {
			return 0;
		}
		long cap = itemTemplate.getMaxStackCount();
		long addCount = this.itemCount + count > cap ? cap - this.itemCount : count;
		if (addCount != 0) {
			this.itemCount += addCount;
			setPersistentState(PersistentState.UPDATE_REQUIRED);
		}
		return count - addCount;
	}
	
	/**
	 * This method should be called ONLY from Storage class In all other ways it is not guaranteed to be udpated in a regular update service It is
	 * allowed to use this method for newly created items which are not yet in any storage
	 *
	 * @param count
	 * @param left count
	 */
	public long decreaseItemCount(long count) {
		if (count <= 0) {
			return 0;
		}
		long removeCount = count >= itemCount ? itemCount : count;
		this.itemCount -= removeCount;
		if (itemCount == 0 && !this.itemTemplate.isKinah()) {
			setPersistentState(PersistentState.DELETED);
		} else {
			setPersistentState(PersistentState.UPDATE_REQUIRED);
		}
		return count - removeCount;
	}
	
	/**
	 * @return the isEquipped
	 */
	public boolean isEquipped() {
		return isEquipped;
	}
	
	/**
	 * @param isEquipped the isEquipped to set
	 */
	public void setEquipped(boolean isEquipped) {
		this.isEquipped = isEquipped;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * @return the equipmentSlot Equipment slot can be of 2 types - one is the ItemSlot enum type if equipped, second - is position in cube FIXME:
	 * That's the biggest nonsense!!! Slot value is Q, while position in Cube is H [RR]
	 */
	public long getEquipmentSlot() {
		return equipmentSlot;
	}
	
	/**
	 * @param equipmentSlot the equipmentSlot to set
	 */
	public void setEquipmentSlot(long equipmentSlot) {
		this.equipmentSlot = equipmentSlot;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * This method should be used to lazy initialize empty manastone list
	 *
	 * @return the itemStones
	 */
	public Set<ManaStone> getItemStones() {
		if (manaStones == null) {
			this.manaStones = itemStonesCollection();
		}
		return manaStones;
	}
	
	/**
	 * This method should be used to lazy initialize empty manastone list
	 *
	 * @return the itemStones
	 */
	public Set<ManaStone> getFusionStones() {
		if (fusionStones == null) {
			this.fusionStones = itemStonesCollection();
		}
		return fusionStones;
	}
	
	public int getFusionStonesSize() {
		if (fusionStones == null) {
			return 0;
		}
		return fusionStones.size();
	}
	
	public int getItemStonesSize() {
		if (manaStones == null) {
			return 0;
		}
		return manaStones.size();
	}
	
	private Set<ManaStone> itemStonesCollection() {
		return new TreeSet<ManaStone>(new Comparator<ManaStone>() {
			
			@Override
			public int compare(ManaStone o1, ManaStone o2) {
				if (o1.getSlot() == o2.getSlot()) {
					return 0;
				}
				return o1.getSlot() > o2.getSlot() ? 1 : -1;
			}
		});
	}
	
	/**
	 * Check manastones without initialization
	 *
	 * @return
	 */
	public boolean hasManaStones() {
		return manaStones != null && manaStones.size() > 0;
	}
	
	/**
	 * Check fusionstones without initialization
	 *
	 * @return
	 */
	public boolean hasFusionStones() {
		return fusionStones != null && fusionStones.size() > 0;
	}
	
	public boolean hasIdianStone() {
		return idianStone != null;
	}
	
	public boolean hasGodStone() {
		return godStone != null;
	}
	
	/**
	 * @return the goodStone
	 */
	public GodStone getGodStone() {
		return godStone;
	}
	
	/**
	 * @param itemId
	 * @return
	 */
	public GodStone addGodStone(int itemId) {
		PersistentState state = godStone != null ? PersistentState.UPDATE_REQUIRED : PersistentState.NEW;
		godStone = new GodStone(getObjectId(), itemId, state);
		return godStone;
	}
	
	/**
	 * @param goodStone the goodStone to set
	 */
	public void setGodStone(GodStone godStone) {
		this.godStone = godStone;
	}
	
	/**
	 * @return the echantLevel
	 */
	public int getEnchantLevel() {
		return enchantLevel;
	}
	
	/**
	 * @param enchantLevel the echantLevel to set
	 */
	public void setEnchantLevel(int enchantLevel) {
		this.enchantLevel = enchantLevel;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Returns the {@link #maxEnchantLevel} for this item. This value is
	 * a combination (sum) of the template's base enchant value, and
	 * this item's tuned bonus (if it exists).
	 * 
	 * @return The {@link #maxEnchantLevel} of this item.
	 */
	public int getMaxEnchantLevel() {
		return maxEnchantLevel;
	}
	
	/**
	 * The number of extra enchant levels over the base template.
	 * <p>
	 * This value is for items that have random max enchant levels upon tuning, and is defined
	 * by:<br><code>{@link #maxEnchantLevel maxEnchant} - {@link ItemTemplate#getMaxEnchantLevel() baseEnchant}</code>
	 * 
	 * @return The number of extra enchant levels over the base ItemTemplate max enchant.
	 */
	public int getBonusEnchantLevel() {
		return maxEnchantLevel - itemTemplate.getMaxEnchantLevel();
	}
	
	/**
	 * @return the persistentState
	 */
	public PersistentState getPersistentState() {
		return persistentState;
	}
	
	/**
	 * Possible changes: NEW -> UPDATED NEW -> UPDATE_REQURIED UPDATE_REQUIRED -> DELETED UPDATE_REQUIRED -> UPDATED UPDATED -> DELETED UPDATED ->
	 * UPDATE_REQUIRED
	 *
	 * @param persistentState the persistentState to set
	 */
	public void setPersistentState(PersistentState persistentState) {
		switch (persistentState) {
		case DELETED:
			if (this.persistentState == PersistentState.NEW) {
				this.persistentState = PersistentState.NOACTION;
			} else {
				this.persistentState = PersistentState.DELETED;
			}
			break;
		case UPDATE_REQUIRED:
			if (this.persistentState == PersistentState.NEW) {
				break;
			}
		default:
			this.persistentState = persistentState;
		}
		
	}
	
	public void setItemLocation(int storageType) {
		this.itemLocation = storageType;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	public int getItemLocation() {
		return itemLocation;
	}
	
	public int getItemMask() {
		return itemTemplate.getMask();
	}
	
	public boolean isSoulBound() {
		return isSoulBound;
	}
	
	private boolean isSoulBound(Player player) {
		if (player.havePermission(MembershipConfig.DISABLE_SOULBIND)) {
			return false;
		} else {
			return isSoulBound;
		}
	}
	
	public void setSoulBound(boolean isSoulBound) {
		this.isSoulBound = isSoulBound;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	public EquipType getEquipmentType() {
		if (itemTemplate.isStigma()) {
			return EquipType.STIGMA;
		}
		if (itemTemplate.isAccessory()) {
			return EquipType.ARMOR;// fix for possible client <=> server data errors related to type "NONE"
		}
		return itemTemplate.getEquipmentType();
	}
	
	@Override
	public String toString() {
		return "Item [itemId=" + itemTemplate.getTemplateId() + " equipmentSlot=" + equipmentSlot + ", godStone=" + godStone + ", isEquipped=" + isEquipped + ", itemColor=" + itemColor
				+ ", itemCount=" + itemCount + ", itemLocation=" + itemLocation + ", itemTemplate=" + itemTemplate + ", manaStones=" + manaStones + ", persistentState=" + persistentState + "]";
	}
	
	public int getItemId() {
		return itemTemplate.getTemplateId();
	}
	
	public int getNameId() {
		return itemTemplate.getNameId();
	}
	
	public boolean hasFusionedItem() {
		return fusionedItemTemplate != null;
	}
	
	public ItemTemplate getFusionedItemTemplate() {
		return fusionedItemTemplate;
	}
	
	public int getFusionedItemId() {
		return fusionedItemTemplate != null ? fusionedItemTemplate.getTemplateId() : 0;
	}
	
	public void setFusionedItem(ItemTemplate itemTemplate) {
		this.fusionedItemTemplate = itemTemplate;
		rndCount = itemTemplate.getRandomBonusCount();
		updateChargeInfo(0);
	}
	
	public int getSockets(boolean isFusionItem) {
		int numSockets;
		if (itemTemplate.isWeapon() || itemTemplate.isArmor()) {
			if (isFusionItem) {
				ItemTemplate fusedTemp = getFusionedItemTemplate();
				if (fusedTemp == null) {
					log.error("Item {} with itemId {} has empty fusioned item ", getObjectId(), getItemId());
					return 0;
				}
				numSockets = fusedTemp.getManastoneSlots();
				numSockets += hasOptionalFusionSocket() ? getOptionalFusionSocket() : 0;
			} else {
				numSockets = getItemTemplate().getManastoneSlots();
				numSockets += hasOptionalSocket() ? getOptionalSocket() : 0;
			}
			if (numSockets < 6) {
				return numSockets;
			}
			return 6;
		}
		return 0;
	}
	
	/**
	 * @return the mask
	 */
	public int getItemMask(Player player) {
		int finalMask = checkConfig(player, itemTemplate.getMask());
		return finalMask;
	}
	
	/**
	 * @param player
	 * @return
	 */
	private int checkConfig(Player player, int mask) {
		int newMask = mask;
		if (player.havePermission(MembershipConfig.STORE_WH_ALL)) {
			newMask = newMask | ItemMask.STORABLE_IN_WH;
		}
		if (player.havePermission(MembershipConfig.STORE_AWH_ALL)) {
			newMask = newMask | ItemMask.STORABLE_IN_AWH;
		}
		if (player.havePermission(MembershipConfig.STORE_LWH_ALL)) {
			newMask = newMask | ItemMask.STORABLE_IN_LWH;
		}
		if (player.havePermission(MembershipConfig.TRADE_ALL)) {
			newMask = newMask | ItemMask.TRADEABLE;
		}
		if (player.havePermission(MembershipConfig.REMODEL_ALL)) {
			newMask = newMask | ItemMask.REMODELABLE;
		}
		
		return newMask;
	}
	
	/**
	 * Compares two items on their object and item ids
	 *
	 * @param Item object
	 * @return true, if this item is equal to the object item
	 * @author vlog
	 */
	public boolean isSameItem(Item i) {
		return this.getObjectId().equals(i.getObjectId()) && this.getItemId() == i.getItemId();
	}
	
	public boolean isStorableinWarehouse(Player player) {
		return (getItemMask(player) & ItemMask.STORABLE_IN_WH) == ItemMask.STORABLE_IN_WH;
	}
	
	public boolean isStorableinAccWarehouse(Player player) {
		return (getItemMask(player) & ItemMask.STORABLE_IN_AWH) == ItemMask.STORABLE_IN_AWH && !isSoulBound(player);
	}
	
	public boolean isStorableinLegWarehouse(Player player) {
		return (getItemMask(player) & ItemMask.STORABLE_IN_LWH) == ItemMask.STORABLE_IN_LWH && !isSoulBound(player);
	}
	
	public boolean isTradeable(Player player) {
		return (getItemMask(player) & ItemMask.TRADEABLE) == ItemMask.TRADEABLE && !isSoulBound(player);
	}
	
	public boolean isRemodelable(Player player) {
		return (getItemMask(player) & ItemMask.REMODELABLE) == ItemMask.REMODELABLE;
	}
	
	public boolean isSellable() {
		return (getItemMask() & ItemMask.SELLABLE) == ItemMask.SELLABLE;
	}
	
	public boolean canApExtract() {
		return (getItemMask() & ItemMask.CAN_AP_EXTRACT) == ItemMask.CAN_AP_EXTRACT;
	}
	
	public boolean canSocketGodstone() {
		return (getItemMask() & ItemMask.CAN_PROC_ENCHANT) == ItemMask.CAN_PROC_ENCHANT;
	}
	
	/**
	 * @return Returns the expireTime.
	 */
	@Override
	public int getExpireTime() {
		return expireTime;
	}
	
	public void setExpireTime(int expireTime) {
		this.expireTime = expireTime;
	}
	
	public int getExpireTimeRemaining() {
		if (expireTime == 0) {
			return 0;
		}
		return expireTime - (int) (System.currentTimeMillis() / 1000);
	}
	
	/**
	 * @return Returns the temporaryExchangeTime.
	 */
	public int getTemporaryExchangeTime() {
		return temporaryExchangeTime;
	}
	
	public int getTemporaryExchangeTimeRemaining() {
		if (temporaryExchangeTime == 0) {
			return 0;
		}
		return temporaryExchangeTime - (int) (System.currentTimeMillis() / 1000);
	}
	
	/**
	 * @param temporaryExchangeTime The temporaryExchangeTime to set.
	 */
	public void setTemporaryExchangeTime(int temporaryExchangeTime) {
		this.temporaryExchangeTime = temporaryExchangeTime;
	}
	
	@Override
	public void expireEnd(Player player) {
		if (player == null) {
			return;
		}
		if (isEquipped()) {
			player.getEquipment().unEquipItem(getObjectId(), getEquipmentSlot());
		}
		
		for (StorageType i : StorageType.values()) {
			if (i == StorageType.LEGION_WAREHOUSE) {
				continue;
			}
			IStorage storage = player.getStorage(i.getId());
			
			if (storage != null && storage.getItemByObjId(getObjectId()) != null) {
				storage.delete(this);
				switch (i) {
				case CUBE:
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400034, new DescriptionId(getNameId())));
					break;
				case ACCOUNT_WAREHOUSE:
				case REGULAR_WAREHOUSE:
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400406, new DescriptionId(getNameId())));
					break;
				default:
					break;
				}
			}
		}
	}
	
	@Override
	public void expireMessage(Player player, int time) {
		if (player != null) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400481, new DescriptionId(getNameId()), time));
		}
	}
	
	public void setRepurchasePrice(long price) {
		repurchasePrice = price;
	}
	
	public long getRepurchasePrice() {
		return repurchasePrice;
	}
	
	public int getActivationCount() {
		return activationCount;
	}
	
	public void setActivationCount(int activationCount) {
		this.activationCount = activationCount;
	}
	
	public ChargeInfo getConditioningInfo() {
		return conditioningInfo;
	}
	
	public void setConditioningInfo(ChargeInfo conditioning) {
		this.conditioningInfo = conditioning;
	}
	
	public int getChargePoints() {
		return conditioningInfo != null ? conditioningInfo.getChargePoints() : 0;
	}
	
	public int getChargeLevel() {
		if (getChargePoints() == 0) {
			return 0;
		}
		return getChargePoints() > ChargeInfo.LEVEL1 ? 2 : 1;
	}
	
	/**
	 * Calculate charge level based on main item and fusioned item
	 */
	public int getChargeLevelMax() {
		int thisChargeLevel = 0;
		if (getImprovement() != null) {
			thisChargeLevel = getImprovement().getLevel();
		}
		
		int fusionedChargeLevel = 0;
		if (hasFusionedItem() && getFusionedItemTemplate().getImprovement() != null) {
			fusionedChargeLevel = getFusionedItemTemplate().getImprovement().getLevel();
		}
		
		return Math.max(thisChargeLevel, fusionedChargeLevel);
	}
	
	@Override
	public boolean canExpireNow() {
		return true;
	}
	
	public Improvement getImprovement() {
		if (getItemTemplate().getImprovement() != null) {
			return getItemTemplate().getImprovement();
		} else if (hasFusionedItem() && getFusionedItemTemplate().getImprovement() != null) {
			return getFusionedItemTemplate().getImprovement();
		}
		return null;
	}
	
	public int getBonusNumber() {
		return bonusNumber;
	}
	
	public List<StatFunction> getCurrentModifiers() {
		if (currentModifiers == null) {
			currentModifiers = new ArrayList<StatFunction>();
		}
		return currentModifiers;
	}
	
	public void setCurrentModifiers(List<StatFunction> currentModifiers) {
		getCurrentModifiers().clear();
		getCurrentModifiers().addAll(currentModifiers);
	}
	
	public IdianStone getIdianStone() {
		return idianStone;
	}
	
	public void setIdianStone(IdianStone idianStone) {
		this.idianStone = idianStone;
	}
	
	public RandomStats getRandomStats() {
		return randomStats;
	}
	
	public void setRandomStats(RandomStats randomStats) {
		this.randomStats = randomStats;
	}
	
	public void setBonusNumber(int bonusNumber) {
		this.bonusNumber = bonusNumber;
	}
	
	public void setRandomCount(int rndCount) {
		this.rndCount = rndCount;
	}
	
	public int getRandomCount() {
		return rndCount;
	}
	
	public void setPackCount(int packCount) {
		this.packCount = packCount;
	}
	
	public int getPackCount() {
		return packCount;
	}
	
	public void setAuthorize(int authorize) {
		this.authorize = authorize;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	public int getAuthorize() {
		return authorize;
	}
	
	public boolean isPacked() {
		return isPacked;
	}
	
	public void setPacked(boolean isPacked) {
		this.isPacked = isPacked;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	public boolean isAmplified() {
		return isAmplified;
	}
	
	public void setAmplified(boolean isAmplified) {
		this.isAmplified = isAmplified;
	}
	
	public int getBuffSkill() {
		return buffSkill;
	}
	
	public void setBuffSkill(int buffSkill) {
		this.buffSkill = buffSkill;
	}
}

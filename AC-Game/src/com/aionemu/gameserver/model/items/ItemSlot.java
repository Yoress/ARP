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
package com.aionemu.gameserver.model.items;

import java.util.ArrayList;
import java.util.List;

/**
 * This enum is defining inventory slots, to which items can be equipped.
 *
 * @author Luno, xTz
 */
public enum ItemSlot {
	
	MAIN_HAND(1L),
	SUB_HAND(1L << 1),
	HELMET(1L << 2),
	TORSO(1L << 3),
	GLOVES(1L << 4),
	BOOTS(1L << 5),
	EARRINGS_LEFT(1L << 6),
	EARRINGS_RIGHT(1L << 7),
	RING_LEFT(1L << 8),
	RING_RIGHT(1L << 9),
	NECKLACE(1L << 10),
	SHOULDER(1L << 11),
	PANTS(1L << 12),
	POWER_SHARD_RIGHT(1L << 13),
	POWER_SHARD_LEFT(1L << 14),
	WINGS(1L << 15),
	// non-NPC equips (slot > Short.MAX)
	WAIST(1L << 16),
	MAIN_OFF_HAND(1L << 17),
	SUB_OFF_HAND(1L << 18),
	PLUME(1L << 19), // 4.6
	// combo
	MAIN_OR_SUB(MAIN_HAND.slotIdMask | SUB_HAND.slotIdMask, true), // 3
	MAIN_OFF_OR_SUB_OFF(MAIN_OFF_HAND.slotIdMask | SUB_OFF_HAND.slotIdMask, true),
	EARRING_RIGHT_OR_LEFT(EARRINGS_LEFT.slotIdMask | EARRINGS_RIGHT.slotIdMask, true), // 192
	RING_RIGHT_OR_LEFT(RING_LEFT.slotIdMask | RING_RIGHT.slotIdMask, true), // 768
	SHARD_RIGHT_OR_LEFT(POWER_SHARD_LEFT.slotIdMask | POWER_SHARD_RIGHT.slotIdMask, true), // 24576
	RIGHT_HAND(MAIN_HAND.slotIdMask | MAIN_OFF_HAND.slotIdMask, true),
	LEFT_HAND(SUB_HAND.slotIdMask | SUB_OFF_HAND.slotIdMask, true),
	// TORSO_GLOVE_FOOT_SHOULDER_LEG(0, true),
	
	// STIGMA slots
	STIGMA1(1L << 30),
	STIGMA2(1L << 31),
	STIGMA3(1L << 32),
	STIGMA4(1L << 33),
	STIGMA5(1L << 34),
	STIGMA6(1L << 35),
	REGULAR_STIGMAS(STIGMA1.slotIdMask | STIGMA2.slotIdMask | STIGMA3.slotIdMask | STIGMA4.slotIdMask | STIGMA5.slotIdMask | STIGMA6.slotIdMask, true),
	ADV_STIGMA1(1L << 47),
	ADV_STIGMA2(1L << 48),
	ADV_STIGMA3(1L << 49),
	ADV_STIGMA4(1L << 50),
	ADV_STIGMA5(1L << 51),
	ADV_STIGMA6(1L << 52),
	ADVANCED_STIGMAS(ADV_STIGMA1.slotIdMask | ADV_STIGMA2.slotIdMask | ADV_STIGMA3.slotIdMask | ADV_STIGMA4.slotIdMask | ADV_STIGMA5.slotIdMask | ADV_STIGMA6.slotIdMask, true),
	ALL_STIGMA(REGULAR_STIGMAS.slotIdMask | ADVANCED_STIGMAS.slotIdMask, true);
	
	private long slotIdMask;
	private boolean combo;
	
	private ItemSlot(long mask) {
		this(mask, false);
	}
	
	private ItemSlot(long mask, boolean combo) {
		this.slotIdMask = mask;
		this.combo = combo;
	}
	
	public long getSlotIdMask() {
		return slotIdMask;
	}
	
	/**
	 * @return the combo
	 */
	public boolean isCombo() {
		return combo;
	}
	
	public static boolean isAdvancedStigma(long slot) {
		return (ADVANCED_STIGMAS.slotIdMask & slot) == slot;
	}
	
	public static boolean isRegularStigma(long slot) {
		return (REGULAR_STIGMAS.slotIdMask & slot) == slot;
	}
	
	public static boolean isStigma(long slot) {
		return (ALL_STIGMA.slotIdMask & slot) == slot;
	}
	
	public static ItemSlot[] getSlotsFor(long slot) {
		List<ItemSlot> slots = new ArrayList<ItemSlot>();
		for (ItemSlot itemSlot : values()) {
			if (slot != 0 && !itemSlot.isCombo() && (slot & itemSlot.slotIdMask) == itemSlot.slotIdMask) {
				slots.add(itemSlot);
			}
		}
		return slots.toArray(new ItemSlot[slots.size()]);
	}
	
	public static ItemSlot getSlotFor(long slot) {
		ItemSlot[] slots = getSlotsFor(slot);
		if (slots != null && slots.length > 0) {
			return slots[0];
		}
		throw new IllegalArgumentException("Invalid provided slotIdMask " + slot);
	}
}

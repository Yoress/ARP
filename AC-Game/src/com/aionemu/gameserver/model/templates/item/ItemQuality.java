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
package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * This represents the tier of an item. For clarity, "tier" refers to the item's rarity and text color.
 * <p>
 * The following tiers are represented by a {@link #qualityId}:
 * <p>
 * <table border="1">
 * <thead><th>Name</th>				<th>Quality ID</th></thead>
 * <tbody>
 * <tr><td>{@link #JUNK}</td>		<td><Center>0</Center></td></tr>
 * <tr><td>{@link #COMMON}</td>		<td><Center>1</Center></td></tr>
 * <tr><td>{@link #RARE}</td>		<td><Center>2</Center></td></tr>
 * <tr><td>{@link #LEGEND}</td>		<td><Center>3</Center></td></tr>
 * <tr><td>{@link #UNIQUE}</td>		<td><Center>4</Center></td></tr>
 * <tr><td>{@link #EPIC}</td>		<td><Center>5</Center></td></tr>
 * <tr><td>{@link #MYTHIC}</td>		<td><Center>6</Center></td></tr>
 * </tbody>
 * </table>
 * 
 * @author ATracer
 * @author Yon (Aion Reconstruction Project) -- Added {@link #getItemQuality(int)}, and {@link #equalOrHigherTierThan(ItemQuality)}.
 */
@XmlType(name = "quality")
@XmlEnum
public enum ItemQuality {
	// TODO: Reorder and rename - requires ATracer parser update (?)
	/**
	 * Represents Miscellaneous (Gray) tier items.
	 */
	JUNK(0), // Junk - Gray
	/**
	 * Represents Common (White) tier items.
	 */
	COMMON(1), // Common - White
	/**
	 * Represents Superior (Green) tier items.
	 */
	RARE(2), // Superior - Green
	/**
	 * Represents Heroic (Blue) tier items.
	 */
	LEGEND(3), // Heroic - Blue
	/**
	 * Represents Fabled (Yellow) tier items.
	 */
	UNIQUE(4), // Fabled - Yellow
	/**
	 * Represents Eternal (Orange) tier items.
	 */
	EPIC(5), // Eternal - Orange
	/**
	 * Represents Mythic (Purple) tier items.
	 */
	MYTHIC(6); // Test - Purple
	
	/**
	 * A numeric value used to indicate the tier of this {@link ItemQuality}.
	 */
	private int qualityId;
	
	/**
	 * Constructors
	 */
	private ItemQuality(int qualityId) {
		this.qualityId = qualityId;
	}
	
	/**
	 * Accessors
	 */
	public int getQualityId() {
		return qualityId;
	}
	
	/**
	 * Retrieves the {@link ItemQuality} for the given quality ID.
	 * <p>
	 * If the given ID does not associate with any {@link ItemQuality},
	 * then null is returned.
	 * 
	 * @param qualityId - The underlying quality ID of the item.
	 * @return The {@link ItemQuality} matching the given qualityId or null.
	 */
	public static ItemQuality getItemQuality(int qualityId) {
		for (ItemQuality iq: ItemQuality.values()) {
			if (iq.qualityId == qualityId) return iq;
		}
		return null;
	}
	
	/**
	 * Compares this {@link ItemQuality} to another. Returns true if the
	 * {@link #qualityId} is greater than or equal to that of the passed
	 * in {@link ItemQuality}.
	 * 
	 * @param iQ -- the {@link ItemQuality} to compare to.
	 * @return true if the given {@link ItemQuality} is lesser or equal in
	 * tier to the caller. False otherwise.  
	 */
	public boolean equalOrHigherTierThan(ItemQuality iQ) {
		if (qualityId >= iQ.qualityId) return true;
		return false;
	}
}

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
package com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.conditions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.model.QuestEnv;

/**
 * @author Mr. Poke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PcInventoryCondition")
public class PcInventoryCondition extends QuestCondition {
	
	@XmlAttribute(name = "item_id", required = true)
	protected int itemId;
	@XmlAttribute(required = true)
	protected long count;
	
	/**
	 * Gets the value of the itemId property.
	 */
	public int getItemId() {
		return itemId;
	}
	
	/**
	 * Gets the value of the count property.
	 */
	public long getCount() {
		return count;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aionemu.gameserver.questEngine.handlers.template.xmlQuest.condition. QuestCondition#doCheck(com.aionemu.gameserver
	 * .questEngine.model.QuestEnv)
	 */
	@Override
	public boolean doCheck(QuestEnv env) {
		Player player = env.getPlayer();
		long itemCount = player.getInventory().getItemCountByItemId(itemId);
		switch (getOp()) {
		case EQUAL:
			return itemCount == count;
		case GREATER:
			return itemCount > count;
		case GREATER_EQUAL:
			return itemCount >= count;
		case LESSER:
			return itemCount < count;
		case LESSER_EQUAL:
			return itemCount <= count;
		case NOT_EQUAL:
			return itemCount != count;
		default:
			return false;
		}
	}
}

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
package com.aionemu.gameserver.network.aion.iteminfo;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.configs.administration.DeveloperConfig;
import com.aionemu.gameserver.model.stats.calc.functions.StatRateFunction;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * @author Rolandas
 */
public class BonusInfoBlobEntry extends ItemBlobEntry {
	
	public BonusInfoBlobEntry() {
		super(ItemBlobType.STAT_BONUSES);
	}
	
	@Override
	public void writeThisBlob(ByteBuffer buf) {
		if (DeveloperConfig.ITEM_STAT_ID > 0) {
			writeH(buf, DeveloperConfig.ITEM_STAT_ID);
			writeD(buf, 10);
			writeC(buf, 0);
		} else {
			writeH(buf, modifier.getName().getItemStoneMask());
			writeD(buf, modifier.getValue() * modifier.getName().getSign());
			writeC(buf, modifier instanceof StatRateFunction ? 1 : 0);
		}
	}
	
	@Override
	public int getSize() {
		return 7;
	}
}

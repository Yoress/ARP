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
package com.aionemu.gameserver.world.zone;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.templates.zone.ZoneInfo;
import com.aionemu.gameserver.model.templates.zone.ZoneType;

/**
 * @author MrPoke
 */
public class PvPZoneInstance extends SiegeZoneInstance {
	
	/**
	 * @param mapId
	 * @param template
	 */
	public PvPZoneInstance(int mapId, ZoneInfo template) {
		super(mapId, template);
	}
	
	@Override
	public synchronized boolean onEnter(Creature creature) {
		if (super.onEnter(creature)) {
			creature.setInsideZoneType(ZoneType.PVP);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public synchronized boolean onLeave(Creature creature) {
		if (super.onLeave(creature)) {
			creature.unsetInsideZoneType(ZoneType.PVP);
			return true;
		} else {
			return false;
		}
	}
}

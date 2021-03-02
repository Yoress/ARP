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
package com.aionemu.gameserver.ai2.mechanics.context;

import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@XmlType
public enum ClassIndicator {
	CLASSI_WARRIOR_GROUP,
	CLASSI_RANGER,
	CLASSI_CASTER_GROUP,
	CLASSI_MAGE_GROUP,
	CLASSI_WIZARD,
	CLASSI_CLERIC_GROUP,
	CLASSI_KNIGHT,
	CLASSI_SCOUT_GROUP,
	CLASSI_MELEE_GROUP,
	CLASSI_ELEMENTALIST,
	CLASSI_FIGHTER,
	CLASSI_ASSASSIN,
	CLASSI_CHANTER,
	CLASSI_PRIEST;
	
	public boolean isClass(Creature creature) {
		if (creature instanceof Player) {
			switch (((Player) creature).getPlayerClass()) {
				case ALL:
					return true;
				case ARTIST:
					return this == CLASSI_CASTER_GROUP;
				case ASSASSIN:
					return this == CLASSI_ASSASSIN || this == CLASSI_SCOUT_GROUP || this == CLASSI_MELEE_GROUP;
				case BARD:
					return this == CLASSI_CASTER_GROUP || this == CLASSI_CLERIC_GROUP;
				case CHANTER:
					return this == CLASSI_CHANTER || this == CLASSI_CLERIC_GROUP || this == CLASSI_MELEE_GROUP;
				case CLERIC:
					return this == CLASSI_CLERIC_GROUP || this == CLASSI_PRIEST;
				case ENGINEER:
					return false; //Not currently supported
				case GLADIATOR:
					return this == CLASSI_FIGHTER || this == CLASSI_WARRIOR_GROUP || this == CLASSI_MELEE_GROUP;
				case GUNNER:
					return false; //Not currently supported
				case MAGE:
					return this == CLASSI_MAGE_GROUP || this == CLASSI_CASTER_GROUP;
				case PRIEST:
					return this == CLASSI_CLERIC_GROUP;
				case RANGER:
					return this == CLASSI_RANGER || this == CLASSI_SCOUT_GROUP;
				case RIDER:
					return false; //Not currently supported
				case SCOUT:
					return this == CLASSI_SCOUT_GROUP || this == CLASSI_MELEE_GROUP;
				case SORCERER:
					return this == CLASSI_WIZARD || this == CLASSI_CASTER_GROUP || this == CLASSI_MAGE_GROUP;
				case SPIRIT_MASTER:
					return this == CLASSI_ELEMENTALIST || this == CLASSI_CASTER_GROUP || this == CLASSI_MAGE_GROUP;
				case TEMPLAR:
					return this == CLASSI_KNIGHT || this == CLASSI_WARRIOR_GROUP || this == CLASSI_MELEE_GROUP;
				case WARRIOR:
					return this == CLASSI_WARRIOR_GROUP || this == CLASSI_MELEE_GROUP;
				default:
					return false;
			}
		}
		return false; //Not supporting non-players at this time.
	}
}

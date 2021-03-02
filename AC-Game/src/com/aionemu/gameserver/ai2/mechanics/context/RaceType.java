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

import com.aionemu.gameserver.model.Race;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@XmlType
public enum RaceType {
	pc_light,
	pc_dark,
	Lycan,
	drakan,
	GChief_Light,
	GChief_Dark,
	MagicalMonster,
	PC_Dark_Castle_door,
	PC_Light_Castle_door,
	Barrier,
	Teleporter,
	GChief_Dragon,
	Shulack,
	construct,
	Beast,
	Neut,
	krall,
	Goblin,
	elemental,
	pc,
	brownie,
	dragon,
	DemiHumanoid;

	public boolean isRace(Race race) {
		switch (this) {
			case Barrier:
				return race == Race.BARRIER;
			case Beast:
				return race == Race.BEAST;
			case GChief_Dark:
				return race == Race.GCHIEF_DARK;
			case GChief_Dragon:
				return race == Race.GCHIEF_DRAGON;
			case GChief_Light:
				return race == Race.GCHIEF_LIGHT;
			case Lycan:
				return race == Race.LYCAN;
			case MagicalMonster:
				return race == Race.MAGICALMONSTER;
			case Neut:
				return race == Race.NEUT;
			case PC_Dark_Castle_door:
				return race == Race.PC_DARK_CASTLE_DOOR;
			case PC_Light_Castle_door:
				return race == Race.PC_LIGHT_CASTLE_DOOR;
			case Shulack:
				return race == Race.SHULACK;
			case Teleporter:
				return race == Race.TELEPORTER;
			case construct:
				return race == Race.CONSTRUCT;
			case drakan:
				return race == Race.DRAKAN;
			case krall:
				return race == Race.KRALL;
			case pc_dark:
				return race == Race.ASMODIANS;
			case pc_light:
				return race == Race.ELYOS;
			case DemiHumanoid:
				return race == Race.DEMIHUMANOID;
			case Goblin:
				return race == Race.GOBLIN;
			case brownie:
				return race == Race.BROWNIE;
			case dragon:
				return race == Race.DRAGON;
			case elemental:
				return race == Race.ELEMENTAL;
			case pc:
				return race.isPlayerRace();
			default:
				assert false:"Unsupported RaceType: " + this;
		}
		return false;
	}
}

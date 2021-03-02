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
package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class GeoDataConfig {
	
	/**
	 * Geodata enable
	 */
	@Property(key = "gameserver.geodata.enable", defaultValue = "false")
	public static boolean GEO_ENABLE;
	
	/**
	 * Defines the length of time (in milliseconds) that a geo update remains valid.
	 */
	@Property(key = "gameserver.geo.invalidate.delay", defaultValue = "200")
	public static int GEO_VALID_TIME;
	
	/**
	 * Enable canSee checks using geodata.
	 */
	@Property(key = "gameserver.geodata.cansee.enable", defaultValue = "true")
	public static boolean CANSEE_ENABLE;
	/**
	 * Enable Fear skill using geodata.
	 */
	@Property(key = "gameserver.geodata.fear.enable", defaultValue = "true")
	public static boolean FEAR_ENABLE;
	/**
	 * Enable Geo checks during npc movement (prevent flying mobs)
	 */
	@Property(key = "gameserver.geo.npc.move", defaultValue = "false")
	public static boolean GEO_NPC_MOVE;
	/**
	 * Enable npc checks aggro target visibility range (canSee)
	 */
	@Property(key = "gameserver.geo.npc.aggro", defaultValue = "false")
	public static boolean GEO_NPC_AGGRO;
	/**
	 * Enable geo materials using skills
	 */
	@Property(key = "gameserver.geo.materials.enable", defaultValue = "false")
	public static boolean GEO_MATERIALS_ENABLE;
	/**
	 * Show collision zone name and skill id
	 */
	@Property(key = "gameserver.geo.materials.showdetails", defaultValue = "false")
	public static boolean GEO_MATERIALS_SHOWDETAILS;
	/**
	 * Enable geo shields
	 */
	@Property(key = "gameserver.geo.shields.enable", defaultValue = "false")
	public static boolean GEO_SHIELDS_ENABLE;
	/**
	 * Enable geo doors
	 */
	@Property(key = "gameserver.geo.doors.enable", defaultValue = "false")
	public static boolean GEO_DOORS_ENABLE;
	/**
	 * Object factory for geodata primitives enabled
	 */
	@Property(key = "gameserver.geodata.objectfactory.enabled", defaultValue = "true")
	public static boolean GEO_OBJECT_FACTORY_ENABLE;
	/**
	 * Enables using ./data/nav to load nav mesh for each map and pathfind with the data
	 */
	@Property(key = "gameserver.geo.nav.pathfinding.enable", defaultValue = "false")
	public static boolean GEO_NAV_ENABLE;
}

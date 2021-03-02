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
package com.aionemu.gameserver.model.templates.npcskill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Basic template for Npc skill list entries.
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "npcskill")
public class NpcSkillTemplate {
	/**
	 * The skill ID of this skill.
	 */
	@XmlAttribute(name = "skillid", required = true)
	private int skillId;
	
	/**
	 * The level of this skill when used.
	 */
	@XmlAttribute(name = "skilllevel", required = true)
	private int skillLevel;
	
	/**
	 * The chance of this skill being selected for use.
	 * <p>
	 * If this value is zero, then the skill can only be used by the AI directly.
	 */
	@XmlAttribute(name = "probability")
	private int probability = 0;
	
	/**
	 * The cooldown of this skill.
	 * <p>
	 * Note that not all skills have a cooldown, as the AI for Npc's is intended to handle it.
	 */
	@XmlAttribute(name = "cooldown")
	private int cooldown = 0;
	
	/**
	 * The amount of times this skill can be used per fight.
	 * <p>
	 * If the skill count is zero, then this skill is only used by the AI directly.
	 */
	@XmlAttribute(name = "count")
	private int skillCount = 0;
	
	/**
	 * Returns the skill ID of this skill.
	 * 
	 * @return {@link #skillId}.
	 */
	public int getSkillId() {
		return skillId;
	}
	
	/**
	 * Returns the level of this skill when used.
	 * 
	 * @return {@link #skillLevel}.
	 */
	public int getSkillLevel() {
		return skillLevel;
	}
	
	/**
	 * Returns the chance of this skill being used.
	 * <p>
	 * If this value is zero, then the skill can only be used by the AI directly.
	 * 
	 * @return {@link #probability}.
	 */
	public int getProbability() {
		return probability;
	}
	
	/**
	 * Returns the cooldown of this skill.
	 * <p>
	 * Note that not all skills have a cooldown, as the AI for Npc's is intended to handle it.
	 * 
	 * @return {@link #cooldown}.
	 */
	public int getCooldown() {
		return cooldown;
	}
	
	/**
	 * Returns the number of times this skill can be used in a fight.
	 * <p>
	 * If the skill count is zero, then this skill is only used by the AI directly.
	 * 
	 * @return {@link #skillCount}.
	 */
	public int getSkillCount() {
		return skillCount;
	}
	
}

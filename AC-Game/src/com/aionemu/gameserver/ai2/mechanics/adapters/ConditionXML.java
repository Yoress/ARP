/**
 * This file is part of the Aion Reconstruction Project Server.
 *
 * The Aion Reconstruction Project Server is free software: you can redistribute it and/or modify it under the terms of the GNU General License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * The Aion Reconstruction Project Server is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General License for more details.
 *
 * You should have received a copy of the GNU General License along with the Aion Reconstruction Project Server. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * @AionReconstructionProjectTeam
 */
package com.aionemu.gameserver.ai2.mechanics.adapters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.ai2.mechanics.conditions.ConditionType;
import com.aionemu.gameserver.ai2.mechanics.context.AbnormalStateIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.BtimerIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.ClassIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.DamageFlag;
import com.aionemu.gameserver.ai2.mechanics.context.FlagvarIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.HyperlinkId;
import com.aionemu.gameserver.ai2.mechanics.context.IntvarIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.NpcIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.ObjIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.QuestProgress;
import com.aionemu.gameserver.ai2.mechanics.context.RaceType;
import com.aionemu.gameserver.ai2.mechanics.context.SkillCategory;
import com.aionemu.gameserver.ai2.mechanics.context.SkillIndex;
import com.aionemu.gameserver.ai2.mechanics.context.State;
import com.aionemu.gameserver.ai2.mechanics.context.UserIndicator;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "condition")
class ConditionXML {
	
	@XmlAttribute(name = "type", required = true)
	ConditionType type;
	
	@XmlAttribute(name = "obj")
	ObjIndicator objI;
	
	@XmlAttribute(name = "user")
	UserIndicator userI;
	
	@XmlAttribute(name = "npc")
	NpcIndicator npcI;
	
	@XmlAttribute(name = "class")
	ClassIndicator classI;
	
	@XmlAttribute(name = "state")
	State state;
	
	@XmlAttribute(name = "race")
	RaceType raceType;
	
	@XmlAttribute(name = "intvar")
	IntvarIndicator intvarIndicator;
	
	@XmlAttribute(name = "flag")
	FlagvarIndicator flagvarIndicator;
	
	@XmlAttribute(name = "timer")
	BtimerIndicator btimerIndicator;
	
	@XmlAttribute(name = "skill")
	SkillIndex skill;
	
	@XmlAttribute(name = "skill_category")
	SkillCategory skillCategory;
	
	@XmlAttribute(name = "abnormal_state")
	AbnormalStateIndicator abnormalState;
	
	@XmlAttribute(name = "damage_flag")
	DamageFlag damageFlag;
	
	@XmlAttribute(name = "hyperlink_id")
	HyperlinkId hyperlinkId;
	
	@XmlAttribute(name = "quest_state")
	QuestProgress questProgress;
	
	@XmlAttribute(name = "only_on_bound")
	Boolean beTrueOnlyWhenHitTheBound;
	
	@XmlAttribute(name = "expected")
	Boolean flagExpected;
	
	@XmlAttribute(name = "add_var")
	Integer varToAdd;
	
	@XmlAttribute(name = "min")
	Integer lowerBound;
	
	@XmlAttribute(name = "max")
	Integer upperBound;
	
	@XmlAttribute(name = "distance")
	Integer distance;
	
	@XmlAttribute(name = "skill_id")
	Integer skillId;
	
	@XmlAttribute(name = "percent")
	Integer percent;
	
	@XmlAttribute(name = "msg_type")
	Integer messageType;
	
	@XmlAttribute(name = "waypoint_id")
	Integer index;
	
	@XmlAttribute(name = "set_var")
	Integer intvarToSet;
	
	@XmlAttribute(name = "compare_var")
	Integer comparand;
	
	@XmlAttribute(name = "quest_id")
	Integer questId;
	
}

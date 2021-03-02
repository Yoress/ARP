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
import com.aionemu.gameserver.ai2.mechanics.actions.ActionType;
import com.aionemu.gameserver.ai2.mechanics.context.Alias;
import com.aionemu.gameserver.ai2.mechanics.context.AreaName;
import com.aionemu.gameserver.ai2.mechanics.context.AreaType;
import com.aionemu.gameserver.ai2.mechanics.context.AttackerIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.BtimerIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.ClassIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.CutsceneTarget;
import com.aionemu.gameserver.ai2.mechanics.context.MoveType;
import com.aionemu.gameserver.ai2.mechanics.context.MovingCollisionType;
import com.aionemu.gameserver.ai2.mechanics.context.ObjIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.OrderIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.SkillIndex;
import com.aionemu.gameserver.ai2.mechanics.context.SpawnId;
import com.aionemu.gameserver.ai2.mechanics.context.SpawnLocationType;
import com.aionemu.gameserver.ai2.mechanics.context.UserIndicator;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "action")
class ActionXML {
	
	@XmlAttribute(name = "type", required = true)
	ActionType type;
	
	@XmlAttribute(name = "obj")
	ObjIndicator objI;
	
	@XmlAttribute(name = "obj_param")
	ObjIndicator paramObj;
	
	@XmlAttribute(name = "user")
	UserIndicator userI;
	
	@XmlAttribute(name = "class")
	ClassIndicator classI;
	
	@XmlAttribute(name = "attacker")
	AttackerIndicator attackerI;
	
	@XmlAttribute(name = "timer")
	BtimerIndicator battleTimerIndicator;
	
	@XmlAttribute(name = "skill")
	SkillIndex skill;
	
	@XmlAttribute(name = "spawn_id")
	SpawnId spawnId;
	
	@XmlAttribute(name = "spawn_loc")
	SpawnLocationType spawnLocationType;
	
	@XmlAttribute(name = "move_type")
	MoveType moveType;
	
	@XmlAttribute(name = "alias")
	Alias alias;
	
	@XmlAttribute(name = "teleport_alias")
	Alias teleportAlias;
	
	@XmlAttribute(name = "area_type")
	AreaType areaType;
	
	@XmlAttribute(name = "area_name")
	AreaName areaName;
	
	@XmlAttribute(name = "moving_collision_type")
	MovingCollisionType movingCollisionType;
	
	@XmlAttribute(name = "cutscene_target")
	CutsceneTarget cutsceneTarget;
	
	@XmlAttribute(name = "order")
	OrderIndicator orderInAttackerList;
	
	@XmlAttribute(name = "path")
	String pathname;
	
	@XmlAttribute(name = "zone")
	String zoneName;
	
	@XmlAttribute(name = "str")
	String string;
	
	@XmlAttribute(name = "str_param_1")
	String param1S;
	
	@XmlAttribute(name = "str_param_2")
	String param2S;
	
	@XmlAttribute(name = "str_param_3")
	String param3S;
	
	@XmlAttribute(name = "push_state")
	Boolean pushState;
	
	@XmlAttribute(name = "except_tank")
	Boolean isExceptMostHating;
	
	@XmlAttribute(name = "volatile_only")
	Boolean volatileHatepointOnly;
	
	@XmlAttribute(name = "despawn_while_attacking")
	Boolean despawnAtAttackState;
	
	@XmlAttribute(name = "aerial")
	Boolean isAerialSpawn;
	
	@XmlAttribute(name = "attack_target")
	Boolean attackTargetAfterSpawn;
	
	@XmlAttribute(name = "restrict_range")
	Boolean restrictedRange;
	
	@XmlAttribute(name = "anim")
	Boolean showFX;
	
	@XmlAttribute(name = "charge_max_count")
	Boolean chargeMaxCount;
	
	@XmlAttribute(name = "set_active")
	Boolean setActive;
	
	@XmlAttribute(name = "attackable")
	Boolean attackable;
	
	@XmlAttribute(name = "x")
	Float x;
	
	@XmlAttribute(name = "y")
	Float y;
	
	@XmlAttribute(name = "z")
	Float z;
	
	@XmlAttribute(name = "msg_type")
	Integer messageType;
	
	@XmlAttribute(name = "int_param_1")
	Integer param1int;
	
	@XmlAttribute(name = "int_param_2")
	Integer param2int;
	
	@XmlAttribute(name = "delay")
	Integer delay;
	
	@XmlAttribute(name = "range")
	Integer range;
	
	@XmlAttribute(name = "str_id")
	Integer stringId;
	
	@XmlAttribute(name = "seconds")
	Integer seconds;
	
	@XmlAttribute(name = "waypoint_id")
	Integer waypointId;
	
	@XmlAttribute(name = "time")
	Integer timeToMove;
	
	@XmlAttribute(name = "set")
	Integer set;
	
	@XmlAttribute(name = "modify")
	Integer modify;
	
	@XmlAttribute(name = "npc_id")
	Integer npcId;
	
	@XmlAttribute(name = "amount")
	Integer numToSpawn;
	
	@XmlAttribute(name = "dir")
	Integer dir;
	
	@XmlAttribute(name = "spawn_range")
	Integer spawnRange;
	
	@XmlAttribute(name = "live_time")
	Integer liveTime;
	
	@XmlAttribute(name = "valid_distance")
	Integer validDistance;
	
	@XmlAttribute(name = "hate_to_add")
	Integer hatepointsToAdd;
	
	@XmlAttribute(name = "percent_hate_to_add")
	Integer percentToAdd;
	
	@XmlAttribute(name = "scene_status")
	Integer scenestatus;
	
	@XmlAttribute(name = "quest_id")
	Integer questId;
	
	@XmlAttribute(name = "portal_id")
	Integer portalId;
	
	@XmlAttribute(name = "door_id")
	Integer doorId;
	
	@XmlAttribute(name = "door_method")
	Integer doorMethod;
	
	@XmlAttribute(name = "op_code")
	Integer opCode;
	
	@XmlAttribute(name = "item_id")
	Integer itemId;
	
	@XmlAttribute(name = "min")
	Integer min;
	
	@XmlAttribute(name = "max")
	Integer max;
	
	@XmlAttribute(name = "subzone_id")
	Integer subZoneId;
	
	@XmlAttribute(name = "group_id")
	Integer groupId;
	
	@XmlAttribute(name = "cutscene_id")
	Integer cutsceneId;
	
	@XmlAttribute(name = "total_set_to_spawn")
	Integer totalSetToSpawn;
	
}

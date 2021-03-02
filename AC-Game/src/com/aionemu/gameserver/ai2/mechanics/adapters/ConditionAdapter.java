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
package com.aionemu.gameserver.ai2.mechanics.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.aionemu.gameserver.ai2.mechanics.AIMechanics;
import com.aionemu.gameserver.ai2.mechanics.conditions.AddIntvarCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.Condition;
import com.aionemu.gameserver.ai2.mechanics.conditions.DecreaseIntvarCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.HasAttackDamageFlagCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IncreaseIntvarCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsAbnormalStateCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsBattleTimerIndicatorCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsDistanceLongerThanCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsDistanceShorterThanCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsEnemyCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsEventSkillCategoryCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsEventSkillIdCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsHpInBoundaryCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsHpLowerThanCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsHyperlinkIdCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsInAbnormalStateCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsLastWaypointCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsMessageCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsMyCurentTargetCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsNpcCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsNpcStateCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsObjInAbnormalStateCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsRaceCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsSkillCountLeftCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsTargetQuestStateCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsUserClassCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsUserCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsUserFlyingCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsWaypointIndexCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.IsWorldFlagVarCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.SetFlagVarCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.SetIntvarIfLargerThanCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.SetIntvarIfLessThanCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.SetWorldFlagVarCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.TestProbabilityCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.UnsetFlagVarCondition;
import com.aionemu.gameserver.ai2.mechanics.conditions.UnsetWorldFlagVarCondition;


/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class ConditionAdapter extends XmlAdapter<ConditionXML, Condition> {
	
	@Override
	public Condition unmarshal(ConditionXML c) throws Exception {
		Condition ret = null;
		switch (c.type) {
			case add_intvar:
				ret = new AddIntvarCondition(c.intvarIndicator, c.varToAdd, c.lowerBound, c.upperBound, c.beTrueOnlyWhenHitTheBound);
				break;
			case is_abnormal_state:
				ret = new IsAbnormalStateCondition(c.abnormalState);
				break;
			case is_battle_timer_indicator:
				ret = new IsBattleTimerIndicatorCondition(c.btimerIndicator);
				break;
			case is_distance_longer_than:
				ret = new IsDistanceLongerThanCondition(c.objI, c.distance);
				break;
			case is_distance_shorter_than:
				ret = new IsDistanceShorterThanCondition(c.objI, c.distance);
				break;
			case is_enemy:
				ret = new IsEnemyCondition(c.objI);
				break;
			case is_event_skill_category:
				ret = new IsEventSkillCategoryCondition(c.skillCategory);
				break;
			case is_event_skill_id:
				ret = new IsEventSkillIdCondition(c.skillId);
				break;
			case is_hp_in_boundary:
				ret = new IsHpInBoundaryCondition(c.objI, c.lowerBound, c.upperBound);
				break;
			case is_hp_lower_than:
				ret = new IsHpLowerThanCondition(c.objI, c.percent);
				break;
			case is_in_abnormal_state:
				ret = new IsInAbnormalStateCondition(c.abnormalState);
				break;
			case is_last_waypoint:
				ret = new IsLastWaypointCondition();
				break;
			case is_message:
				ret = new IsMessageCondition(c.messageType);
				break;
			case is_my_curent_target:
				ret = new IsMyCurentTargetCondition(c.objI);
				break;
			case is_npc:
				ret = new IsNpcCondition(c.objI);
				break;
			case is_npc_state:
				ret = new IsNpcStateCondition(c.npcI, c.state);
				break;
			case is_obj_in_abnormal_state:
				ret = new IsObjInAbnormalStateCondition(c.objI, c.abnormalState);
				break;
			case is_race:
				ret = new IsRaceCondition(c.objI, c.raceType);
				break;
			case is_skill_count_left:
				ret = new IsSkillCountLeftCondition(c.skill);
				break;
			case is_user:
				ret = new IsUserCondition(c.objI);
				break;
			case is_user_class:
				ret = new IsUserClassCondition(c.userI, c.classI);
				break;
			case is_user_flying:
				ret = new IsUserFlyingCondition(c.userI);
				break;
			case is_waypoint_index:
				ret = new IsWaypointIndexCondition(c.index);
				break;
			case set_flag_var:
				ret = new SetFlagVarCondition(c.flagvarIndicator);
				break;
			case set_intvar_if_larger_than:
				ret = new SetIntvarIfLargerThanCondition(c.intvarIndicator, c.intvarToSet, c.comparand);
				break;
			case set_intvar_if_less_than:
				ret = new SetIntvarIfLessThanCondition(c.intvarIndicator, c.intvarToSet, c.comparand);
				break;
			case set_world_flag_var:
				ret = new SetWorldFlagVarCondition(c.flagvarIndicator);
				break;
			case test_probability:
				ret = new TestProbabilityCondition(c.percent);
				break;
			case unset_flag_var:
				ret = new UnsetFlagVarCondition(c.flagvarIndicator);
				break;
			case unset_world_flag_var:
				ret = new UnsetWorldFlagVarCondition(c.flagvarIndicator);
				break;
			case decrease_intvar:
				ret = new DecreaseIntvarCondition(c.intvarIndicator, c.lowerBound, c.upperBound, c.beTrueOnlyWhenHitTheBound);
				break;
			case has_attack_damage_flag:
				ret = new HasAttackDamageFlagCondition(c.damageFlag);
				break;
			case increase_intvar:
				ret = new IncreaseIntvarCondition(c.intvarIndicator, c.lowerBound, c.upperBound, c.beTrueOnlyWhenHitTheBound);
				break;
			case is_hyperlink_id:
				ret = new IsHyperlinkIdCondition(c.hyperlinkId);
				break;
			case is_target_quest_state:
				ret = new IsTargetQuestStateCondition(c.objI, c.questId, c.questProgress);
				break;
			case is_world_flag_var:
				ret = new IsWorldFlagVarCondition(c.flagvarIndicator, c.flagExpected);
				break;
			default:
				assert false:"Unsupported Condition Type: " + c.type;
		}
		return AIMechanics.addOrGetCanonical(ret);
	}
	
	@Override
	public ConditionXML marshal(Condition c) throws Exception {
		ConditionXML ret = new ConditionXML();
		switch (c.type) {
			case add_intvar:
				if (c instanceof AddIntvarCondition) {
					AddIntvarCondition con = (AddIntvarCondition) c;
					ret.type = con.type;
					ret.intvarIndicator = con.intvarIndicator;
					ret.varToAdd = con.varToAdd;
					ret.lowerBound = con.lowerBound;
					ret.upperBound = con.upperBound;
					ret.beTrueOnlyWhenHitTheBound = con.beTrueOnlyWhenHitTheBound;
				}
				break;
			case is_abnormal_state:
				if (c instanceof IsAbnormalStateCondition) {
					IsAbnormalStateCondition con = (IsAbnormalStateCondition) c;
					ret.type = con.type;
					ret.abnormalState = con.abnormalState;
				}
				break;
			case is_battle_timer_indicator:
				if (c instanceof IsBattleTimerIndicatorCondition) {
					IsBattleTimerIndicatorCondition con = (IsBattleTimerIndicatorCondition) c;
					ret.type = con.type;
					ret.btimerIndicator = con.btimerIndicator;
				}
				break;
			case is_distance_longer_than:
				if (c instanceof IsDistanceLongerThanCondition) {
					IsDistanceLongerThanCondition con = (IsDistanceLongerThanCondition) c;
					ret.type = con.type;
					ret.objI = con.who;
					ret.distance = con.distance;
				}
				break;
			case is_distance_shorter_than:
				if (c instanceof IsDistanceShorterThanCondition) {
					IsDistanceShorterThanCondition con = (IsDistanceShorterThanCondition) c;
					ret.type = con.type;
					ret.objI = con.who;
					ret.distance = con.distance;
				}
				break;
			case is_enemy:
				if (c instanceof IsEnemyCondition) {
					IsEnemyCondition con = (IsEnemyCondition) c;
					ret.type = con.type;
					ret.objI = con.who;
				}
				break;
			case is_event_skill_category:
				if (c instanceof IsEventSkillCategoryCondition) {
					IsEventSkillCategoryCondition con = (IsEventSkillCategoryCondition) c;
					ret.type = con.type;
					ret.skillCategory = con.skillCategory;
				}
				break;
			case is_event_skill_id:
				if (c instanceof IsEventSkillIdCondition) {
					IsEventSkillIdCondition con = (IsEventSkillIdCondition) c;
					ret.type = con.type;
					ret.skillId = con.skillId;
				}
				break;
			case is_hp_in_boundary:
				if (c instanceof IsHpInBoundaryCondition) {
					IsHpInBoundaryCondition con = (IsHpInBoundaryCondition) c;
					ret.type = con.type;
					ret.objI = con.who;
					ret.lowerBound = con.largerThan;
					ret.upperBound = con.lessThan;
				}
				break;
			case is_hp_lower_than:
				if (c instanceof IsHpLowerThanCondition) {
					IsHpLowerThanCondition con = (IsHpLowerThanCondition) c;
					ret.type = con.type;
					ret.objI = con.who;
					ret.percent = con.percent;
				}
				break;
			case is_in_abnormal_state:
				if (c instanceof IsInAbnormalStateCondition) {
					IsInAbnormalStateCondition con = (IsInAbnormalStateCondition) c;
					ret.type = con.type;
					ret.abnormalState = con.abnormalState;
				}
				break;
			case is_last_waypoint:
				if (c instanceof IsLastWaypointCondition) {
					IsLastWaypointCondition con = (IsLastWaypointCondition) c;
					ret.type = con.type;
				}
				break;
			case is_message:
				if (c instanceof IsMessageCondition) {
					IsMessageCondition con = (IsMessageCondition) c;
					ret.type = con.type;
					ret.messageType = con.messageType;
				}
				break;
			case is_my_curent_target:
				if (c instanceof IsMyCurentTargetCondition) {
					IsMyCurentTargetCondition con = (IsMyCurentTargetCondition) c;
					ret.type = con.type;
					ret.objI = con.who;
				}
				break;
			case is_npc:
				if (c instanceof IsNpcCondition) {
					IsNpcCondition con = (IsNpcCondition) c;
					ret.type = con.type;
					ret.objI = con.objIndicator;
				}
				break;
			case is_npc_state:
				if (c instanceof IsNpcStateCondition) {
					IsNpcStateCondition con = (IsNpcStateCondition) c;
					ret.type = con.type;
					ret.npcI = con.who;
					ret.state = con.state;
				}
				break;
			case is_obj_in_abnormal_state:
				if (c instanceof IsObjInAbnormalStateCondition) {
					IsObjInAbnormalStateCondition con = (IsObjInAbnormalStateCondition) c;
					ret.type = con.type;
					ret.objI = con.obj;
					ret.abnormalState = con.abnormalState;
				}
				break;
			case is_race:
				if (c instanceof IsRaceCondition) {
					IsRaceCondition con = (IsRaceCondition) c;
					ret.type = con.type;
					ret.objI = con.from;
					ret.raceType = con.raceType;
				}
				break;
			case is_skill_count_left:
				if (c instanceof IsSkillCountLeftCondition) {
					IsSkillCountLeftCondition con = (IsSkillCountLeftCondition) c;
					ret.type = con.type;
					ret.skill = con.skill;
				}
				break;
			case is_user:
				if (c instanceof IsUserCondition) {
					IsUserCondition con = (IsUserCondition) c;
					ret.type = con.type;
					ret.objI = con.objIndicator;
				}
				break;
			case is_user_class:
				if (c instanceof IsUserClassCondition) {
					IsUserClassCondition con = (IsUserClassCondition) c;
					ret.type = con.type;
					ret.userI = con.user;
					ret.classI = con.classI;
				}
				break;
			case is_user_flying:
				if (c instanceof IsUserFlyingCondition) {
					IsUserFlyingCondition con = (IsUserFlyingCondition) c;
					ret.type = con.type;
					ret.userI = con.user;
				}
				break;
			case is_waypoint_index:
				if (c instanceof IsWaypointIndexCondition) {
					IsWaypointIndexCondition con = (IsWaypointIndexCondition) c;
					ret.type = con.type;
					ret.index = con.index;
				}
				break;
			case set_flag_var:
				if (c instanceof SetFlagVarCondition) {
					SetFlagVarCondition con = (SetFlagVarCondition) c;
					ret.type = con.type;
					ret.flagvarIndicator = con.flagvarIndicator;
				}
				break;
			case set_intvar_if_larger_than:
				if (c instanceof SetIntvarIfLargerThanCondition) {
					SetIntvarIfLargerThanCondition con = (SetIntvarIfLargerThanCondition) c;
					ret.type = con.type;
					ret.intvarIndicator = con.intvarIndicator;
					ret.intvarToSet = con.intvarToSet;
					ret.comparand = con.comparand;
				}
				break;
			case set_intvar_if_less_than:
				if (c instanceof SetIntvarIfLessThanCondition) {
					SetIntvarIfLessThanCondition con = (SetIntvarIfLessThanCondition) c;
					ret.type = con.type;
					ret.intvarIndicator = con.intvarIndicator;
					ret.intvarToSet = con.intvarToSet;
					ret.comparand = con.comparand;
				}
				break;
			case set_world_flag_var:
				if (c instanceof SetWorldFlagVarCondition) {
					SetWorldFlagVarCondition con = (SetWorldFlagVarCondition) c;
					ret.type = con.type;
					ret.flagvarIndicator = con.flagvarIndicator;
				}
				break;
			case test_probability:
				if (c instanceof TestProbabilityCondition) {
					TestProbabilityCondition con = (TestProbabilityCondition) c;
					ret.type = con.type;
					ret.percent = con.percent;
				}
				break;
			case unset_flag_var:
				if (c instanceof UnsetFlagVarCondition) {
					UnsetFlagVarCondition con = (UnsetFlagVarCondition) c;
					ret.type = con.type;
					ret.flagvarIndicator = con.flagvarIndicator;
				}
				break;
			case unset_world_flag_var:
				if (c instanceof UnsetWorldFlagVarCondition) {
					UnsetWorldFlagVarCondition con = (UnsetWorldFlagVarCondition) c;
					ret.type = con.type;
					ret.flagvarIndicator = con.flagvarIndicator;
				}
				break;
			case decrease_intvar:
				if (c instanceof DecreaseIntvarCondition) {
					DecreaseIntvarCondition con = (DecreaseIntvarCondition) c;
					ret.type = con.type;
					ret.intvarIndicator = con.intvarIndicator;
					ret.lowerBound = con.lowerBound;
					ret.upperBound = con.upperBound;
					ret.beTrueOnlyWhenHitTheBound = con.beTrueOnlyWhenHitTheBound;
				}
				break;
			case has_attack_damage_flag:
				if (c instanceof HasAttackDamageFlagCondition) {
					HasAttackDamageFlagCondition con = (HasAttackDamageFlagCondition) c;
					ret.type = con.type;
					ret.damageFlag = con.damageFlag;
				}
				break;
			case increase_intvar:
				if (c instanceof IncreaseIntvarCondition) {
					IncreaseIntvarCondition con = (IncreaseIntvarCondition) c;
					ret.type = con.type;
					ret.intvarIndicator = con.intvarIndicator;
					ret.lowerBound = con.lowerBound;
					ret.upperBound = con.upperBound;
					ret.beTrueOnlyWhenHitTheBound = con.beTrueOnlyWhenHitTheBound;
				}
				break;
			case is_hyperlink_id:
				if (c instanceof IsHyperlinkIdCondition) {
					IsHyperlinkIdCondition con = (IsHyperlinkIdCondition) c;
					ret.type = con.type;
					ret.hyperlinkId = con.hyperlinkId;
				}
				break;
			case is_target_quest_state:
				if (c instanceof IsTargetQuestStateCondition) {
					IsTargetQuestStateCondition con = (IsTargetQuestStateCondition) c;
					ret.type = con.type;
					ret.objI = con.target;
					ret.questId = con.questId;
					ret.questProgress = con.questProgress;
				}
				break;
			case is_world_flag_var:
				if (c instanceof IsWorldFlagVarCondition) {
					IsWorldFlagVarCondition con = (IsWorldFlagVarCondition) c;
					ret.type = con.type;
					ret.flagvarIndicator = con.flagvarIndicator;
					ret.flagExpected = con.flagExpected;
				}
				break;
			default:
				assert false:"Unsupported Condition Type: " + c.type;
		}
		return ret;
	}
	
}

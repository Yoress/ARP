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
package com.aionemu.gameserver.ai2.mechanics.conditions;

import javax.xml.bind.annotation.XmlType;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@XmlType
public enum ConditionType {
	set_world_flag_var,
	is_hp_lower_than,
	is_last_waypoint,
	set_intvar_if_less_than,
	is_abnormal_state,
	add_intvar,
	is_skill_count_left,
	is_enemy,
	is_my_curent_target,
	is_user_flying,
	is_race,
	is_user,
	is_distance_shorter_than,
	test_probability,
	is_npc,
	is_event_skill_category,
	is_message,
	is_hp_in_boundary,
	is_in_abnormal_state,
	is_npc_state,
	is_user_class,
	is_distance_longer_than,
	set_flag_var,
	is_battle_timer_indicator,
	set_intvar_if_larger_than,
	is_event_skill_id,
	unset_world_flag_var,
	is_waypoint_index,
	is_obj_in_abnormal_state,
	unset_flag_var,
	is_hyperlink_id,
	increase_intvar,
	has_attack_damage_flag,
	decrease_intvar,
	is_world_flag_var,
	is_target_quest_state;
}

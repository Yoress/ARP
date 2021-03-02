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
package com.aionemu.gameserver.ai2.mechanics.actions;

import javax.xml.bind.annotation.XmlType;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@XmlType
public enum ActionType {
	use_skill,
	despawn_self,
	do_nothing,
	add_battle_timer,
	say_to_all,
	spawn,
	goto_waypoint,
	goto_next_waypoint,
	set_condition_spawn_variable,
	broadcast_message,
	despawn,
	flee_from,
	spawn_on_target,
	switch_target_by_attacker_indicator,
	add_hate_point,
	use_skill_by_attacker_indicator,
	switch_target,
	set_idle_timer,
	switch_target_by_class_indicator,
	send_message,
	attack_most_hating,
	spawn_on_target_by_attacker_indicator,
	random_move,
	send_system_msg,
	say_to_all_str,
	display_system_message,
	broadcast_message_to_party,
	goto_alias,
	reset_hatepoints,
	teleport_target_alias,
	teleport_target,
	spawn_on_multi_target,
	give_abysspoint,
	give_exp,
	give_money,
	give_world_score,
	charge_limitedquest,
	control_door,
	give_item_by_user_indicator,
	change_world_scene_status,
	reset_queued_actions,
	give_score,
	give_item_by_obj_indicator,
	play_cutscene_by_user_indicator,
	on_off_moving_collision,
	on_off_windpath,
	enable_area,
	return_to_spawn_point,
	close_dialog,
	shout_to_all,
	close_directportal,
	say,
	toggle_attackable_status_flag,
	change_direction;
}

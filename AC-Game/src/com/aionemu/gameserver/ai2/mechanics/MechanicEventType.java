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
package com.aionemu.gameserver.ai2.mechanics;

import javax.xml.bind.annotation.XmlType;

/**
 * Event types for the various AI mechanics; these names were originally taken from a set of AI data that
 * looked to be either a copy of a retail file, or a system based on it.
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@XmlType
public enum MechanicEventType {
	/**
	 * When a player is seen. Mind that this is when the player enters aggro range, and not when
	 * the player enters the known list.
	 */
	on_see_user,
	
	/**
	 * When AIState shifts to FIGHT
	 */
	on_enter_attack_state,
	
	/**
	 * When a battle timer finishes.
	 */
	on_battle_timer,
	
	/**
	 * Possibly when the AI activates or the owner of the AI spawns/respawns; that's how it's currently implemented.
	 * <p>
	 * It's possible this means something else, but that's currently unknown.
	 */
	on_wake_up,
	
	/**
	 * When the AI gets a destination reached event while walking.
	 */
	on_arrived_at_waypoint,
	
	/**
	 * When the owner of the AI dies.
	 */
	on_die,
	
	/**
	 * When a nearby entity sends a message; this will be handled in the custom event handling of the MechanicsAI.
	 */
	on_message,
	
	/**
	 * When something attacks the owner of the AI.
	 */
	on_attacked,
	
	/**
	 * When something uses a skill on the owner of the mechanic.
	 */
	on_spelled,
	
	/**
	 * When something is attacking a nearby friendly entity.
	 */
	on_see_friend_attacked,
	
	/**
	 * When something uses a skill on a nearby friendly entity.
	 */
	on_friend_spelled,
	
	/**
	 * When the AIState shifts away from FIGHT.
	 */
	on_leave_attack_state,
	
	/**
	 * When the owner of the AI dies to a player.
	 */
	on_killed_by_user,
	
	/**
	 * When the owner of the AI is placed into an altered state.
	 */
	on_enter_abnormal_state,
	
	/**
	 * When a nearby friendly entity is slain by a player.
	 */
	on_sense_friend_killed_by_user,
	
	/**
	 * When the owner of the AI dies to a non-player.
	 */
	on_killed_by_npc,
	
	/**
	 * When the AIState shifts to FEAR, and the current action is interrupted.
	 */
	on_stop_to_flee,
	
	/**
	 * When the owner detects a nearby player has moved.
	 */
	on_see_user_move,
	
	/**
	 * Unknown; likely an internal timer for retail. Fired by SetIdleTimerAction.
	 */
	on_idle_timer,
	
	/**
	 * When the AIState shifts to IDLE
	 */
	on_enter_idle_state,
	
	/**
	 * When the owner of the AI despawns.
	 */
	on_despawn,
	
	/**
	 * When the owner of the AI detects a non-player nearby.
	 */
	on_see_npc,
	
	/**
	 * When a nearby friendly entity is attacking something.
	 */
	on_see_friend_attacking,
	
	/**
	 * When a nearby friendly entity uses a skill.
	 */
	on_friend_spelling,
	
	/**
	 * When the AIState is WALKING, the AISubState is RANDOMWALK, and the owner of the AI is about to randomly move in its area.
	 */
	on_stop_to_random_move,
	
	/**
	 * When a player talks with the owner of the AI.
	 */
	on_talked_by_user,
	
	/**
	 * When the aggro list's most hated changes.
	 */
	on_most_hating_updated,
	
	/**
	 * When the owner of the AI detects a non-player Creature moving.
	 */
	on_see_npc_move,
	
	/**
	 * When a nearby friendly entity dies to a Player.
	 */
	on_see_friend_killed_by_user,
	
	/**
	 * When a nearby entity uses a skill; range unknown, so aggro range will be used.
	 */
	on_see_spell,
	
	/**
	 * When the owner of the AI's altered state wears off.
	 */
	on_leave_abnormal_state,
	
	/**
	 * When the owner of the AI takes damage from something.
	 */
	on_damaged,
	
	/**
	 * When a nearby friendly entity's AIState shifts to FIGHT.
	 */
	on_friend_enter_attack_state,
	
	/**
	 * Unknown; assumptions are that this is either when the owner has finished a cast, started a cast, or the owner has a nearby entity
	 * starting a cast, or completing a cast towards it.
	 * <p>
	 * The data so far only uses this with Tamer Anikiki without conditions and the only action is to do nothing.
	 * New data has this is a couple other places, checking for a sanctuary effect on the owner and doing nothing if
	 * present. Speculation that this is when the owner completes a cast comes back -- these use cases may be
	 * for determining when an AI should stop acting while shielded.
	 * <p>
	 * This is not currently implemented.
	 */
	on_casted,
	
	/**
	 * When the AI gets a destination reached event while not in AIState WALKING.
	 */
	on_arrived_at_point,
	
	/**
	 * When the owner of the AI resets.
	 */
	on_enter_return_sp,
	
	/**
	 * When the owner of the AI stops resetting. Pretty sure this emulator doesn't currently allow mobs to stop resetting,
	 * so this is not implemented at this time.
	 */
	on_leave_return_sp,
	
	/**
	 * When a user clicks on a dialog option while talking to the owner of the AI.
	 */
	on_hyperlink_clicked,
	
	/**
	 * When a user completes a quest through the owner of the AI?
	 * <p>
	 * The only time this is used is for some sort of test quest; it will not be implemented, as custom quest
	 * handlers are more accessible.
	 */
	on_quest_finished,
	
	/**
	 * When a player enters into some area within or nearby the owner of the AI's area.
	 * <p>
	 * This is likely not going to be implemented, as the sensory area for things is unknown.
	 */
	on_user_enter_sensory_area,
	
	/**
	 * When a nearby entity uses an auto attack; range unknown, so aggro range will be used.
	 */
	on_see_attacked,
	
	/**
	 * When the owner of the AI sees its master hit by a skill. Range unknown; aggro range will be used.
	 */
	on_see_master_spelled,
	
	/**
	 * When a player heals the owner of the AI?
	 */
	on_healed_by_user,
	
	/**
	 * When the owner of the AI has completed spawning in.
	 */
	on_leave_wakeup_state,
	
	/**
	 * When the owner of the AI is being created.
	 */
	on_enter_wakeup_state,
	
	/**
	 * When an entity in the same group auto attacks something.
	 * <p>
	 * It's unclear what this is; speculation that it may be mob parties (as in, spawned together as allies).
	 * TODO
	 */
	on_party_mbr_attacking,
	
	/**
	 * When an entity in the same group is auto attacked by something.
	 * <p>
	 * It's unclear what this is; speculation that it may be mob parties (as in, spawned together as allies).
	 * TODO
	 */
	on_party_mbr_attacked,
	
	/**
	 * When an entity in the same group is hit by something's skill.
	 * <p>
	 * It's unclear what this is; speculation that it may be mob parties (as in, spawned together as allies).
	 * TODO
	 */
	on_party_mbr_spelled,
	
	/**
	 * When an entity in the same group shifts into a FIGHT state.
	 * <p>
	 * It's unclear what this is; speculation that it may be mob parties (as in, spawned together as allies).
	 * TODO
	 */
	on_party_mbr_enter_attack_state,
	
	/**
	 * When a player interacts with the owner of the AI (which might be a tower, for example) and it
	 * starts a cast bar for that player
	 * TODO
	 */
	on_gauge_begin,
	
	/**
	 * When a player's interaction with the owner of the AI (which might be a tower, for example) that has
	 * a cast bar for that player is interrupted.
	 * TODO
	 */
	on_gauge_stop,
	
	/**
	 * When a player's interaction with the owner of the AI (which might be a tower, for example) that has
	 * a cast bar for that player is completed.
	 * TODO
	 */
	on_gauge_end,
	
	/**
	 * When the owner of the AI sees its master hit by an auto attack. Range unknown; aggro range will be used.
	 */
	on_master_attacked;
	
}

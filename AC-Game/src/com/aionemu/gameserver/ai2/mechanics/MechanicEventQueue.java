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

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.LinkedBlockingQueue;

import com.aionemu.gameserver.ai2.mechanics.actions.Action;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;

/**
 * A simple FIFO queue for scheduling Mechanic actions (backed by a {@link LinkedBlockingQueue}).
 * 
 * @see LinkedBlockingQueue
 * @author Yon (Aion Reconstruction Project)
 */
public class MechanicEventQueue {
	
	private final class EventActionList {
		
		final MechanicEvent event;
		
		final Action[] actions;
		
		private byte pos = 0;
		
		EventActionList(final MechanicEvent event, final Action[] actions) {
			this.event = event;
			this.actions = actions;
		}
		
		boolean hasNext() {return pos < actions.length;}
		
		Action next(boolean advance) {
			if (pos < actions.length) {
				return actions[(advance ? pos++ : pos)];
			}
			return null;
		}
	}
	
	final class EventActionPair {
		
		final MechanicEvent event;
		
		final Action action;
		
		EventActionPair(final MechanicEvent event, final Action action) {
			this.event = event;
			this.action = action;
		}
	}
	
	private final LinkedBlockingQueue<EventActionList> queue = new LinkedBlockingQueue<EventActionList>();
	
	private final boolean[] eventList = new boolean[MechanicEventType.values().length];
	
	private byte pullAttempts = 0;
	
	public MechanicEventQueue() {
		super();
	}
	
	public void clear() {
		Arrays.fill(eventList, false);
		pullAttempts = 0;
		queue.clear();
	}
	
	public boolean isEmpty() {
		return queue.isEmpty();
	}
	
	public boolean cannotAcceptEvent(MechanicEventType type) {
		switch (type) {
			case on_arrived_at_point:
			case on_arrived_at_waypoint:
			case on_battle_timer:
			case on_despawn:
			case on_die:
			case on_enter_attack_state:
			case on_enter_idle_state:
			case on_enter_return_sp:
			case on_idle_timer:
			case on_killed_by_npc:
			case on_killed_by_user:
			case on_leave_abnormal_state:
			case on_leave_attack_state:
			case on_leave_return_sp:
			case on_message:
			case on_talked_by_user:
			case on_stop_to_flee:
			case on_stop_to_random_move:
			case on_wake_up:
				return false;
			case on_attacked:
			case on_casted:
			case on_damaged:
			case on_enter_abnormal_state:
			case on_friend_enter_attack_state:
			case on_friend_spelled:
			case on_friend_spelling:
			case on_most_hating_updated:
			case on_see_friend_attacked:
			case on_see_friend_attacking:
			case on_see_friend_killed_by_user:
			case on_see_npc:
			case on_see_npc_move:
			case on_see_spell:
			case on_see_user:
			case on_see_user_move:
			case on_sense_friend_killed_by_user:
			case on_spelled:
			default: return eventList[type.ordinal()];
		}
	}
	
	public boolean add(MechanicEvent event, Action[] actions) {
		if (event == null || actions.length == 0) return false;
		if (!cannotAcceptEvent(event.type)) {
			eventList[event.type.ordinal()] = true;
			return queue.add(new EventActionList(event, actions));
		}
		return false;
	}
	
	public void incrementPullAttempts() {
		pullAttempts++;
	}
	
	public boolean isPastPullAttemptThreshold(int threshold) {
		return threshold < pullAttempts;
	}
	
	/**
	 * This will return the head of the queue without removing it,
	 * or null if the queue is empty.
	 * 
	 * @return The head of the queue, or null if the queue is empty.
	 */
	public EventActionPair poke() {
		synchronized (queue) {
			EventActionList next = queue.peek();
			if (next == null) return null;
			assert next.hasNext():"next doesn't have next!? pos: " + next.pos + ", actions length: " + next.actions.length; //It should not be empty if it exists, as it would be removed in #pop()
			return new EventActionPair(next.event, next.next(false));
		}
	}
	
	/**
	 * This will return the head of the queue after removing it, throwing an exception
	 * if the queue is empty.
	 * 
	 * @return The head of the queue, or throws an exception if the queue is empty.
	 * @throws NoSuchElementException if the queue is empty
	 */
	public EventActionPair pop() {
		synchronized (queue) {
			pullAttempts = 0;
			EventActionList next = queue.peek();
			if (next == null) throw new NoSuchElementException();
			assert next.hasNext():"next doesn't have next!? pos: " + next.pos + ", actions length: " + next.actions.length; //It should not be empty if it exists, as it would be removed in a moment if it were.
			EventActionPair ret = new EventActionPair(next.event, next.next(true));
			if (!next.hasNext()) {
				queue.remove();
				eventList[next.event.type.ordinal()] = false;
			}
			return ret;
		}
	}
}

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

import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.context.NpcIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.State;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class IsNpcStateCondition extends Condition {
	
	final public NpcIndicator who;
	
	final public State state;
	
	public IsNpcStateCondition(NpcIndicator who, State state) {
		super(ConditionType.is_npc_state);
		this.who = who;
		this.state = state;
	}
	
	@Override
	public boolean check(MechanicEvent event, AbstractMechanicsAI2 ai) {
		switch (who) {
			case NPCI_SELF:
				switch (state) {
					case NPC_STATE_ATTACK:
						return ai.isInState(AIState.FIGHT);
					case NPC_STATE_FLEE:
						return ai.isInState(AIState.FEAR) && ai.isInSubState(AISubState.FLEE);
					case NPC_STATE_GOTO_WAYPOINT:
						return ai.isInState(AIState.WALKING) && (ai.isInSubState(AISubState.WALK_PATH) || ai.isInSubState(AISubState.WALK_WAIT_GROUP));
					case NPC_STATE_IDLE:
						return ai.isInState(AIState.IDLE) || (ai.isInState(AIState.WALKING) && ai.isInSubState(AISubState.WALK_RANDOM));
					case NPC_STATE_WAKE_UP:
						return ai.getOwner().getPosition().isMapRegionActive() || ai.isInState(AIState.CREATED);
					case NPC_STATE_GOTO_POINT:
						//FIXME: Investigate context and fix
						return ai.getOwner().getMoveController().isInMove() && !ai.getOwner().getMoveController().isFollowingTarget()
							   && !ai.isInSubState(AISubState.WALK_PATH) && !ai.isInSubState(AISubState.WALK_RANDOM)
							   && !ai.isInSubState(AISubState.WALK_WAIT_GROUP);
					case NPC_STATE_RANDOM_MOVE:
						return ai.isInState(AIState.WALKING) && ai.isInSubState(AISubState.WALK_RANDOM);
					default:
						assert false:"Unsupported State: " + state;
				}
			default:
				assert false:"Unsupported NpcIndicator: " + who;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return 3*who.ordinal() + 5*state.ordinal();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IsNpcStateCondition) {
			IsNpcStateCondition o = (IsNpcStateCondition) obj;
			return (o.who == who && o.state == state);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + who + "] --> " + state;
	}
	
}

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

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.handler.ActivateEventHandler;
import com.aionemu.gameserver.ai2.handler.AggroEventHandler;
import com.aionemu.gameserver.ai2.handler.CreatureEventHandler;
import com.aionemu.gameserver.ai2.handler.FollowEventHandler;
import com.aionemu.gameserver.ai2.handler.ReturningEventHandler;
import com.aionemu.gameserver.ai2.handler.SpawnEventHandler;
import com.aionemu.gameserver.ai2.handler.TalkEventHandler;
import com.aionemu.gameserver.ai2.mechanics.actions.Action;
import com.aionemu.gameserver.ai2.mechanics.events.CreatureEvent;
import com.aionemu.gameserver.ai2.mechanics.events.GeneralMechanicEvent;
import com.aionemu.gameserver.ai2.mechanics.events.MessageEvent;
import com.aionemu.gameserver.ai2.mechanics.events.TalkEvent;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@AIName("mechanic_ai2")
public class MechanicsAI2 extends AbstractMechanicsAI2 {
	
	@Override
	protected void handleDied() {
		super.handleDied();
		onMechanicEvent(new GeneralMechanicEvent(MechanicEventType.on_die));
		resetMechanicSystem();
	}
	
	@Override
	protected void handleDialogStart(Player player) {
		onMechanicEvent(new TalkEvent(MechanicEventType.on_talked_by_user, player));
		TalkEventHandler.onTalk(this, player); //TODO: Might have to change this around a bit
	}
	
	@Override
	protected void handleDialogFinish(Player creature) {
		TalkEventHandler.onFinishTalk(this, creature);
	}
	
	@Override
	protected void handleFinishAttack() {
		onMechanicEvent(new GeneralMechanicEvent(MechanicEventType.on_leave_attack_state));
		resetMechanicSystem();
		super.handleFinishAttack();
	}
	
	@Override
	protected void handleMoveArrived() {
		if (getState() == AIState.WALKING) {
			if (getSubState() == AISubState.WALK_PATH) onMechanicEvent(new GeneralMechanicEvent(MechanicEventType.on_arrived_at_waypoint));
		} else {
			//FIXME: Validate!
			if (getSubState() == AISubState.WALK_ALIAS) {
				setSubStateIfNot(AISubState.NONE);
				onMechanicEvent(new GeneralMechanicEvent(MechanicEventType.on_arrived_at_point));
			}
		}
		/*
		 * TODO: Look into these events; if Do Nothing pops up, then maybe we shouldn't do the normal response.
		 * For example, if there's an idle timer based wait for moving to the next walk point, then we should
		 * let the idle timer trigger moving to the next point.
		 * <p>
		 * Note that the waypoint related actions are not currently implemented.
		 */
		super.handleMoveArrived();
	}
	
	@Override
	protected void handleCreatureMoved(Creature creature) {
		if ((creature instanceof Player || creature.getActingCreature() instanceof Player)
		&& MathUtil.isIn3dRange(getOwner(), creature, getObjectTemplate().getAggroRange())) {
			onMechanicEvent(new CreatureEvent(MechanicEventType.on_see_user, creature, null, null));
		} else if (creature instanceof Npc && MathUtil.isIn3dRange(getOwner(), creature, getObjectTemplate().getAggroRange())) {
			onMechanicEvent(new CreatureEvent(MechanicEventType.on_see_npc, creature, null, null));
		}
		
		if (isInState(AIState.FOLLOWING)) {
			FollowEventHandler.creatureMoved(this, creature);
		}// else //Might have to make this a chained if statement
		
		if (creature instanceof Player || creature.getActingCreature() instanceof Player) {
			if (!onMechanicEvent(new CreatureEvent(MechanicEventType.on_see_user_move, creature, null, null)))
				CreatureEventHandler.onCreatureMoved(this, creature);
		} else {
			if (!onMechanicEvent(new CreatureEvent(MechanicEventType.on_see_npc_move, creature, null, null)))
				CreatureEventHandler.onCreatureMoved(this, creature);
		}
	}
	
	@Override
	protected void handleActivate() {
		ActivateEventHandler.onActivate(this);
		onMechanicEvent(new GeneralMechanicEvent(MechanicEventType.on_wake_up));
		if (getState() == AIState.IDLE) onMechanicEvent(new GeneralMechanicEvent(MechanicEventType.on_enter_idle_state));
	}
	
	@Override
	protected void handleSpawned() {
		SpawnEventHandler.onSpawn(this);
		onMechanicEvent(new GeneralMechanicEvent(MechanicEventType.on_wake_up));
	}
	
	@Override
	protected void handleRespawned() {
		SpawnEventHandler.onRespawn(this);
		onMechanicEvent(new GeneralMechanicEvent(MechanicEventType.on_wake_up));
	}
	
	@Override
	protected void handleBackHome() {
		ReturningEventHandler.onBackHome(this);
		resetMechanicSystem();
	}
	
	@Override
	public boolean isMayShout() {
		return false; //Mechanics will handle it.
	}
	
	@Override
	protected void handleCreatureSee(Creature creature) {
		if ((creature instanceof Player || creature.getActingCreature() instanceof Player)
		&& MathUtil.isIn3dRange(getOwner(), creature, getObjectTemplate().getAggroRange())) {
			if (!onMechanicEvent(new CreatureEvent(MechanicEventType.on_see_user, creature, null, null)))
				CreatureEventHandler.onCreatureSee(this, creature);
		} else if (creature instanceof Npc && MathUtil.isIn3dRange(getOwner(), creature, getObjectTemplate().getAggroRange())) {
			if (!onMechanicEvent(new CreatureEvent(MechanicEventType.on_see_npc, creature, null, null)))
				CreatureEventHandler.onCreatureSee(this, creature);
		} else {
			CreatureEventHandler.onCreatureSee(this, creature);
		}
	}
	
	@Override
	protected void handleCreatureAggro(Creature creature) {
		if (canThink() && isNonFightingState()) AggroEventHandler.onAggro(this, creature);
	}
	
	@Override
	protected void handleCreatureNotSee(Creature creature) {}
	
	@Override
	protected void handleFollowMe(Creature creature) {
		FollowEventHandler.follow(this, creature);
	}
	
	@Override
	protected void handleStopFollowMe(Creature creature) {
		FollowEventHandler.stopFollow(this, creature);
	}
	
	@Override
	protected void handleCustomEvent(int eventId, Object... args) {
		//FIXME: Change this so the args passed in is just the MessageEvent itself
		if (args.length > 0 && args[0] instanceof Action) {
			onMechanicEvent(new MessageEvent(MechanicEventType.on_message, eventId, (Action) args[0], (Creature) args[1], (Creature) args[2]));
		}
	}
}

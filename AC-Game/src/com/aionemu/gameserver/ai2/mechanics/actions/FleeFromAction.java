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

import java.util.concurrent.ScheduledFuture;

import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.MechanicEventType;
import com.aionemu.gameserver.ai2.mechanics.context.ObjIndicator;
import com.aionemu.gameserver.ai2.mechanics.events.GeneralMechanicEvent;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.PositionUtil;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class FleeFromAction extends Action {
	
	public final ObjIndicator from;
	
	public final int seconds;
	
	public final boolean pushState;
	
	public FleeFromAction(ObjIndicator from, int seconds, boolean pushState) {
		super(ActionType.flee_from);
		this.from = from;
		this.seconds = seconds;
		this.pushState = pushState;
	}
	
	@Override
	public void performAction(final MechanicEvent event, final AbstractMechanicsAI2 ai) {
		//TODO: Might have to fix something in here, haven't tested it.
		if (GeoDataConfig.FEAR_ENABLE & seconds > 0 && ai.getSubState() != AISubState.FLEE) {
			final Creature effector = (Creature) event.getObjectIndicator(from, ai);
			final Npc effected = ai.getOwner();
			
			effected.getController().cancelCurrentSkill();
			
			// PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TARGET_IMMOBILIZE(effected));
			if (ai.isInState(AIState.WALKING)) {
				WalkManager.stopWalking(ai);
			} else {
				effected.getMoveController().abortMove();
			}
			effected.getController().stopMoving();
			
			if (!effected.isInState(CreatureState.WEAPON_EQUIPPED)) {
				effected.setState(CreatureState.WEAPON_EQUIPPED);
				PacketSendUtility.broadcastPacket(effected, new SM_EMOTION(effected, EmotionType.START_EMOTE2, 0, effector.getObjectId()));
				PacketSendUtility.broadcastPacket(effected, new SM_EMOTION(effected, EmotionType.ATTACKMODE, 0, effector.getObjectId()));
			}
			
			ai.setStateIfNot(AIState.FEAR);
			ai.setSubStateIfNot(AISubState.FLEE);
			if (pushState) {
				ai.onMechanicEvent(new GeneralMechanicEvent(MechanicEventType.on_stop_to_flee));
			}
			
			final ScheduledFuture<?> fearTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new FearTask(effector, effected), 0, 1000);
			
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					fearTask.cancel(true);
					if (ai.isInSubState(AISubState.FLEE)) {
						effected.getMoveController().abortMove();
//						PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TARGET_IMMOBILIZE(effected));
						ai.setSubStateIfNot(AISubState.NONE);
						if (effected.getAggroList().isHating(effector)) {
							ai.onCreatureEvent(AIEventType.ATTACK, effector);
						} else {
							//This might break, but oh well.
							if (ai.setStateIfNot(AIState.FIGHT)) ai.think();
						}
					}
				}
			}, seconds * 1000);
		}
	}
	
	private class FearTask implements Runnable {
		
		private Creature effector;
		private Creature effected;
		
		public FearTask(Creature effector, Creature effected) {
			this.effector = effector;
			this.effected = effected;
		}
		
		@Override
		public void run() {
			if (((AbstractAI) effected.getAi2()).isInSubState(AISubState.FLEE)) {
				float x = effected.getX();
				float y = effected.getY();
				if (!MathUtil.isNearCoordinates(effected, effector, 40)) {
					return;
				}
				byte moveAwayHeading = PositionUtil.getMoveAwayHeading(effector, effected);
				double radian = Math.toRadians(MathUtil.convertHeadingToDegree(moveAwayHeading));
				float maxDistance = effected.getGameStats().getMovementSpeedFloat();
				float x1 = (float) (Math.cos(radian) * maxDistance);
				float y1 = (float) (Math.sin(radian) * maxDistance);
				byte intentions = (byte) (CollisionIntention.PHYSICAL.getId() | CollisionIntention.DOOR.getId());
				Vector3f closestCollision = GeoService.getInstance().getClosestCollision(effected, x + x1, y + y1, effected.getZ(), true, intentions);
				if (effected.isFlyingOrGliding()) {
					closestCollision.setZ(effected.getZ());
				}
				if (Thread.interrupted()) return;
				if (effected instanceof Npc) {
					((Npc) effected).getMoveController().resetMove();
					((Npc) effected).getMoveController().moveToPoint(closestCollision.getX(), closestCollision.getY(), closestCollision.getZ());
				} else {
					effected.getMoveController().setNewDirection(closestCollision.getX(), closestCollision.getY(), closestCollision.getZ(), moveAwayHeading);
					effected.getMoveController().startMovingToDestination();
				}
			}
		}
	}
	
	@Override
	public int hashCode() {
		return 3*from.ordinal() + 5*seconds + 7*(pushState ? 1 : 0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FleeFromAction) {
			return (((FleeFromAction) obj).from == from && ((FleeFromAction) obj).seconds == seconds && ((FleeFromAction) obj).pushState == pushState);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + from + "] --> " + seconds;
	}
	
}

/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 * Aion-Lightning is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Aion-Lightning is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. *
 *
 * You should have received a copy of the GNU General Public License along with Aion-Lightning. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * Credits goes to all Open Source Core Developer Groups listed below Please do not change here something, ragarding the developer credits, except the
 * "developed by XXXX". Even if you edit a lot of files in this source, you still have no rights to call it as "your Core". Everybody knows that this
 * Emulator Core was developed by Aion Lightning
 * 
 * @-Aion-Unique-
 * @-Aion-Lightning
 * @Aion-Engine
 * @Aion-Extreme
 * @Aion-NextGen
 * @Aion-Core Dev.
 */
package com.aionemu.gameserver.ai2.handler;

import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.manager.AttackManager;
import com.aionemu.gameserver.ai2.manager.FollowManager;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;

/**
 * @author ATracer
 * @modified Yon (Aion Reconstruction Project) -- {@link #onTargetReached(NpcAI2)} no longer supports {@link AIState#FIGHT} attack handling,
 * and no longer misuses the movement controller's recallPreviousStep() method; removed deprecated method calls.
 */
public class TargetEventHandler {
	
	/**
	 * If the AI is in a {@link AIState#FIGHT FIGHT State}, attacking should be
	 * handled internally by the AI.
	 * 
	 * @param npcAI
	 */
	public static void onTargetReached(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onTargetReached");
		}
		
		AIState currentState = npcAI.getState();
		switch (currentState) {
		case FIGHT: //attack handling was deprecated, and now is no longer supported
			npcAI.getOwner().getMoveController().abortMove();
//			AttackManager.scheduleNextAttack(npcAI); //Deprecated Method
//			if (npcAI.getOwner().getMoveController().isFollowingTarget()) {
//				npcAI.getOwner().getMoveController().storeStep();
//			}
			break;
		case RETURNING:
			npcAI.getOwner().getMoveController().abortMove();
//			npcAI.getOwner().getMoveController().recallPreviousStep(); //This shouldn't be called here; it gets called properly via NotAtHome event.
			if (npcAI.getOwner().isAtSpawnLocation()) {
				npcAI.onGeneralEvent(AIEventType.BACK_HOME);
			} else {
				npcAI.onGeneralEvent(AIEventType.NOT_AT_HOME);
			}
			break;
		case FOLLOWING:
			npcAI.getOwner().getMoveController().abortMove();
//			npcAI.getOwner().getMoveController().storeStep();
			break;
		case FEAR:
			npcAI.getOwner().getMoveController().abortMove();
//			npcAI.getOwner().getMoveController().storeStep();
			break;
		case WALKING:
			WalkManager.targetReached(npcAI);
			checkAggro(npcAI); //TODO: Why is this shit here?
			break;
		default:
			break;
		}
	}
	
	/**
	 * @param npcAI
	 */
	public static void onTargetTooFar(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onTargetTooFar");
		}
		switch (npcAI.getState()) {
		case FIGHT:
			AttackManager.targetTooFar(npcAI);
			break;
		case FOLLOWING:
			FollowManager.targetTooFar(npcAI);
			break;
		case FEAR:
			break;
		default:
			if (npcAI.isLogging()) {
				AI2Logger.info(npcAI, "default onTargetTooFar");
			}
		}
	}
	
	/**
	 * @param npcAI
	 */
	public static void onTargetGiveup(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onTargetGiveup");
		}
		VisibleObject target = npcAI.getOwner().getTarget();
		if (target != null) {
			npcAI.getOwner().getAggroList().stopHating(target);
		}
		if (npcAI.isMoveSupported()) {
			npcAI.getOwner().getMoveController().abortMove();
		}
		if (!npcAI.isAlreadyDead()) {
			npcAI.think();
		}
	}
	
	/**
	 * @param npcAI
	 * @deprecated Attack handling has been moved into the AI itself.
	 */
	@Deprecated
	public static void onTargetChange(NpcAI2 npcAI, Creature creature) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onTargetChange");
		}
		if (npcAI.isInState(AIState.FIGHT)) {
			npcAI.getOwner().setTarget(creature);
			AttackManager.scheduleNextAttack(npcAI);
		}
	}
	
	private static void checkAggro(NpcAI2 npcAI) {
		for (VisibleObject obj : npcAI.getOwner().getKnownList().getKnownObjects().values()) {
			if (obj instanceof Creature) {
				CreatureEventHandler.checkAggro(npcAI, (Creature) obj);
			}
		}
	}
}

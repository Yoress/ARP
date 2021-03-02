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
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.manager.AttackManager;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * @author ATracer
 * @modified Yon (Aion Reconstruction Project) -- deprecated {@link #onAttack(NpcAI2, Creature)}, {@link #onForcedAttack(NpcAI2)},
 * {@link #onAttackComplete(NpcAI2)}; reset skill counts in {@link #onFinishAttack(NpcAI2)} for new skill list support.
 */
public class AttackEventHandler {
	
	/**
	 * @param npcAI
	 * @param creature
	 * @deprecated Attack handling has been moved into the AI itself.
	 */
	@Deprecated
	public static void onAttack(NpcAI2 npcAI, Creature creature) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onAttack");
		}
		if (creature == null || creature.getLifeStats().isAlreadyDead()) {
			return;
		}
		// TODO lock or better switch
		if (npcAI.isInState(AIState.RETURNING)) {
			npcAI.getOwner().getMoveController().abortMove();
			npcAI.setStateIfNot(AIState.IDLE);
			npcAI.onGeneralEvent(AIEventType.NOT_AT_HOME);
			return;
		}
		if (!npcAI.canThink()) {
			return;
		}
		if (npcAI.isInState(AIState.WALKING)) {
			WalkManager.stopWalking(npcAI);
		}
		npcAI.getOwner().getGameStats().renewLastAttackedTime();
		if (!npcAI.isInState(AIState.FIGHT)) {
			npcAI.setStateIfNot(AIState.FIGHT);
			if (npcAI.isLogging()) {
				AI2Logger.info(npcAI, "onAttack() -> startAttacking");
			}
			npcAI.setSubStateIfNot(AISubState.NONE);
			npcAI.getOwner().setTarget(creature);
			AttackManager.startAttacking(npcAI); //Deprecated Method
			if (npcAI.poll(AIQuestion.CAN_SHOUT)) {
				ShoutEventHandler.onAttackBegin(npcAI, (Creature) npcAI.getOwner().getTarget());
			}
		}
	}
	
	/**
	 * @param npcAI
	 * @deprecated Attack handling has been moved into the AI itself.
	 */
	@Deprecated
	public static void onForcedAttack(NpcAI2 npcAI) {
		onAttack(npcAI, (Creature) npcAI.getOwner().getTarget());
	}
	
	/**
	 * @param npcAI
	 * @deprecated Attack handling has been moved into the AI itself.
	 */
	@Deprecated
	public static void onAttackComplete(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onAttackComplete: " + npcAI.getOwner().getGameStats().getLastAttackTimeDelta());
		}
		npcAI.getOwner().getGameStats().renewLastAttackTime();
		AttackManager.scheduleNextAttack(npcAI); //Deprecated Method
	}
	
	/**
	 * @param npcAI
	 */
	public static void onFinishAttack(NpcAI2 npcAI) {
		if (!npcAI.canThink()) {
			return;
		}
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onFinishAttack");
		}
		Npc npc = npcAI.getOwner();
		EmoteManager.emoteStopAttacking(npc);
		npc.getLifeStats().startResting();
		npc.getAggroList().clear();
		if (npcAI.poll(AIQuestion.CAN_SHOUT)) {
			ShoutEventHandler.onAttackEnd(npcAI);
		}
		npc.setTarget(null);
		npc.setSkillNumber(0);
		npc.getSkillList().resetCounts();
	}
}

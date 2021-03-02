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
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * @author ATracer
 * @modified Yon (Aion Reconstruction Project) -- {@link #thinkAttack(NpcAI2)} no longer abuses the movement controller's
 * recallPreviousPoint() method.
 */
public class ThinkEventHandler {
	
	/**
	 * @param npcAI
	 */
	public static void onThink(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "think");
		}
		if (npcAI.isAlreadyDead()) {
			AI2Logger.info(npcAI, "can't think in dead state");
			return;
		}
		if (!npcAI.tryLockThink()) {
			AI2Logger.info(npcAI, "can't acquire lock");
			return;
		}
		try {
			if (!npcAI.getOwner().getPosition().isMapRegionActive() || npcAI.getSubState() == AISubState.FREEZE) {
				thinkInInactiveRegion(npcAI);
				return;
			}
			if (npcAI.isLogging()) {
				AI2Logger.info(npcAI, "think state " + npcAI.getState());
			}
			switch (npcAI.getState()) {
			case FIGHT:
				thinkAttack(npcAI);
				break;
			case WALKING:
				thinkWalking(npcAI);
				break;
			case IDLE:
				thinkIdle(npcAI);
				break;
			default:
				break;
			}
		} finally {
			npcAI.unlockThink();
		}
	}
	
	/**
	 * @param npcAI
	 */
	private static void thinkInInactiveRegion(NpcAI2 npcAI) {
		
		if (!npcAI.canThink()) {
			return;
		}
		
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "think in inactive region: " + npcAI.getState());
		}
		switch (npcAI.getState()) {
		case FIGHT:
			thinkAttack(npcAI);
			break;
		default:
			if (!npcAI.getOwner().isAtSpawnLocation()) {
				npcAI.onGeneralEvent(AIEventType.NOT_AT_HOME);
			}
		}
		
	}
	
	/**
	 * @param npcAI
	 */
	public static void thinkAttack(NpcAI2 npcAI) {
		Npc npc = npcAI.getOwner();
		Creature mostHated = npc.getAggroList().getMostHated();
		if (mostHated != null && !mostHated.getLifeStats().isAlreadyDead()) {
			npcAI.onCreatureEvent(AIEventType.TARGET_CHANGED, mostHated);
		} else {
//			npc.getMoveController().recallPreviousStep(); //Should not be called here; it's handled properly via NOT_AT_HOME event
			npcAI.onGeneralEvent(AIEventType.ATTACK_FINISH);
			npcAI.onGeneralEvent(npc.isAtSpawnLocation() ? AIEventType.BACK_HOME : AIEventType.NOT_AT_HOME);
		}
	}
	
	/**
	 * @param npcAI
	 */
	public static void thinkWalking(NpcAI2 npcAI) {
		WalkManager.startWalking(npcAI);
	}
	
	/**
	 * @param npcAI
	 */
	public static void thinkIdle(NpcAI2 npcAI) {
		if (WalkManager.isWalking(npcAI)) {
			boolean startedWalking = WalkManager.startWalking(npcAI);
			if (!startedWalking) {
				npcAI.setStateIfNot(AIState.IDLE);
			}
		}
	}
}

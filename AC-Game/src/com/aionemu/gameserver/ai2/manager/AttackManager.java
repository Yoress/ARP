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
package com.aionemu.gameserver.ai2.manager;

import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.AttackIntention;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * @author ATracer
 * @modified Yon (Aion Reconstruction Project) -- deprecated handling that's being moved inside AI's, removed
 * non-retail-like leash in {@link #checkGiveupDistance(NpcAI2)}.
 */
public class AttackManager {
	
	/**
	 * @param npcAI
	 * @deprecated Attack handling has been moved into the AI itself.
	 */
	@Deprecated
	public static void startAttacking(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "AttackManager: startAttacking");
		}
		npcAI.getOwner().getGameStats().setFightStartingTime();
		EmoteManager.emoteStartAttacking(npcAI.getOwner());
		scheduleNextAttack(npcAI);
	}
	
	/**
	 * @param npcAI
	 * @deprecated Attack handling has been moved into the AI itself.
	 */
	@Deprecated
	public static void scheduleNextAttack(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "AttackManager: scheduleNextAttack");
		}
		// don't start attack while in casting substate
		AISubState subState = npcAI.getSubState();
		if (subState == AISubState.NONE) {
			chooseAttack(npcAI, npcAI.getOwner().getGameStats().getNextAttackInterval());
		} else {
			if (npcAI.isLogging()) {
				AI2Logger.info(npcAI, "Will not choose attack in substate" + subState);
			}
		}
	}
	
	/**
	 * choose attack type
	 * @deprecated Attack handling has been moved into the AI itself.
	 */
	@Deprecated
	private static void chooseAttack(NpcAI2 npcAI, int delay) {
		AttackIntention attackIntention = npcAI.chooseAttackIntention(null);
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "AttackManager: chooseAttack " + attackIntention + " delay " + delay);
		}
		if (!npcAI.canThink()) {
			return;
		}
		switch (attackIntention) {
		case SIMPLE_ATTACK:
			SimpleAttackManager.performAttack(npcAI, delay);
			break;
		case SKILL_ATTACK:
			SkillAttackManager.performAttack(npcAI, delay);
			break;
		case FINISH_ATTACK:
			npcAI.think();
			break;
		default:
			break;
		}
	}
	
	/**
	 * @param npcAI
	 */
	public static void targetTooFar(NpcAI2 npcAI) {
		Npc npc = npcAI.getOwner();
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "AttackManager: attackTimeDelta " + npc.getGameStats().getLastAttackTimeDelta());
		}
		
		// switch target if there is more hated creature
		if (npc.getGameStats().getLastChangeTargetTimeDelta() > 5) {
			Creature mostHated = npc.getAggroList().getMostHated();
			if (mostHated != null && !mostHated.getLifeStats().isAlreadyDead() && !npc.isTargeting(mostHated.getObjectId())) {
				if (npcAI.isLogging()) {
					AI2Logger.info(npcAI, "AttackManager: switching target during chase");
				}
				npcAI.onCreatureEvent(AIEventType.TARGET_CHANGED, mostHated);
				return;
			}
		}
		if (!npc.canSee((Creature) npc.getTarget())) {
			npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
			return;
		}
		if (checkGiveupDistance(npcAI)) {
			npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
			return;
		}
		if (npcAI.isMoveSupported()) {
			/*
			 * FIXME: npc may not be able to move due to abnormal state; if that's the case, we should
			 * pick a new target if a hated entity is close enough to us to attack. Will have to add a special
			 * case in AI to check if it's using a skill or not; target may be out of skill range, but in auto
			 * attack range -- the AI should choose to auto attack until it is able to get in range to use the skill.
			 */
			npc.getMoveController().moveToTargetObject();
			return;
		}
		npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
	}
	
	private static boolean checkGiveupDistance(NpcAI2 npcAI) {
		Npc npc = npcAI.getOwner();
		// if target run away too far
		float distanceToTarget = npc.getDistanceToTarget();
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "AttackManager: distanceToTarget " + distanceToTarget);
		}
		// TODO may be ask AI too
		int chaseTarget = npc.isBoss() ? 50 : npc.getPosition().getWorldMapInstance().getTemplate().getAiInfo().getChaseTarget();
		if (distanceToTarget > chaseTarget) {
			return true;
		}
		
		double distanceToHome = npc.getDistanceToSpawnLocation();
		int chaseHome = npc.isBoss() ? 150 : npc.getPosition().getWorldMapInstance().getTemplate().getAiInfo().getChaseHome();
//		if (distanceToHome > chaseHome) { //Leashes like this don't exist on retail (with very few exceptions)
//			return true;
//		}
		// if npc is far away from home
		// start thinking about home after 100 meters and no attack for 10 seconds (only for default monsters)
		if (chaseHome <= 200 || distanceToHome > chaseHome) { // TODO: Check Client and use chase_user_by_trace value
			if ((npc.getGameStats().getLastAttackTimeDelta() > 10 && npc.getGameStats().getLastAttackedTimeDelta() > 10)
					/*|| (distanceToHome > chaseHome / 2 && npc.getGameStats().getLastAttackedTimeDelta() > 10)*/) {
				return true;
			}
		}
		return false;
	}
}

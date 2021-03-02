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
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * @author ATracer
 * @author GiGatR00n v4.7.5.x
 * @modified Yon (Aion Reconstruction Project) -- {@link #attackAction(NpcAI2)} changed to consider the target as too far away when
 * world geometry is in the way, deprecated handling that's being moved inside AI's, {@link #isTargetInAttackRange(Npc)} altered
 * to consider if the NPC is trying to use a skill or not (based on the AI's skillId).
 * @deprecated All functionality of this class has been moved into the AI classes.
 */
@Deprecated
public class SimpleAttackManager {
	
	/**
	 * @param npcAI
	 * @param delay
	 * @deprecated Attack handling has been moved into the AI itself.
	 */
	@Deprecated
	public static void performAttack(NpcAI2 npcAI, int delay) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "performAttack");
		}
		if (npcAI.getOwner().getGameStats().isNextAttackScheduled()) {
			if (npcAI.isLogging()) {
				AI2Logger.info(npcAI, "Attack already scheduled");
			}
			scheduleCheckedAttackAction(npcAI, delay); //TO-DO: Should this reset delay to 2000 if it's lower?
			return;
		}
		
		if (!isTargetInAttackRange(npcAI.getOwner())) {
			if (npcAI.isLogging()) {
				AI2Logger.info(npcAI, "Attack will not be scheduled because of range");
			}
			npcAI.onGeneralEvent(AIEventType.TARGET_TOOFAR);
			return;
		}
		npcAI.getOwner().getGameStats().setNextAttackTime(System.currentTimeMillis() + delay);
		if (delay > 0) {
			ThreadPoolManager.getInstance().schedule(new SimpleAttackAction(npcAI), delay);
		} else {
			attackAction(npcAI);
		}
	}
	
	/**
	 * @param npcAI
	 * @param delay
	 * @deprecated Attack handling has been moved into the AI itself.
	 */
	@Deprecated
	private static void scheduleCheckedAttackAction(NpcAI2 npcAI, int delay) {
		if (delay < 2000) {
			delay = 2000;
		}
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "Scheduling checked attack " + delay);
		}
		ThreadPoolManager.getInstance().schedule(new SimpleCheckedAttackAction(npcAI), delay);
	}
	
	/**
	 * @deprecated A more powerful method was added internally within AI's.
	 */
	@Deprecated
	public static boolean isTargetInAttackRange(Npc npc) {
		if (npc.getAi2().isLogging()) {
			float distance = npc.getDistanceToTarget();
			AI2Logger.info((AbstractAI) npc.getAi2(), "isTargetInAttackRange: " + distance);
		}
		if (npc.getTarget() == null || !(npc.getTarget() instanceof Creature)) {
			return false;
		}
		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(((AbstractAI) npc.getAi2()).getSkillId());
		if (template != null) {
			return MathUtil.isInAttackRange(npc, (Creature) npc.getTarget(), template.getProperties().getFirstTargetRange());
		} else return MathUtil.isInAttackRange(npc, (Creature) npc.getTarget(), npc.getGameStats().getAttackRange().getCurrent() / 1000f);
		// return distance <= npc.getController().getAttackDistanceToTarget() +
		// NpcMoveController.MOVE_CHECK_OFFSET;
		//TO-DO: Investigate commented code
	}
	
	/**
	 * @param npcAI
	 * @deprecated Attack handling has been moved into the AI itself.
	 */
	@Deprecated
	protected static void attackAction(final NpcAI2 npcAI) {
		if (!npcAI.isInState(AIState.FIGHT)) {
			return;
		}
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "attackAction");
		}
		Npc npc = npcAI.getOwner();
		Creature target = (Creature) npc.getTarget();
		if (target != null && !target.getLifeStats().isAlreadyDead()) {
			if (!npc.canSee(target)/* || !GeoService.getInstance().canSee(npc, target)*/) { //delete check geo when the Path Finding
				npc.getController().cancelCurrentSkill();
				npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
				return;
			}
			if (!GeoService.getInstance().canSee(npc, target)) {
				npcAI.onGeneralEvent(AIEventType.TARGET_TOOFAR);
				return;
			}
			if (isTargetInAttackRange(npc)) {
				npc.getController().attackTarget(target, 0);
				npcAI.onGeneralEvent(AIEventType.ATTACK_COMPLETE);
				return;
			}
			npcAI.onGeneralEvent(AIEventType.TARGET_TOOFAR);
		} else {
			npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
		}
	}
	
	/**
	 * @deprecated Attack handling has been moved into the AI itself.
	 */
	@Deprecated
	private final static class SimpleAttackAction implements Runnable {
		
		private NpcAI2 npcAI;
		
		SimpleAttackAction(NpcAI2 npcAI) {
			this.npcAI = npcAI;
		}
		
		@Override
		public void run() {
			attackAction(npcAI);
			npcAI = null;
		}
	}
	
	/**
	 * @deprecated Attack handling has been moved into the AI itself.
	 */
	@Deprecated
	private final static class SimpleCheckedAttackAction implements Runnable {
		
		private NpcAI2 npcAI;
		
		SimpleCheckedAttackAction(NpcAI2 npcAI) {
			this.npcAI = npcAI;
		}
		
		@Override
		public void run() {
			if (!npcAI.getOwner().getGameStats().isNextAttackScheduled()) {
				attackAction(npcAI);
			} else {
				if (npcAI.isLogging()) {
					AI2Logger.info(npcAI, "Scheduled checked attacked confirmed");
				}
			}
			npcAI = null;
		}
	}
}

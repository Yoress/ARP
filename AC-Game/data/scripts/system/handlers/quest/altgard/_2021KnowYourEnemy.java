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
package quest.altgard;

import java.util.concurrent.ScheduledFuture;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.zone.ZoneName;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * @author Mr. Poke
 * @modified Yon (Aion Reconstruction Project) -- Reworked to be less nerfed, added missing CS
 */
public class _2021KnowYourEnemy extends QuestHandler {
	
	private final static int questId = 2021;
	
	private final TIntObjectHashMap<ScheduledFuture<?>> observationTasks = new TIntObjectHashMap<ScheduledFuture<?>>(5);
	
	public _2021KnowYourEnemy() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(203669).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("BLACK_CLAW_OUTPOST_220030000"), questId);
		qe.registerOnLeaveZone(ZoneName.get("BLACK_CLAW_OUTPOST_220030000"), questId);
		qe.registerQuestNpc(700099).addOnKillEvent(questId);
		qe.registerQuestNpc(203557).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			return false;
		}
		
		final int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		
		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
			case 203669:
				switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 0) {
						return sendQuestDialog(env, 1011);
					} else if (var == 2) {
//						player.getEffectController().removeEffect(1868); //Moved to leave zone
						return sendQuestDialog(env, 1352);
					} else if (var == 6) {
						return sendQuestDialog(env, 1693);
					}
					break;
				case SELECT_ACTION_1013:
					if (var == 0) {
						playQuestMovie(env, 65);
					}
					return false;
				case SETPRO1:
					if (var == 0) {
						SkillEngine.getInstance().applyEffectDirectly(1868, player, player, 0);
						return defaultCloseDialog(env, 0, 1); // 1
					}
					break;
				case SETPRO2:
					return defaultCloseDialog(env, 2, 3); // 3
				case SETPRO3:
					return defaultCloseDialog(env, 6, 6, true, false); // reward
				}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203557) {
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 2034);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START) {
			return false;
		}
		
		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		
		if (targetId == 700099 && var >= 3 && var < 6) {
			qs.setQuestVarById(0, var + 1);
			updateQuestStatus(env);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onEnterZoneEvent(final QuestEnv env, ZoneName zoneName) {
		if (zoneName != ZoneName.get("BLACK_CLAW_OUTPOST_220030000")) {
			return false;
		}
		final Player player = env.getPlayer();
		if (player == null) {
			return false;
		}
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			return false;
		}
		if (qs.getQuestVarById(0) == 1) {
			final int playerObjectId = player.getObjectId();
			observationTasks.put(playerObjectId, ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					if (player == null || !player.isOnline() || player.getWorldId() != 220030000
					|| qs == null || qs.getStatus() != QuestStatus.START || qs.getQuestVarById(0) != 1) {
						ScheduledFuture<?> us = observationTasks.remove(playerObjectId);
						if (us != null) us.cancel(false);
					} else {
						if (MathUtil.isNearCoordinates(player, 1973, 2412, 322, 50)) {
							qs.setQuestVarById(0, 2);
							updateQuestStatus(env);
							ScheduledFuture<?> us = observationTasks.remove(playerObjectId);
							if (us != null) us.cancel(false);
						}
					}
				}
			}, 1000, 1000));
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onLeaveZoneEvent(QuestEnv env, ZoneName zoneName) {
		if (zoneName != ZoneName.get("BLACK_CLAW_OUTPOST_220030000")) {
			return false;
		}
		final Player player = env.getPlayer();
		if (player == null) return false;
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) return false;
		ScheduledFuture<?> observeTask = observationTasks.remove(player.getObjectId());
		if (observeTask != null) observeTask.cancel(false);
		if (qs.getQuestVarById(0) == 2) {
			player.getEffectController().removeEffect(1868);
		}
		return false;
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2200, true);
	}
}

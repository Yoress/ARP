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

import java.util.concurrent.Future;

import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnSearchResult;
import com.aionemu.gameserver.model.templates.spawns.SpawnSpotTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NPC_INFO;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Escort Groken (203608) to the sailboat (700178). Talk with Manir (203607).
 *
 * @author Mr. Poke
 * @reworked vlog
 * @reworked Yon (Aion Reconstruction Project)
 * @modified Yon (Aion Reconstruction Project) -- No longer misuses the sendQuestSelectionDialog(QuestEnv) method.
 */
public class _2290GrokensEscape extends QuestHandler {
	
	private final static int questId = 2290;
	
	public _2290GrokensEscape() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(203608).addOnQuestStart(questId);
		qe.registerOnLogOut(questId);
		qe.registerQuestNpc(203608).addOnTalkEvent(questId);
		qe.registerQuestNpc(700178).addOnTalkEvent(questId);
		qe.registerQuestNpc(203607).addOnTalkEvent(questId);
		qe.registerAddOnLostTargetEvent(questId);
	}
	
	private Npc groken; //FIXME: This approach doesn't work when there is more than one channel!
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203608) { // Groken
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1011);
				}
				if (env.getDialogId() == DialogAction.ASK_QUEST_ACCEPT.id()) {
					return sendQuestDialog(env, 4);
				}
				if (env.getDialogId() == DialogAction.QUEST_ACCEPT_1.id()) {
					return sendQuestDialog(env, 1003);
				}
				if (env.getDialogId() == DialogAction.QUEST_REFUSE_1.id()) {
					return sendQuestDialog(env, 1004);
				}
				if (env.getDialogId() == DialogAction.FINISH_DIALOG.id()) {
					//It shouldn't be possible to trigger this case, but if we do the dialog should close, not reset to quest selection.
//					return sendQuestSelectionDialog(env);
					return closeDialogWindow(env);
				}
				if (env.getDialogId() == DialogAction.SELECT_ACTION_1012.id()) {
					if (QuestService.startQuest(env)) {
						groken = (Npc) env.getVisibleObject();
						return startFollowEvent(env, groken, 700178, 0, 1); // 1
					}
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 203608) { // Groken
				if (env.getDialog() == DialogAction.QUEST_SELECT && qs.getQuestVarById(0) == 0) {
					groken = (Npc) env.getVisibleObject();
					return startFollowEvent(env, groken, 700178, 0, 1); // 1
				}
			} else if (targetId == 203607) { // Manir
				if (env.getDialog() == DialogAction.QUEST_SELECT && qs.getQuestVarById(0) == 3) {
					return sendQuestDialog(env, 1693);
				} else if (env.getDialog() == DialogAction.SELECT_QUEST_REWARD) {
					return defaultCloseDialog(env, 3, 3, true, true);
				}
			} else if (targetId == 700178) {
				if (qs.getQuestVarById(0) == 1) {
					player.getController().cancelTask(TaskId.QUEST_FOLLOW);
					groken.getAi2().onCreatureEvent(AIEventType.STOP_FOLLOW_ME, player);
					if (!groken.getAi2().getName().equals("following")) {
						groken.getController().onDelete();
					}
					changeQuestStep(env, 1, 3, false);
					playQuestMovie(env, 69);
				}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203607) { // Manir
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
	
	private boolean startFollowEvent(QuestEnv env, Npc follower, int targetNpcId, int step, int nextStep) {
		final Player player = env.getPlayer();
		if (!(env.getVisibleObject() instanceof Npc)) {
			return false;
		}
		follower.getAi2().onCreatureEvent(AIEventType.FOLLOW_ME, player);
		player.getController().addTask(TaskId.QUEST_FOLLOW, followTask(env, follower, targetNpcId));
		PacketSendUtility.sendPacket(player, new SM_NPC_INFO(follower, player));
		if (step == 0 && nextStep == 0) {
			return true;
		} else {
			return defaultCloseDialog(env, step, nextStep);
		}
	}
	
	@Override
	public boolean onLogOutEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 1) {
				changeQuestStep(env, 1, 0, false);
			}
		}
		return false;
	}
	
	@Override
	public boolean onNpcLostTargetEvent(QuestEnv env) {
		return defaultFollowEndEvent(env, 1, 0, false); // 0
	}
	
	private Future<?> followTask(QuestEnv env, Npc follower, int targetID) {
		return ThreadPoolManager.getInstance().scheduleAtFixedRate(new FollowingNpc(env, follower, targetID), 1000, 1000);
	}
	
	/**
	 * Based on com.aionemu.gameserver.questEngine.task.FollowingNpcCheckTask
	 * 
	 * @author ATracer
	 * @reworked Yon (Aion Reconstruction Project)
	 */
	public class FollowingNpc implements Runnable {
		
		private final QuestEnv env;
		private final Npc follower;
		private SpawnSearchResult expectedTarget;
		private final long startTime = System.currentTimeMillis();
		
		FollowingNpc(QuestEnv env, Npc follower, int targetID) {
			this.env = env;
			this.follower = follower;
			expectedTarget = DataManager.SPAWNS_DATA2.getFirstSpawnByNpcId(follower.getWorldId(), targetID);
			if (expectedTarget == null) {
				throw new IllegalArgumentException("Supplied npc doesn't exist: " + targetID);
			}
		}
		
		@Override
		public void run() {
			final Player player = env.getPlayer();
			Npc npc = follower;
			if (player.getLifeStats().isAlreadyDead() || npc.getLifeStats().isAlreadyDead()) {
				onFail(env);
			}
			if (!MathUtil.isIn3dRange(player, npc, 50)) {
				onFail(env);
			}
			if (!isIn3dRangeOfTarget(npc, expectedTarget, 200)) {
				onFail(env);
			}
			// Basically a 5 minute timer to get the NPC to the destination.
			if (System.currentTimeMillis() - startTime >= 300000) onFail(env);
		}
		
		private boolean isIn3dRangeOfTarget(Npc follower, SpawnSearchResult target, float range) {
			if (follower.getWorldId() != target.getWorldId()) {
				return false;
			}
			SpawnSpotTemplate targetSpot = target.getSpot();
			float dx = (targetSpot.getX() - follower.getX());
			float dy = (targetSpot.getY() - follower.getY());
			float dz = (targetSpot.getZ() - follower.getZ());
			return dx * dx + dy * dy + dz * dz < range * range;
		}
		
		/**
		 * Following task failed, abort further progress
		 */
		protected void onFail(QuestEnv env) {
			stopFollowing(env);
			QuestEngine.getInstance().onNpcLostTarget(env);
		}
		
		private final void stopFollowing(QuestEnv env) {
			Player player = env.getPlayer();
			Npc npc = follower;
			player.getController().cancelTask(TaskId.QUEST_FOLLOW);
			npc.getAi2().onCreatureEvent(AIEventType.STOP_FOLLOW_ME, player);
			if (!npc.getAi2().getName().equals("following")) {
				npc.getController().onDelete();
			}
		}
	}
}

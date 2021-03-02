/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 * Aion-Lightning is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your option) any later version.
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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.CreatureType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author Ritsu
 * @Reworked Majka Ajural
 * @modified Yon (Aion Reconstruction Project) -- Removed QuestBusy support, allowed for the quest to be completed.
 */
public class _2252ChasingtheLegend extends QuestHandler {
	
	private final static int questId = 2252;
	private final static int questStartNpcId = 203646; // Sinood
	private final static int questStep1NpcId = 700060; // Bones of Munishan (Npc)
	private final static int questActionItemId = 182203235; // Bones of Munishan (Item)
	private final static int questKillNpc1Id = 210634; // Minushan's Spirit
	private final static int questKillNpc2Id = 210635; // Minushan Drakie. To check on retail if it is spawned also 210635
	
	public _2252ChasingtheLegend() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(questStartNpcId).addOnQuestStart(questId); // Sinood
		qe.registerQuestNpc(questStartNpcId).addOnTalkEvent(questId);
		qe.registerQuestNpc(questStep1NpcId).addOnTalkEvent(questId); // Bone of Minusha
		qe.registerQuestNpc(questKillNpc1Id).addOnKillEvent(questId); // Minushan's Spirit
		qe.registerQuestNpc(questKillNpc2Id).addOnKillEvent(questId); // Minushan Drakie
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == questStartNpcId) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1011);
				} else if (dialog == DialogAction.QUEST_ACCEPT_1) {
					if (QuestService.startQuest(env)) {
						giveQuestItem(env, questActionItemId, 1);
						return sendQuestDialog(env, 1003);
					}
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == questStartNpcId) {
				switch (dialog) {
				case QUEST_SELECT:
					if (var == 0) {
						long MinushanBone = player.getInventory().getItemCountByItemId(questActionItemId);
						if (MinushanBone == 0) { // Player hasn't action item; dialogue for a new chance
							giveQuestItem(env, questActionItemId, 1);
							qs.setQuestVarById(0, 0);
							return sendQuestDialog(env, 1693);
						} else { // Dialogue for encouragement
							return sendQuestDialog(env, 2034);
						}
					}
				}
			}
			if (targetId == questStep1NpcId) {
				switch (dialog) {
				case USE_OBJECT:
					final Npc npc = (Npc) player.getTarget();
					
					if (npc == null/* || npc.getIsQuestBusy()*/) {
						return false;
					}
					
					if (var == 0 && checkItemExistence(env, questActionItemId, 1, true)) {
//						npc.setIsQuestBusy(true); // Set npc not usable during spawn time
						
						// Random spawn
						int chance = 95; // Chance to spawn biggest reward mob
						int spawnTime = 3; // 3 min of spawn
						int questSpawnedNpcId = (Rnd.get(1, 100) <= chance ? questKillNpc1Id : questKillNpc2Id);
						// Minushan's Spirit or Minushan's Drakie
						final Npc questMob = (Npc) QuestService.spawnQuestNpc(player.getWorldId(), player.getInstanceId(), questSpawnedNpcId, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
						NpcShoutsService.getInstance().sendMsg(questMob, 1100630, questMob.getObjectId(), 0, 0);
						
						//On retail, the bones despawned, I think? ... I'll try the icon thing
//						npc.getController().onDieSilence();
						npc.setType(CreatureType.PEACE);
						final ScheduledFuture<?> despawnTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								if (!questMob.getLifeStats().isAlreadyDead()) {
									questMob.getController().onDelete();
								}
								npc.setType(CreatureType.NULL);
							}
						}, spawnTime * 60000);
						
						questMob.getObserveController().addObserver(new ActionObserver(ObserverType.DEATH) {
							@Override
							public void died(Creature creature) {
								despawnTask.cancel(false);
								npc.setType(CreatureType.NULL);
							}
						});
						
						//TO-DO: setting not usable icon while mob is spawned
						//FIX-ME: This shit doesn't do anything other than keep a reference to the Npc; remove it.
//						ThreadPoolManager.getInstance().schedule(new Runnable() {
//							
//							@Override
//							public void run() {
//								npc.setIsQuestBusy(false);
//								questMob.getController().delete();
//								//TO-DO: setting usable icon after mob is despawned
//							}
//						}, spawnTime * 60000);
					}
				}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			int var = qs.getQuestVarById(0);
			switch (dialog) {
				case USE_OBJECT:
					return sendQuestDialog(env, 1352);
				default:
					int reward = var - 1;
					return sendQuestEndDialog(env, reward);
			}
			//FIX-ME: Cannot complete quest with this junk.
//			switch (dialog) {
//			case SELECT_QUEST_REWARD:
//				int reward = var - 1;
//				return sendQuestEndDialog(env, reward);
//			default:
//				return sendQuestDialog(env, 1352);
//			}
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
		if (var == 0) {
			int targetId = env.getTargetId();
			switch (targetId) {
			case questKillNpc1Id: // Minushan's Spirit - Highest reward
				qs.setStatus(QuestStatus.REWARD);
				qs.setQuestVarById(0, 1);
				updateQuestStatus(env);
				return true;
			case questKillNpc2Id: // Minushan Drakie - Lowest reward
				qs.setStatus(QuestStatus.REWARD);
				qs.setQuestVarById(0, 2);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}
}

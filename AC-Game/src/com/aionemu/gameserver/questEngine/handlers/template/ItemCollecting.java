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
package com.aionemu.gameserver.questEngine.handlers.template;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.quest.XMLStartCondition;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author MrPoke
 * @reworked vlog, Rolandas
 * @modified Yon (Aion Reconstruction Project) -- tweaked {@link #onDialogEvent(QuestEnv)} to not send quest dialogue for
 * SELECT_ACTION_1012 when the quest state is null or complete, added a case for USE_OBJECT, corrected misuse of
 * {@link #sendQuestSelectionDialog(QuestEnv)} in {@link #onDialogEvent(QuestEnv)}.
 */
public class ItemCollecting extends QuestHandler {
	
	private final Set<Integer> startNpcs = new HashSet<Integer>();
	private final Set<Integer> actionItems = new HashSet<Integer>();
	private final Set<Integer> endNpcs = new HashSet<Integer>();
	private final int questMovie;
	private final int nextNpcId;
	private final int startDialogId;
	private final int startDialogId2;
	private final int itemId;
	
	public ItemCollecting(int questId, List<Integer> startNpcIds, int nextNpcId, List<Integer> actionItemIds, List<Integer> endNpcIds, int questMovie, int startDialogId, int startDialogId2, int itemId) {
		super(questId);
		startNpcs.addAll(startNpcIds);
		startNpcs.remove(0);
		this.nextNpcId = nextNpcId;
		if (actionItemIds != null) {
			actionItems.addAll(actionItemIds);
			actionItems.remove(0);
		}
		if (endNpcIds == null) {
			endNpcs.addAll(startNpcs);
		} else {
			endNpcs.addAll(endNpcIds);
			endNpcs.remove(0);
		}
		this.questMovie = questMovie;
		this.startDialogId = startDialogId;
		this.startDialogId2 = startDialogId2;
		this.itemId = itemId;
	}
	
	@Override
	public void register() {
		Iterator<Integer> iterator = startNpcs.iterator();
		while (iterator.hasNext()) {
			int startNpc = iterator.next();
			qe.registerQuestNpc(startNpc).addOnQuestStart(getQuestId());
			qe.registerQuestNpc(startNpc).addOnTalkEvent(getQuestId());
		}
		if (nextNpcId != 0) {
			qe.registerQuestNpc(nextNpcId).addOnTalkEvent(getQuestId());
		}
		iterator = actionItems.iterator();
		while (iterator.hasNext()) {
			int actionItem = iterator.next();
			qe.registerQuestNpc(actionItem).addOnTalkEvent(getQuestId());
			qe.registerCanAct(getQuestId(), actionItem);
		}
		
		iterator = endNpcs.iterator();
		while (iterator.hasNext()) {
			int endNpc = iterator.next();
			qe.registerQuestNpc(endNpc).addOnTalkEvent(getQuestId());
		}
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(getQuestId());
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (startNpcs.isEmpty() || startNpcs.contains(targetId)) {
				switch (dialog) {
				case USE_OBJECT: {
					List<XMLStartCondition> conditions = DataManager.QUEST_DATA.getQuestById(getQuestId()).getXMLStartConditions();
					int completedConditionCount = 0;
					for (XMLStartCondition cond: conditions) {
						if (cond.check(player, false)) {
							completedConditionCount++;
						}
					}
					if (qs == null && completedConditionCount != conditions.size()) return false;
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				case QUEST_SELECT: {
					return sendQuestDialog(env, startDialogId != 0 ? startDialogId : 1011);
				}
				case SETPRO1: {
					QuestService.startQuest(env);
					return closeDialogWindow(env);
				}
				case SELECT_ACTION_1012: { //TODO: Why is this here? What Quest is it for?
					if (qs == null || qs.getStatus() == QuestStatus.COMPLETE) return false; //TODO: Might still be buggy
					if (questMovie != 0) {
						playQuestMovie(env, questMovie);
					}
					return sendQuestDialog(env, 1012);
				}
				default: {
					if (itemId != 0) {
						giveQuestItem(env, itemId, 1);
					}
					return sendQuestStartDialog(env);
				}
				}
			}
		} else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == nextNpcId && var == 0) {
				switch (dialog) {
				case QUEST_SELECT: {
					return sendQuestDialog(env, 1352);
				}
				case SETPRO1: {
					return defaultCloseDialog(env, 0, 1);
				}
				}
			} else if (endNpcs.contains(targetId)) {
				switch (dialog) {
				case QUEST_SELECT: {
					return sendQuestDialog(env, startDialogId2 != 0 ? startDialogId2 : 2375);
				}
				case CHECK_USER_HAS_QUEST_ITEM: {
					//TODO: If any quest other than 4912 needs to send this dialog ID, then it should be handled in data.
					return checkQuestItems(env, var, var, true, 5, (questId == 4912 ? 10001 : 2716));// 1004); // reward
				}
				case CHECK_USER_HAS_QUEST_ITEM_SIMPLE: {
					return checkQuestItemsSimple(env, var, var, true, 5, 0, 0); // reward
				}
				case FINISH_DIALOG: {
					//Should not call this here; we should close the dialog, not open the quest selection page.
					//As a side note, I don't think it's possible to trigger this case because the client sends CM_CLOSE_DIALOG
					//when this dialog action is selected.
//					return sendQuestSelectionDialog(env);
					return defaultCloseDialog(env, var, var);
				}
				case SET_SUCCEED: {
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return closeDialogWindow(env);
				}
				case SETPRO1: {
					return checkQuestItemsSimple(env, var, var, true, 5, 0, 0);
				}
				case SETPRO2: {
					return checkQuestItemsSimple(env, var, var, true, 6, 0, 0);
				}
				case SETPRO3: {
					return checkQuestItemsSimple(env, var, var, true, 7, 0, 0);
				}
				}
			} else if (targetId != 0 && actionItems.contains(targetId)) {
				return true; // looting
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (endNpcs.contains(targetId)) {
				if (itemId != 0) {
					removeQuestItem(env, itemId, 1);
				}
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}

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
package quest.danaria;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * @author Romanz
 *
 */
public class _23060PlausibleDeniablity extends QuestHandler {
	
	private final static int questId = 23060;
	
	public _23060PlausibleDeniablity() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestItem(182213453, questId);
		qe.registerQuestNpc(801104).addOnTalkEvent(questId);
		qe.registerQuestNpc(801131).addOnTalkEvent(questId);
		qe.registerQuestNpc(800936).addOnTalkEvent(questId);
		qe.registerQuestNpc(800937).addOnTalkEvent(questId);
		qe.registerQuestNpc(800938).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 0) {
				if (dialog == DialogAction.QUEST_ACCEPT_1) {
					QuestService.startQuest(env);
					return closeDialogWindow(env);
				}
			}
		} else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 801104) {
				switch (dialog) {
				case QUEST_SELECT: {
					if (qs.getQuestVarById(0) == 0) {
						return sendQuestDialog(env, 1352);
					} else if (qs.getQuestVarById(0) == 2) {
						return sendQuestDialog(env, 2034);
					}
				}
				case SETPRO1: {
					removeQuestItem(env, 182213453, 1);
					return defaultCloseDialog(env, 0, 1);
				}
				case SETPRO3: {
					return defaultCloseDialog(env, 2, 3);
				}
				}
			}
			
			else if (targetId == 801131) {
				switch (dialog) {
				case QUEST_SELECT: {
					return sendQuestDialog(env, 1693);
				}
				case SETPRO2: {
					return defaultCloseDialog(env, 1, 2);
				}
				}
			} else if (targetId == 800936 || targetId == 800937 || targetId == 800938) {
				switch (dialog) {
				case QUEST_SELECT: {
					return sendQuestDialog(env, 2375);
				}
				case SELECT_QUEST_REWARD: {
					changeQuestStep(env, 3, 3, true); // reward
					return sendQuestDialog(env, 5);
				}
				}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 800936 || targetId == 800937 || targetId == 800938) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			return HandlerResult.fromBoolean(sendQuestDialog(env, 4));
		}
		return HandlerResult.FAILED;
	}
}

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
package quest.katalam;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Romanz
 *
 */
public class _12565ForgottenKnowledge extends QuestHandler {
	
	private final static int questId = 12565;
	
	public _12565ForgottenKnowledge() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(801019).addOnQuestStart(questId);
		qe.registerQuestNpc(801019).addOnTalkEvent(questId);
		qe.registerQuestNpc(801016).addOnTalkEvent(questId);
		qe.registerQuestNpc(730784).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		DialogAction dialog = env.getDialog();
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 801019) {
				switch (dialog) {
				case QUEST_SELECT: {
					return sendQuestDialog(env, 1011);
				}
				case QUEST_ACCEPT_SIMPLE: {
					return sendQuestStartDialog(env);
				}
				}
			}
		} else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 801016) {
				switch (dialog) {
				case QUEST_SELECT: {
					if (qs.getQuestVarById(0) == 0) {
						return sendQuestDialog(env, 1352);
					} else if (qs.getQuestVarById(0) == 2) {
						return sendQuestDialog(env, 2375);
					}
				}
				case SETPRO1: {
					return defaultCloseDialog(env, 0, 1);
				}
				case SELECT_QUEST_REWARD: {
					changeQuestStep(env, 2, 3, true); // reward
					return sendQuestDialog(env, 5);
				}
				}
			} else if (targetId == 730784) {
				switch (dialog) {
				case QUEST_SELECT: {
					if (qs.getQuestVarById(0) == 1) {
						return sendQuestDialog(env, 1693);
					}
				}
				case SETPRO2: {
					return defaultCloseDialog(env, 1, 2);
				}
				}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 801016) {
				return sendQuestEndDialog(env);
				
			}
		}
		return false;
	}
}

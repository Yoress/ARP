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
package quest.miragent_holy_templar;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Nanou
 * @modified Gigi
 */
public class _3936DecorationsOfSanctum extends QuestHandler {
	
	private final static int questId = 3936;
	
	public _3936DecorationsOfSanctum() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(203710).addOnQuestStart(questId);// Dairos
		qe.registerQuestNpc(203710).addOnTalkEvent(questId);// Dairos
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();
		
		// Start to Dairos
		if (qs == null || (qs.getStatus() == QuestStatus.NONE || qs.getStatus() == QuestStatus.COMPLETE)) {
			if (targetId == 203710) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		}
		
		if (qs == null) {
			return false;
		}
		
		int var = qs.getQuestVarById(0);
		
		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
			// 1 - Report the result to Dairos.
			case 203710:
				switch (dialog) {
				case QUEST_SELECT:
					if (var == 0) {
						return sendQuestDialog(env, 1011);
					} else if (var == 1) {
						return sendQuestDialog(env, 1352);
					}
				case SETPRO1:
					return defaultCloseDialog(env, 0, 1); // 1
				case CHECK_USER_HAS_QUEST_ITEM:
					long itemCount1 = player.getInventory().getItemCountByItemId(182206091);
					long itemCount2 = player.getInventory().getItemCountByItemId(182206092);
					long itemCount3 = player.getInventory().getItemCountByItemId(182206093);
					long itemCount4 = player.getInventory().getItemCountByItemId(182206094);
					if (itemCount1 >= 10 && itemCount2 >= 10 && itemCount3 >= 10 && itemCount4 >= 10) {
						removeQuestItem(env, 182206091, 10);
						removeQuestItem(env, 182206092, 10);
						removeQuestItem(env, 182206093, 10);
						removeQuestItem(env, 182206094, 10);
						changeQuestStep(env, 1, 1, true);
						return sendQuestDialog(env, 5);
					} else {
						return sendQuestDialog(env, 10001);
					}
				}
				break;
			// No match
			default:
				return sendQuestStartDialog(env);
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203710) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}

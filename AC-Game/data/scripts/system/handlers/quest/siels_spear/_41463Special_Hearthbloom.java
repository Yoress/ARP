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
package quest.siels_spear;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Cheatkiller
 *
 */
public class _41463Special_Hearthbloom extends QuestHandler {
	
	private final static int questId = 41463;
	
	public _41463Special_Hearthbloom() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(205579).addOnQuestStart(questId);
		qe.registerQuestNpc(205579).addOnTalkEvent(questId);
		qe.registerQuestNpc(205580).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		DialogAction dialog = env.getDialog();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 205579) {
				switch (env.getDialog()) {
				case QUEST_SELECT:
					return sendQuestDialog(env, 1011);
				case QUEST_ACCEPT_SIMPLE:
					return sendQuestStartDialog(env);
				case QUEST_REFUSE_SIMPLE:
					return sendQuestEndDialog(env);
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
			case 205580: {
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 0) {
						return sendQuestDialog(env, 1352);
					} else if (player.getInventory().getItemCountByItemId(182213224) >= 7) {
						return sendQuestDialog(env, 2375);
					} else {
						return closeDialogWindow(env);
					}
				}
				case CHECK_USER_HAS_QUEST_ITEM_SIMPLE: {
					removeQuestItem(env, 182213224, 7);
					qs.setQuestVar(2);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return sendQuestDialog(env, 5);
				}
				case SETPRO1: {
					if (giveQuestItem(env, 170190066, 1)) {
						return defaultCloseDialog(env, 0, 1);
					}
				}
				}
				break;
			}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 205580) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}

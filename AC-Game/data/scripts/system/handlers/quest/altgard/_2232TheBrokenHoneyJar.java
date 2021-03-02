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

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Mr. Poke fix by Nephis and quest helper team.
 * @reworked vlog
 * @modified Yon (Aion Reconstruction Project) -- No longer misuses the sendQuestSelectionDialog(QuestEnv) method.
 */
public class _2232TheBrokenHoneyJar extends QuestHandler {
	
	private final static int questId = 2232;
	
	public _2232TheBrokenHoneyJar() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(203613).addOnQuestStart(questId);
		qe.registerQuestNpc(203613).addOnTalkEvent(questId);
		qe.registerQuestNpc(203622).addOnTalkEvent(questId);
		qe.registerQuestNpc(700061).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203613) { // Gilungk
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1011);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 203613) { // Gilungk
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 1) {
						return sendQuestDialog(env, 2375);
					}
				}
				case CHECK_USER_HAS_QUEST_ITEM: {
					return checkQuestItems(env, 1, 1, true, 5, 2716); // reward
				}
				case FINISH_DIALOG: {
					//It shouldn't be possible to trigger this case, but if we do the dialog should close, not reset to quest selection.
//					return sendQuestSelectionDialog(env);
					return defaultCloseDialog(env, var, var);
				}
				}
			} else if (targetId == 700061) { // Beehive
				if (dialog == DialogAction.USE_OBJECT) {
					return true; // loot
				}
			} else if (targetId == 203622) { // Tatural
				if (dialog == DialogAction.QUEST_SELECT) {
					if (var == 0) {
						return sendQuestDialog(env, 1352);
					}
				} else if (env.getDialog() == DialogAction.SETPRO1) {
					return defaultCloseDialog(env, 0, 1); // 1
				}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203613) { // Gilungk
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}

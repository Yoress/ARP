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
package quest.reshanta;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author vlog TODO: siege weapons attacking event, when siege weapons done
 */
public class _4702GeneralDeath extends QuestHandler {
	
	private static final int questId = 4702;
	
	public _4702GeneralDeath() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(278016).addOnQuestStart(questId);
		qe.registerQuestNpc(278016).addOnTalkEvent(questId);
		qe.registerQuestNpc(256694).addOnKillEvent(questId);
		qe.registerQuestNpc(256693).addOnKillEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 278016) { // Lisya
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				} else {
					return sendQuestStartDialog(env, 182205676, 1);
				}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 278016) { // Lisya
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
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
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 0) {
				return defaultOnKillEvent(env, 256694, 0, 1); // 1
			} else if (var == 1) {
				return defaultOnKillEvent(env, 256693, 1, true); // reward
			}
		}
		return false;
	}
}

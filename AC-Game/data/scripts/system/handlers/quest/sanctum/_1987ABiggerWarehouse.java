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
package quest.sanctum;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author vlog
 */
public class _1987ABiggerWarehouse extends QuestHandler {
	
	private static final int questId = 1987;
	
	public _1987ABiggerWarehouse() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(203700).addOnQuestStart(questId);
		qe.registerQuestNpc(203700).addOnTalkEvent(questId);
		qe.registerQuestNpc(203749).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();
		
		if (qs == null) {
			if (targetId == 203700) { // Fasimedes
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 203749) { // Bustant
				switch (dialog) {
				case QUEST_SELECT: {
					return sendQuestDialog(env, 2375);
				}
				case SELECT_QUEST_REWARD: {
					changeQuestStep(env, 0, 0, true); // reward
					return sendQuestDialog(env, 5);
				}
				}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203749) { // Bustant
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}

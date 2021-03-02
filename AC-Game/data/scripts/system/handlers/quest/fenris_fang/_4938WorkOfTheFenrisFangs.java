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
package quest.fenris_fang;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Nanou
 * @reworked vlog
 * @modified Yon (Aion Reconstruction Project) -- No longer misuses the sendQuestSelectionDialog(QuestEnv) method.
 */
public class _4938WorkOfTheFenrisFangs extends QuestHandler {
	
	private final static int questId = 4938;
	
	public _4938WorkOfTheFenrisFangs() {
		super(questId);
	}
	
	@Override
	public void register() {
		int[] npcs = {204053, 798367, 798368, 798369, 798370, 798371, 798372, 798373, 798374, 204075};
		qe.registerQuestNpc(204053).addOnQuestStart(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204053) { // Kvasir
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
			case 798367: { // Riikaard
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 0) {
						return sendQuestDialog(env, 1011);
					}
				}
				case SETPRO1: {
					return defaultCloseDialog(env, 0, 1); // 1
				}
				}
				break;
			}
			case 798368: { // Herosir
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 1) {
						return sendQuestDialog(env, 1352);
					}
				}
				case SETPRO2: {
					return defaultCloseDialog(env, 1, 2); // 2
				}
				}
				break;
			}
			case 798369: { // Gellner
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 2) {
						return sendQuestDialog(env, 1693);
					}
				}
				case SETPRO3: {
					return defaultCloseDialog(env, 2, 3); // 3
				}
				}
				break;
			}
			case 798370: { // Natorp
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 3) {
						return sendQuestDialog(env, 2034);
					}
				}
				case SETPRO4: {
					return defaultCloseDialog(env, 3, 4); // 4
				}
				}
				break;
			}
			case 798371: { // Needham
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 4) {
						return sendQuestDialog(env, 2375);
					}
				}
				case SETPRO5: {
					return defaultCloseDialog(env, 4, 5); // 5
				}
				}
				break;
			}
			case 798372: { // Landsberg
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 5) {
						return sendQuestDialog(env, 2716);
					}
				}
				case SETPRO6: {
					return defaultCloseDialog(env, 5, 6); // 6
				}
				}
				break;
			}
			case 798373: { // Levinard
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 6) {
						return sendQuestDialog(env, 3057);
					}
				}
				case SETPRO7: {
					return defaultCloseDialog(env, 6, 7); // 7
				}
				}
				break;
			}
			case 798374: { // Lonergan
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 7) {
						return sendQuestDialog(env, 3398);
					}
				}
				case SETPRO8: {
					return defaultCloseDialog(env, 7, 8); // 8
				}
				}
				break;
			}
			case 204075: { // Balder
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 8) {
						return sendQuestDialog(env, 3739);
					}
				}
				case SET_SUCCEED: {
					if (player.getInventory().getItemCountByItemId(186000084) >= 1) {
						removeQuestItem(env, 186000084, 1);
						return defaultCloseDialog(env, 8, 8, true, false, 0);
					} else {
						return sendQuestDialog(env, 3825);
					}
				}
				case FINISH_DIALOG: {
					//It shouldn't be possible to trigger this case, but if we do the dialog should close, not reset to quest selection.
//					return sendQuestSelectionDialog(env);
					return defaultCloseDialog(env, var, var);
				}
				}
				break;
			}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204053) { // Kvasir
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}

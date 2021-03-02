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
package quest.verteron;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * @author Rhys2002, Nephis
 * @reworked vlog
 * @modified Yon (Aion Reconstruction Project) -- No longer misuses the sendQuestSelectionDialog(QuestEnv) method.
 */
public class _1016SourceOfThePollution extends QuestHandler {
	
	private final static int questId = 1016;
	
	public _1016SourceOfThePollution() {
		super(questId);
	}
	
	@Override
	public void register() {
		int[] npcs = {203149, 203148, 203832, 203705, 203822, 203761, 203098, 203195};
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(210318).addOnKillEvent(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			return false;
		}
		DialogAction dialog = env.getDialog();
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		
		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
			case 203149: { // Geolus
				switch (dialog) {
				case QUEST_SELECT: {
					switch (var) {
					case 0: {
						return sendQuestDialog(env, 1011);
					}
					case 2: {
						return sendQuestDialog(env, 1693);
					}
					case 7: {
						return sendQuestDialog(env, 3398);
					}
					case 8: {
						if (player.getInventory().getItemCountByItemId(182200015) < 2) {
							return sendQuestDialog(env, 3484);
						} else {
							return sendQuestDialog(env, 3569);
						}
					}
					}
				}
				case SELECT_ACTION_3400: {
					playQuestMovie(env, 28);
					return sendQuestDialog(env, 3400);
				}
				case SETPRO1: {
					return defaultCloseDialog(env, 0, 1); // 1
				}
				case SETPRO3: {
					return defaultCloseDialog(env, 2, 3); // 3
				}
				case SETPRO8: {
					if (var == 7) {
						removeQuestItem(env, 182200013, 1);
						removeQuestItem(env, 182200014, 1);
						return defaultCloseDialog(env, 7, 8, 182200015, 2, 0, 0); // 8
					} else if (var == 8) {
						return defaultCloseDialog(env, 8, 8, 182200015, 2, 0, 0); // 8
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
			case 203148: { // Lepios
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 1) {
						return sendQuestDialog(env, 1352);
					}
				}
				case SETPRO2: {
					return defaultCloseDialog(env, 1, 2, 182200017, 1, 0, 0); // 2
				}
				}
				break;
			}
			case 203832: { // Dimos
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 3) {
						return sendQuestDialog(env, 2034);
					}
				}
				case SETPRO4: {
					return defaultCloseDialog(env, 3, 4, 182200013, 1, 0, 0); // 4
				}
				}
				break;
			}
			case 203705: { // Jumentis
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
			case 203822: { // Quintus
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 5) {
						return sendQuestDialog(env, 2716);
					}
				}
				case SETPRO6: {
					return defaultCloseDialog(env, 5, 6, 182200018, 1, 182200017, 1); // 6
				}
				}
				break;
			}
			case 203761: { // Hygea
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 6) {
						return sendQuestDialog(env, 3057);
					}
				}
				case SETPRO7: {
					return defaultCloseDialog(env, 6, 7, 182200014, 1, 182200018, 1); // 7
				}
				}
				break;
			}
			case 203195: { // Kato
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 9) {
						return sendQuestDialog(env, 3739);
					}
				}
				case SETPRO9: {
					if (var == 9) {
						if (!giveQuestItem(env, 182200016, 1)) {
							return closeDialogWindow(env);
						}
						Npc npc = (Npc) env.getVisibleObject();
						changeQuestStep(env, 9, 9, true); // reward
						removeQuestItem(env, 182200015, 2);
						npc.getController().onDelete();
						return closeDialogWindow(env);
					}
				}
				}
			}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203098) { // Spatalos
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					removeQuestItem(env, 182200016, 1);
					return sendQuestDialog(env, 4080);
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
			if (var == 8) {
				int targetId = env.getTargetId();
				if (targetId == 210318) {
					QuestService.addNewSpawn(210030000, player.getInstanceId(), 203195, player.getX() + 2, player.getY() + 2, player.getZ() + 1, (byte) 0);
					return defaultOnKillEvent(env, 210318, 8, 9); // 9
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1130, true);
	}
}

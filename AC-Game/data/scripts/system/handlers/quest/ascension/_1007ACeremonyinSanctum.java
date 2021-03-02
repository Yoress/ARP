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
package quest.ascension;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.craft.CraftSkillUpdateService;
import com.aionemu.gameserver.services.teleport.TeleportService2;

/**
 * @author MrPoke + Dune11
 * @reworked vlog
 * @modified Yon (Aion Reconstruction Project) -- No longer misuses the sendQuestSelectionDialog(QuestEnv) method.
 */
public class _1007ACeremonyinSanctum extends QuestHandler {
	
	private final static int questId = 1007;
	
	public _1007ACeremonyinSanctum() {
		super(questId);
	}
	
	@Override
	public void register() {
		int[] npcs = {790001, 203725, 203752, 203758, 203759, 203760, 203761, 801212, 801213};
		if (CustomConfig.ENABLE_SIMPLE_2NDCLASS) {
			return;
		}
		qe.registerOnLevelUp(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();
		if (qs == null) {
			return false;
		}
		int var = qs.getQuestVars().getQuestVars();
		
		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
			case 790001: { // Pernos
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 0) {
						return sendQuestDialog(env, 1011);
					}
				}
				case SETPRO1: {
					changeQuestStep(env, 0, 1, false); // 1
					TeleportService2.teleportTo(player, 110010000, 1313f, 1512f, 568f);
					return closeDialogWindow(env);
				}
				}
				break;
			}
			case 203725: { // Leah
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 1) {
						return sendQuestDialog(env, 1352);
					}
				}
				case SELECT_ACTION_1353: {
					return playQuestMovie(env, 92);
				}
				case SETPRO2: {
					return defaultCloseDialog(env, 1, 2); // 2
				}
				}
				break;
			}
			case 203752: { // Jucleas
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 2) {
						return sendQuestDialog(env, 1693);
					}
				}
				case SELECT_ACTION_1694: {
					return playQuestMovie(env, 91);
				}
				case SETPRO3: {
					if (var == 2) {
						PlayerClass playerClass = PlayerClass.getStartingClassFor(player.getCommonData().getPlayerClass());
						switch (playerClass) {
						case WARRIOR: {
							qs.setQuestVar(10);
							break;
						}
						case SCOUT: {
							qs.setQuestVar(20);
							break;
						}
						case MAGE: {
							qs.setQuestVar(30);
							break;
						}
						case PRIEST: {
							qs.setQuestVar(40);
							break;
						}
						case ENGINEER: {
							qs.setQuestVar(50);
							break;
						}
						case ARTIST: {
							qs.setQuestVar(60);
							break;
						}
						}
//						qs.setStatus(QuestStatus.REWARD);
//						updateQuestStatus(env);
//						//Don't call this here; in general, the only valid place to call this is when the DialogAction is USE_OBJECT
//						return sendQuestSelectionDialog(env);
						var = qs.getQuestVarById(0);
						return defaultCloseDialog(env, var, var, true);
					}
				}
					break;
				}
			}
				break;
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203758 && var == 10) {
				switch (env.getDialogId()) {
				case -1:
					return sendQuestDialog(env, 2034);
				case 1009:
					return sendQuestDialog(env, 5);
				case 8:
				case 9:
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
				case 16:
				case 18:
//					if (QuestService.finishQuest(env, 0)) {
//						//Don't call this here; in general, the only valid place to call this is when the DialogAction is USE_OBJECT
//						return sendQuestSelectionDialog(env);
//					}
					return sendQuestEndDialog(env, 0);
				}
			} else if (targetId == 203759 && var == 20) {
				switch (env.getDialogId()) {
				case -1:
					return sendQuestDialog(env, 2375);
				case 1009:
					return sendQuestDialog(env, 6);
				case 8:
				case 9:
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
				case 16:
				case 18:
//					if (QuestService.finishQuest(env, 1)) {
//						//Don't call this here; in general, the only valid place to call this is when the DialogAction is USE_OBJECT
//						return sendQuestSelectionDialog(env);
//					}
					return sendQuestEndDialog(env, 1);
				}
			} else if (targetId == 203760 && var == 30) {
				switch (env.getDialogId()) {
				case -1:
					return sendQuestDialog(env, 2716);
				case 1009:
					return sendQuestDialog(env, 7);
				case 8:
				case 9:
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
				case 16:
				case 18:
//					if (QuestService.finishQuest(env, 2)) {
//						//Don't call this here; in general, the only valid place to call this is when the DialogAction is USE_OBJECT
//						return sendQuestSelectionDialog(env);
//					}
					return sendQuestEndDialog(env, 2);
				}
			} else if (targetId == 203761 && var == 40) {
				switch (env.getDialogId()) {
				case -1:
					return sendQuestDialog(env, 3057);
				case 1009:
					return sendQuestDialog(env, 8);
				case 8:
				case 9:
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
				case 16:
				case 18:
//					if (QuestService.finishQuest(env, 3)) {
//						//Don't call this here; in general, the only valid place to call this is when the DialogAction is USE_OBJECT
//						return sendQuestSelectionDialog(env);
//					}
					return sendQuestEndDialog(env, 3);
				}
			} else if (targetId == 801212 && var == 50) {
				switch (env.getDialogId()) {
				case -1:
					return sendQuestDialog(env, 3398);
				case 1009:
					return sendQuestDialog(env, 45);
				case 8:
				case 9:
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
				case 16:
				case 18:
//					if (QuestService.finishQuest(env, 4)) {
//						//Don't call this here; in general, the only valid place to call this is when the DialogAction is USE_OBJECT
//						return sendQuestSelectionDialog(env);
//					}
					return sendQuestEndDialog(env, 4);
				}
			} else if (targetId == 801213 && var == 60) {
				switch (env.getDialogId()) {
				case -1:
					return sendQuestDialog(env, 3739);
				case 1009:
					return sendQuestDialog(env, 46);
				case 8:
				case 9:
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
				case 16:
				case 18:
//					if (QuestService.finishQuest(env, 5)) {
//						//Don't call this here; in general, the only valid place to call this is when the DialogAction is USE_OBJECT
//						return sendQuestSelectionDialog(env);
//					}
					return sendQuestEndDialog(env, 5);
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		CraftSkillUpdateService.getInstance().setMorphRecipe(env.getPlayer());
		return defaultOnLvlUpEvent(env, 1006);
	}
}

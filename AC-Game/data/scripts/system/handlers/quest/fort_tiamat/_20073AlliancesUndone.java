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
package quest.fort_tiamat;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Cheatkiller
 *
 */
public class _20073AlliancesUndone extends QuestHandler {
	
	private final static int questId = 20073;
	private static List<Integer> beasts = new ArrayList<Integer>();
	
	public _20073AlliancesUndone() {
		super(questId);
	}
	
	@Override
	public void register() {
		int[] npcs = {798800, 205579, 205987, 203550};
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
		beasts.add(218253);
		beasts.add(218333);
		beasts.add(218421);
		beasts.add(218464);
		for (int beast : beasts) {
			qe.registerQuestNpc(beast).addOnKillEvent(questId);
		}
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterWorld(questId);
		qe.registerQuestItem(182213252, questId);
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
		
		int var = qs.getQuestVarById(0);
		
		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 798800) {
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 0) {
						return sendQuestDialog(env, 1011);
					} else if (var == 2) {
						return sendQuestDialog(env, 1693);
					} else if (var == 8) {
						return sendQuestDialog(env, 4080);
					}
				}
				case SETPRO1: {
					giveQuestItem(env, 182213252, 1);
					return defaultCloseDialog(env, 0, 1); // 1
				}
				case SETPRO3: {
					giveQuestItem(env, 182213253, 1);
					giveQuestItem(env, 182213254, 1);
					return defaultCloseDialog(env, 2, 3);
				}
				case SET_SUCCEED: {
					return defaultCloseDialog(env, 8, 9, true, false);
				}
				}
			} else if (targetId == 205579) {
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 3) {
						return sendQuestDialog(env, 2034);
					} else if (var == 7) {
						return sendQuestDialog(env, 3398);
					}
				}
				case SETPRO5: {
					removeQuestItem(env, 182213253, 1);
					removeQuestItem(env, 182213254, 1);
					return defaultCloseDialog(env, 3, 5);
				}
				case SETPRO8: {
					return defaultCloseDialog(env, 7, 8);
				}
				}
			} else if (targetId == 205987) {
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 5) {
						return sendQuestDialog(env, 2716);
					}
				}
				case SETPRO6: {
					return defaultCloseDialog(env, 5, 6);
				}
				}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203550) {
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
		int targetId = env.getTargetId();
		
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 6) {
				int kakaro = qs.getQuestVarById(1);
				int danitur = qs.getQuestVarById(2);
				int kratr = qs.getQuestVarById(3);
				int ermus = qs.getQuestVarById(4);
				if (beasts.contains(targetId)) {
					if (kakaro + danitur + kratr + ermus == 3) {
						qs.setQuestVar(7);
						updateQuestStatus(env);
						return true;
					}
					return defaultOnKillEvent(env, targetId, 0, 1, beasts.indexOf(targetId) + 1); // i:
																									 // 1
				}
			}
		}
		return false;
	}
	
	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			removeQuestItem(env, 182213252, 1);
			changeQuestStep(env, 1, 2, false);
			return HandlerResult.SUCCESS;
		}
		return HandlerResult.FAILED;
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 20072);
	}
}

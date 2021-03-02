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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnSearchResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * @author Rhys2002
 * @reworked vlog
 */
public class _2071SpeakingBalaur extends QuestHandler {
	
	private final static int questId = 2071;
	
	public _2071SpeakingBalaur() {
		super(questId);
	}
	
	@Override
	public void register() {
		int[] npcs = {278003, 278086, 278039, 279027, 204210};
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(253610).addOnAttackEvent(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		if (qs == null) {
			return false;
		}
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		
		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
			case 278003: { // Hisui
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
			case 278086: { // Sinjah
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
			case 278039: { // Grunn
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
			case 279027: { // Kaoranerk
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 4) {
						return sendQuestDialog(env, 2375);
					} else if (var == 6) {
						return sendQuestDialog(env, 3057);
					}
				}
				case SELECT_ACTION_3058: {
					removeQuestItem(env, 182205501, 1);
					playQuestMovie(env, 293);
					return sendQuestDialog(env, 3058);
				}
				case SETPRO5: {
					return defaultCloseDialog(env, 4, 5); // 5
				}
				case SET_SUCCEED: {
					return defaultCloseDialog(env, 6, 6, true, false); // reward
				}
				}
				break;
			}
			case 204210: { // Phosphor
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 5) {
						return sendQuestDialog(env, 2716);
					}
				}
				case SETPRO6: {
					return defaultCloseDialog(env, 5, 6, 182205501, 1, 0, 0); // 6
				}
				}
			}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 278003) { // Hisui
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onAttackEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 2) {
				final Npc npc = (Npc) env.getVisibleObject();
				final SpawnSearchResult searchResult = DataManager.SPAWNS_DATA2.getFirstSpawnByNpcId(npc.getWorldId(), 278086); // Sinjah
				if (MathUtil.getDistance(searchResult.getSpot().getX(), searchResult.getSpot().getY(), searchResult.getSpot().getZ(), npc.getX(), npc.getY(), npc.getZ()) <= 15) {
					npc.getController().onDie(player);
					changeQuestStep(env, 2, 3, false); // 3
					return true;
				}
			}
		}
		return true;
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2701, true);
	}
}

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
package quest.beluslan;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * Talk with Sleipnir (204768). Talk with Rubelik (204743). Talk with Sleipnir. Find Esnu (204808) and make a deal with her. Remove Aika Deathsong
 * (213741) and bring the key (182204312) to Esnu. Talk again with Esnu and get the key to Golden Trumpet Temple (730135). Report the results to
 * Sleipnir.
 *
 * @author Hellboy aion4Free
 * @reworked vlog
 */
public class _2055TheSeirensTreasure extends QuestHandler {
	
	private final static int questId = 2055;
	private final static int[] npc_ids = {204768, 204743, 204808};
	
	public _2055TheSeirensTreasure() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		for (int npc_id : npc_ids) {
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
		}
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env, 2054);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] quests = {2500, 2054};
		return defaultOnLvlUpEvent(env, quests, true);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			return false;
		}
		
		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		
		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
			case 204768: // Sleipnir
				switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 0) {
						return sendQuestDialog(env, 1011);
					}
					if (var == 2) {
						return sendQuestDialog(env, 1693);
					}
					if (var == 6 && player.getInventory().getItemCountByItemId(182204321) >= 1) {
						return sendQuestDialog(env, 3057);
					}
				case SETPRO1:
					if (var == 0) {
						playQuestMovie(env, 239);
						return defaultCloseDialog(env, 0, 1, 182204310, 1, 0, 0); // 1
					}
				case SETPRO3:
					return defaultCloseDialog(env, 2, 3); // 3
				case SELECT_QUEST_REWARD: {
					removeQuestItem(env, 182204321, 1);
					return defaultCloseDialog(env, 6, 6, true, true); // reward
				}
				case SELECT_ACTION_3143:
					return sendQuestDialog(env, 3143);
				case SETPRO7: {
					playQuestMovie(env, 239);
					removeQuestItem(env, 182204321, 1);
					return defaultCloseDialog(env, 6, 6, true, true); // reward
				}
				}
				break;
			case 204743: // Rubelik
				switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 1) {
						return sendQuestDialog(env, 1352);
					}
				case SETPRO2:
					return defaultCloseDialog(env, 1, 2, 182204311, 1, 182204310, 1); // 2
				}
				break;
			case 204808: // Esnu
				switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 3) {
						return sendQuestDialog(env, 2034);
					}
					if (var == 4) {
						return sendQuestDialog(env, 2375);
					}
					if (var == 5) {
						return sendQuestDialog(env, 2716);
					}
				case SETPRO4:
					if (var == 3) {
						playQuestMovie(env, 240);
						return defaultCloseDialog(env, 3, 4, 0, 0, 182204311, 1); // 4
					}
				case CHECK_USER_HAS_QUEST_ITEM:
					return checkQuestItems(env, 4, 5, false, 10000, 10001); // 5
				case FINISH_DIALOG:
					if (var == 5) {
						defaultCloseDialog(env, 5, 5); // 5
					}
					if (var == 4) {
						defaultCloseDialog(env, 4, 4); // 4
					}
				case SETPRO6:
					return defaultCloseDialog(env, 5, 6, false, false, 182204321, 1, 0, 0); // 6
				}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204768) { // Sleipnir
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}

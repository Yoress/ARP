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
package quest.theobomos;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Balthazar
 * @modified Yon (Aion Reconstruction Project) -- No longer keeps the quest dialog page open.
 */
public class _3013AClueLeftByTheDead extends QuestHandler {
	
	private final static int questId = 3013;
	
	public _3013AClueLeftByTheDead() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(798132).addOnQuestStart(questId);
		qe.registerQuestNpc(798132).addOnTalkEvent(questId);
		qe.registerQuestNpc(798146).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798132) {
				switch (env.getDialog()) {
				case QUEST_SELECT: {
					return sendQuestDialog(env, 4762);
				}
				default:
					return sendQuestStartDialog(env);
				}
			}
		}
		
		if (qs == null) {
			return false;
		}
		
		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
			case 798132: {
				switch (env.getDialog()) {
				case QUEST_SELECT: {
					if (qs.getQuestVarById(0) == 0) {
						long itemCount = player.getInventory().getItemCountByItemId(182208008);
						if (itemCount >= 1) {
							return sendQuestDialog(env, 1011);
						} else {
							return sendQuestDialog(env, 10001);
						}
					}
				}
				case CHECK_USER_HAS_QUEST_ITEM: {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					removeQuestItem(env, 182208008, 1);
//					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), qs.getQuestVarById(0) + 1, 0, 0, 182208008, 1);
				}
				}
			}
			case 798146: {
				switch (env.getDialog()) {
				case QUEST_SELECT: {
					if (qs.getQuestVarById(0) == 1) {
						return sendQuestDialog(env, 1352);
					}
				}
				case SET_SUCCEED: {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					updateQuestStatus(env);
//					qs.setStatus(QuestStatus.REWARD);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), qs.getQuestVarById(0), true);
				}
				}
			}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798146) {
				switch (env.getDialog()) {
				case QUEST_SELECT: {
					return sendQuestDialog(env, 10002);
				}
				case SELECT_QUEST_REWARD: {
					return sendQuestDialog(env, 5);
				}
				default:
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}

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
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Rolandas
 * @modified Yon (Aion Reconstruction Project) -- No longer keeps the quest dialog page open.
 */
public class _3968PalentinesRequest extends QuestHandler {
	
	private final static int questId = 3968;
	
	public _3968PalentinesRequest() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(798390).addOnQuestStart(questId);
		qe.registerQuestNpc(798176).addOnTalkEvent(questId);
		qe.registerQuestNpc(204528).addOnTalkEvent(questId);
		qe.registerQuestNpc(203927).addOnTalkEvent(questId);
		qe.registerQuestNpc(798390).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (targetId == 798390) {
			if (qs == null || qs.getStatus() == QuestStatus.NONE) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1011);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		}
		
		if (qs == null) {
			return false;
		}
		
		int var = qs.getQuestVarById(0);
		
		if (targetId == 798176) {
			if (qs.getStatus() == QuestStatus.START && var == 0) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1352);
				} else if (env.getDialog() == DialogAction.SETPRO1) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					if (giveQuestItem(env, 182206123, 1)) {
//						qs.setQuestVar(++var);
//						updateQuestStatus(env);
//						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					}
//					return true;
					return defaultCloseDialog(env, var, var + 1, 182206123, 1, 0, 0);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (targetId == 204528) {
			if (qs.getStatus() == QuestStatus.START && var == 1) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1693);
				} else if (env.getDialog() == DialogAction.SETPRO2) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					if (giveQuestItem(env, 182206124, 1)) {
//						qs.setQuestVar(++var);
//						updateQuestStatus(env);
//						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					}
//					return true;
					return defaultCloseDialog(env, var, var + 1, 182206124, 1, 0, 0);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (targetId == 203927) {
			if (qs.getStatus() == QuestStatus.START && var == 2) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 2034);
				} else if (env.getDialog() == DialogAction.SETPRO3) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					if (giveQuestItem(env, 182206125, 1)) {
//						qs.setQuestVar(++var);
//						qs.setStatus(QuestStatus.REWARD);
//						updateQuestStatus(env);
//						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					}
//					return true;
					return defaultCloseDialog(env, var, var + 1, true, 182206125, 1, 0, 0);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (targetId == 798390) {
			if (env.getDialog() == DialogAction.USE_OBJECT && qs.getStatus() == QuestStatus.REWARD) {
				return sendQuestDialog(env, 2375);
			} else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id() && qs.getStatus() != QuestStatus.COMPLETE && qs.getStatus() != QuestStatus.NONE) {
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				removeQuestItem(env, 182206123, 1);
				removeQuestItem(env, 182206124, 1);
				removeQuestItem(env, 182206125, 1);
				return sendQuestEndDialog(env);
			} else {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}

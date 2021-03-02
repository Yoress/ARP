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
package quest.inggison;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author dta3000
 * @modified Yon (Aion Reconstruction Project) -- No longer keeps the quest dialog page open.
 */
public class _11009MeiriaFriendlySuggestion extends QuestHandler {
	
	private final static int questId = 11009;
	
	public _11009MeiriaFriendlySuggestion() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(798945).addOnQuestStart(questId);
		qe.registerQuestNpc(798945).addOnTalkEvent(questId);
		qe.registerQuestNpc(799008).addOnTalkEvent(questId);
		qe.registerQuestNpc(799017).addOnTalkEvent(questId);
		qe.registerQuestNpc(798941).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (targetId == 798945) {
			if (qs == null) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1011);
				} else if (env.getDialogId() == DialogAction.QUEST_ACCEPT_1.id()) {
					if (giveQuestItem(env, 182206711, 2)) {
						return sendQuestStartDialog(env);
					} else {
						return true;
					}
				} else {
					return sendQuestStartDialog(env);
				}
			}
		}
		
		if (qs == null) {
			return false;
		}
		
		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
			case 799008: {
				switch (env.getDialog()) {
				case QUEST_SELECT: {
					return sendQuestDialog(env, 1352);
				}
				case SETPRO1: {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					removeQuestItem(env, 182206711, 1);
//					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), qs.getQuestVarById(0) + 1, 0, 0, 182206711, 1);
				}
				}
			}
			case 799017: {
				switch (env.getDialog()) {
				case QUEST_SELECT: {
					return sendQuestDialog(env, 1693);
				}
				case SETPRO2: {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					removeQuestItem(env, 182206711, 1);
//					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					//TODO: This is odd handling; look to replace it with a call to super.
					if (giveQuestItem(env, 182206712, 1)) {
						return true;
					}
					return defaultCloseDialog(env, qs.getQuestVarById(0), qs.getQuestVarById(0) + 1, 0, 0, 182206711, 1);
				}
				}
			}
			case 798941: {
				switch (env.getDialog()) {
				case QUEST_SELECT: {
					return sendQuestDialog(env, 2034);
				}
				case SETPRO3: {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					removeQuestItem(env, 182206712, 1);
//					qs.setQuestVar(3);
//					qs.setStatus(QuestStatus.REWARD);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), 3, true, 0, 0, 182206712, 1);
				}
				}
			}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798945) {
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 2375);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}

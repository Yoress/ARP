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

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Leunam
 * @modified Yon (Aion Reconstruction Project) -- No longer keeps the quest dialog page open.
 */
public class _3058StoneofMabolo extends QuestHandler {
	
	private final static int questId = 3058;
	
	public _3058StoneofMabolo() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(798189).addOnTalkEvent(questId);
		qe.registerQuestNpc(203701).addOnTalkEvent(questId);
		qe.registerQuestNpc(798213).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (env.getDialogId() == DialogAction.QUEST_ACCEPT_1.id()) {
				QuestService.startQuest(env);
				//FIXME: Don't send SM_DIALOG_WINDOW through unique quest handlers
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 0));
				return true;
//				return closeDialogWindow(env);
			}/* else {
				//TODO: Check if there's a decline dialog to send, instead
				//FIXME: Don't send SM_DIALOG_WINDOW through unique quest handlers
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 0));
				return closeDialogWindow(env);
			}*/
		}
		if (qs == null) {
			return false;
		}
		
		int var = qs.getQuestVarById(0);
		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798213) {
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 2375);
				} else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id()) {
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		} else if (qs.getStatus() != QuestStatus.START) {
			return false;
		}
		if (targetId == 798189) {
			switch (env.getDialog()) {
			case QUEST_SELECT:
				if (var == 0) {
					return sendQuestDialog(env, 1352);
				}
			case SETPRO1:
				if (var == 0) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, var + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, var, var + 1);
				}
				return false;
			}
		} else if (targetId == 203701) {
			switch (env.getDialog()) {
			case QUEST_SELECT:
				if (var == 1) {
					return sendQuestDialog(env, 1693);
				}
			case SETPRO2:
				if (var == 1) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, var + 1); //TODO: This may need to be updated; defaultCloseDialog() will skip it.
//					qs.setStatus(QuestStatus.REWARD);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, var, var + 1, true);
				}
				return false;
			}
		}
		return false;
	}
}

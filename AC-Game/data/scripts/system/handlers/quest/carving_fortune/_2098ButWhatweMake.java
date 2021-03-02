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
package quest.carving_fortune;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Manu72
 * @modified Yon (Aion Reconstruction Project) -- No longer keeps the quest dialog page open.
 */
public class _2098ButWhatweMake extends QuestHandler {
	
	private final static int questId = 2098;
	
	public _2098ButWhatweMake() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(203550).addOnQuestStart(questId); // Munin
		qe.registerQuestNpc(203550).addOnTalkEvent(questId); // Munin
		qe.registerQuestNpc(204361).addOnTalkEvent(questId); // Hreidmar
		qe.registerQuestNpc(204408).addOnTalkEvent(questId); // Bulagan
		qe.registerQuestNpc(205198).addOnTalkEvent(questId); // Cayron
		qe.registerQuestNpc(204805).addOnTalkEvent(questId); // Vanargand
		qe.registerQuestNpc(204808).addOnTalkEvent(questId); // Esnu
		qe.registerQuestNpc(203546).addOnTalkEvent(questId); // Skuld
		qe.registerQuestNpc(204387).addOnTalkEvent(questId); // Ananta
		qe.registerQuestNpc(205190).addOnTalkEvent(questId); // Seznec
		qe.registerQuestNpc(204207).addOnTalkEvent(questId); // Kasir
		qe.registerQuestNpc(204301).addOnTalkEvent(questId); // Aegir
		qe.registerQuestNpc(205155).addOnTalkEvent(questId); // Heintz
		qe.registerQuestNpc(204784).addOnTalkEvent(questId); // Delris
		qe.registerQuestNpc(278001).addOnTalkEvent(questId); // Votan
		qe.registerQuestNpc(204053).addOnTalkEvent(questId); // Kvasir
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2097);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (targetId == 203550) // Munin
		{
			if (qs == null || qs.getStatus() == QuestStatus.START) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1011);
				} else if (env.getDialog() == DialogAction.SETPRO1) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					if (giveQuestItem(env, 182207089, 1)) ;
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), qs.getQuestVarById(0) + 1, 182207089, 1, 0, 0);
				} else {
					return sendQuestStartDialog(env);
				}
			} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) // Reward
			{
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 10002);
				} else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id()) {
					qs.setQuestVar(14);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return sendQuestEndDialog(env);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		} else if (targetId == 204361) // Hreidmar
		{
			
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1352);
				} else if (env.getDialog() == DialogAction.SETPRO2) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), qs.getQuestVarById(0) + 1);
				} else {
					return sendQuestStartDialog(env);
				}
			}
			
		} else if (targetId == 204408) // Bulagan
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 2) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1693);
				} else if (env.getDialog() == DialogAction.SETPRO3) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), qs.getQuestVarById(0) + 1);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (targetId == 205198) // Cayron
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 3) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 2034);
				} else if (env.getDialog() == DialogAction.SETPRO4) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), qs.getQuestVarById(0) + 1);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (targetId == 204805) // Vanargand
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 4) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 2375);
				} else if (env.getDialogId() == DialogAction.SETPRO5.id()) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), qs.getQuestVarById(0) + 1);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (targetId == 204808) // Esnu
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 5) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 2716);
				} else if (env.getDialogId() == DialogAction.SETPRO6.id()) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					removeQuestItem(env, 182207089, 1);
//					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					if (giveQuestItem(env, 182207090, 1)) ;
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), qs.getQuestVarById(0) + 1, 182207090, 1, 182207089, 1);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (targetId == 203546) // Skuld
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 6) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 3057);
				} else if (env.getDialogId() == DialogAction.SETPRO7.id()) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), qs.getQuestVarById(0) + 1);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (targetId == 204387) // Ananta
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 7) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 3398);
				} else if (env.getDialogId() == DialogAction.SETPRO8.id()) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), qs.getQuestVarById(0) + 1);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (targetId == 205190) // Seznec
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 8) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 3739);
				} else if (env.getDialogId() == DialogAction.SETPRO9.id()) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), qs.getQuestVarById(0) + 1);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (targetId == 204207) // Kasir
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 9) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4080);
				} else if (env.getDialogId() == DialogAction.SETPRO10.id()) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					removeQuestItem(env, 182207090, 1);
//					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					if (giveQuestItem(env, 182207091, 1)) ;
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), qs.getQuestVarById(0) + 1, 182207091, 1, 182207090, 1);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (targetId == 204301) // Aegir
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 10) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1608);
				} else if (env.getDialogId() == DialogAction.SETPRO11.id()) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), qs.getQuestVarById(0) + 1);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (targetId == 205155) // Heintz
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 11) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1949);
				} else if (env.getDialogId() == DialogAction.SETPRO12.id()) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), qs.getQuestVarById(0) + 1);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (targetId == 204784) // Delris
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 12) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 2290);
				} else if (env.getDialogId() == DialogAction.SETPRO13.id()) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), qs.getQuestVarById(0) + 1);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (targetId == 278001) // Votan
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 13) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 2631);
				} else if (env.getDialogId() == DialogAction.SETPRO14.id()) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), qs.getQuestVarById(0) + 1);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (targetId == 204053) // Kvasir
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 14) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 2972);
				} else if (env.getDialogId() == DialogAction.SET_SUCCEED.id()) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					removeQuestItem(env, 182207091, 1);
//					qs.setStatus(QuestStatus.REWARD);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					if (giveQuestItem(env, 182207092, 1)) ;
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), qs.getQuestVarById(0), true, 182207092, 1, 182207091, 1);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		}
		return false;
		
	}
}

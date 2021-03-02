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
package quest.danaria;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @modified Yon (Aion Reconstruction Project) -- No longer keeps the quest dialog page open.
 */
public class _23054AndBusinessIsGood extends QuestHandler {
	
	private final static int questId = 23054;
	
	public _23054AndBusinessIsGood() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(801127).addOnQuestStart(questId); // Kisping.
		qe.registerQuestNpc(801125).addOnTalkEvent(questId); // Kisping.
		qe.registerQuestNpc(801127).addOnTalkEvent(questId); // Ivolk.
		qe.registerQuestNpc(801128).addOnTalkEvent(questId); // Melkorka.
		qe.registerQuestItem(182213442, questId); // Mission Report.
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 801127) { // Ivolk.
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
		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
			case 801127: { // Ivolk.
				if (env.getDialog() == DialogAction.QUEST_SELECT) return sendQuestDialog(env, 1693);
				if (env.getDialog() == DialogAction.SETPRO2) {
					removeQuestItem(env, 182213441, 1); // Kisping's Report.
					qs.setQuestVar(2);
					changeQuestStep(env, 1, 2, false);
					updateQuestStatus(env);
					giveQuestItem(env, 182213442, 1); // Mission Report.
					return closeDialogWindow(env);
				}
			}
			}
			switch (targetId) {
			case 801125: { // Kisping.
				switch (env.getDialog()) {
				case QUEST_SELECT: {
					return sendQuestDialog(env, 1352);
				}
				case SETPRO1: {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
//					updateQuestStatus(env);
//					giveQuestItem(env, 182213441, 1); // Kisping's Report.
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), qs.getQuestVarById(0) + 1, 182213441, 1, 0, 0);
				}
				default:
					break;
				}
			}
			case 801128: { // Melkorka.
				switch (env.getDialog()) {
				case QUEST_SELECT: {
					return sendQuestDialog(env, 2375);
				}
				case SELECT_QUEST_REWARD: {
					qs.setQuestVar(2);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					
					removeQuestItem(env, 182213442, 1); // Mission Report.
					return sendQuestEndDialog(env);
				}
				default:
					return sendQuestEndDialog(env);
				}
			}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 801128) { // Melkorka.
				switch (env.getDialog()) {
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
	
	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			return HandlerResult.fromBoolean(useQuestItem(env, item, 2, 3, false, 182213442, 1));
		}
		return HandlerResult.SUCCESS; // ??
	}
}

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
package quest.heiron;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * Report-To-Quest Start: Perento (204500) Take the paper voucher (182213000) to Koruchinerk (798321) Go to New Heiron Gate and meet Herthia (205228)
 * Bring the Fake Stigma (182213001) to Perento
 *
 * @author vlog
 * @modified Gigi
 * @modified Yon (Aion Reconstruction Project) -- No longer keeps the quest dialog page open.
 */
public class _18600ScoringSomeBadStigma extends QuestHandler {
	
	private final static int _questId = 18600;
	private final static int[] _npcs = {204500, 798321, 205228};
	
	public _18600ScoringSomeBadStigma() {
		super(_questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(204500).addOnQuestStart(_questId);
		for (int npc_id : _npcs) {
			qe.registerQuestNpc(npc_id).addOnTalkEvent(_questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(_questId);
		
		if (targetId == 204500) // Perento
		{
			if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1011);
				} else if (env.getDialogId() == DialogAction.QUEST_ACCEPT_1.id()) {
					giveQuestItem(env, 182213000, 1);
					return sendQuestStartDialog(env);
				} else {
					return sendQuestStartDialog(env);
				}
			}
			if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 2375);
				} else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id()) {
					removeQuestItem(env, 182213001, 1);
					return sendQuestEndDialog(env);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		if (targetId == 798321) // Koruchinerk
		{
			if (qs != null && qs.getStatus() == QuestStatus.START) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1352);
				}
				if (env.getDialog() == DialogAction.SETPRO1) {
					return defaultCloseDialog(env, 0, 1, 0, 0, 182213000, 1); // 1
				}
			}
		}
		if (targetId == 205228) // Herthia
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1693);
				} else if (env.getDialog() == DialogAction.SETPRO2) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVar(3);
//					qs.setStatus(QuestStatus.REWARD);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), 3, true);
				}
			}
		}
		return false;
	}
}

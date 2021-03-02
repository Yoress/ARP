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
package quest.morheim;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author MrPoke remod By Nephis
 * @modified Yon (Aion Reconstruction Project) -- No longer keeps the quest dialog page open.
 */
public class _2307IrresistibleSoup extends QuestHandler {
	
	private final static int questId = 2307; //FIXME: INGREDIENT CHECK NEED TO SCRIPT
	
	public _2307IrresistibleSoup() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(204378).addOnQuestStart(questId); // Favyr
		qe.registerQuestNpc(204378).addOnTalkEvent(questId);
		qe.registerQuestNpc(204336).addOnTalkEvent(questId); // Spedor
		qe.registerQuestNpc(700247).addOnTalkEvent(questId); // Aromatic Soup
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (targetId == 204378) // Favyr
		{
			if (qs == null || qs.getStatus() == QuestStatus.NONE) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				} else {
					return sendQuestStartDialog(env);
				}
			} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
				return sendQuestEndDialog(env);
			}
		} else if (targetId == 204336) // Spedor
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1011);
				} else if (env.getDialog() == DialogAction.SETPRO1) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVar(2);
//					qs.setStatus(QuestStatus.REWARD);
//					updateQuestStatus(env);
//					removeQuestItem(env, 182204106, 1);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), 2, true, 0, 0, 182204106, 1);
				} else if (env.getDialogId() == DialogAction.SELECT_ACTION_1182.id()) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVar(1);
//					qs.setStatus(QuestStatus.REWARD);
//					updateQuestStatus(env);
//					removeQuestItem(env, 182204107, 1);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), 1, true, 0, 0, 182204107, 1);
				} else if (env.getDialogId() == DialogAction.SELECT_ACTION_1267.id()) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVar(1);
//					qs.setStatus(QuestStatus.REWARD);
//					updateQuestStatus(env);
//					removeQuestItem(env, 182204108, 1);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), 1, true, 0, 0, 182204108, 1);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
			switch (targetId) {
			case 700247: // Aromatic Soup
				if (qs.getQuestVarById(0) == 0 && env.getDialog() == DialogAction.USE_OBJECT) {
					qs.setQuestVar(1);
					updateQuestStatus(env);
				}
				break;
			}
		}
		return false;
	}
}

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
public class _3969SexiestManAlive extends QuestHandler {
	
	private final static int questId = 3969;
	
	public _3969SexiestManAlive() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(798390).addOnQuestStart(questId);// Palentine
		qe.registerQuestNpc(798391).addOnTalkEvent(questId);// Andu
		qe.registerQuestNpc(798390).addOnTalkEvent(questId);// Palentine
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		
		QuestState qs2 = player.getQuestStateList().getQuestState(3968);
		if (qs2 == null || qs2.getStatus() != QuestStatus.COMPLETE) {
			return false;
		}
		
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if (targetId == 798390)// Palentine
		{
			if (qs == null || qs.getStatus() == QuestStatus.NONE) {
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 1011);
				} else if (env.getDialogId() == DialogAction.QUEST_ACCEPT_1.id()) {
					if (giveQuestItem(env, 182206126, 1)) {
						return sendQuestStartDialog(env);
					}
					return true;
				} else {
					return sendQuestStartDialog(env);
				}
			}
		}
		
		if (qs == null) {
			return false;
		}
		
		int var = qs.getQuestVarById(0);
		
		if (targetId == 798391)// Andu
		{
			if (qs.getStatus() == QuestStatus.START && var == 0) {
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 1352);
				} else if (env.getDialog() == DialogAction.SETPRO1) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					if (player.getInventory().getItemCountByItemId(182206126) > 0) {
//						removeQuestItem(env, 182206126, 1);
//						qs.setQuestVar(++var);
//						qs.setStatus(QuestStatus.REWARD);
//						updateQuestStatus(env);
//						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					}
//					return true;
					return defaultCloseDialog(env, var, var + 1, true, 0, 0, 182206126, 1);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (targetId == 798390)// Palentine
		{
			if (qs.getStatus() == QuestStatus.REWARD) {
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 2375);
				}
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}

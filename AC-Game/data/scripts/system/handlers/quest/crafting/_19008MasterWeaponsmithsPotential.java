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
package quest.crafting;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Thuatan,Modifly by Newlives@aioncore 29-1-2015
 * @modified Yon (Aion Reconstruction Project) -- No longer keeps the quest dialog page open.
 */
public class _19008MasterWeaponsmithsPotential extends QuestHandler {
	
	private final static int questId = 19008;
	
	public _19008MasterWeaponsmithsPotential() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(203788).addOnQuestStart(questId);
		qe.registerQuestNpc(203788).addOnTalkEvent(questId);
		qe.registerQuestNpc(203789).addOnTalkEvent(questId);
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
			if (targetId == 203788) { // Anteros
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		}
		
		if (qs == null) {
			return false;
		}
		
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
			case 203789: { // Nakia
				switch (env.getDialog()) {
				case QUEST_SELECT:
					return sendQuestDialog(env, 1011);
				case SETPRO10:
					//TODO: This is odd handling; look to replace with a call to super.
					if (!giveQuestItem(env, 152201706, 1)) {
						return true;
					}
					if (!giveQuestItem(env, 152020250, 1)) {
						return true;
					}
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), 1);
				case SETPRO20:
					//TODO: This is odd handling; look to replace with a call to super.
					if (!giveQuestItem(env, 152201707, 1)) {
						return true;
					}
					if (!giveQuestItem(env, 152020250, 1)) {
						return true;
					}
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), 1);
				}
			}
			case 203788: { //
				switch (env.getDialog()) {
				case QUEST_SELECT:
					long itemCount1 = player.getInventory().getItemCountByItemId(182206764);
					if (itemCount1 > 0) {
						removeQuestItem(env, 182206764, 1);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 1352);
					} else {
						return sendQuestDialog(env, 2716);
					}
				}
			}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203788) { // Anteros
				if (env.getDialogId() == DialogAction.CHECK_USER_HAS_QUEST_ITEM.id()) {
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}

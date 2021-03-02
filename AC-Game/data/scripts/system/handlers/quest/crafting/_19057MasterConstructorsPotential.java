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
import com.aionemu.gameserver.services.QuestService;

/**
 * @author Ritsu,Modifly by Newlives@aioncore 29-1-2015
 * @modified Yon (Aion Reconstruction Project) -- No longer keeps the quest dialog page open.
 */
public class _19057MasterConstructorsPotential extends QuestHandler {
	
	private final static int questId = 19057;
	private final static int[] recipesItemIds = {152203543, 152203544};
	private final static int[] recipesIds = {155003543, 155003544};
	
	public _19057MasterConstructorsPotential() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(798450).addOnQuestStart(questId);
		qe.registerQuestNpc(798450).addOnTalkEvent(questId);
		qe.registerQuestNpc(798451).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798450) { // Undin
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
			int var = qs.getQuestVarById(0);
			switch (targetId) {
			case 798451: // Lurine
				long kinah = player.getInventory().getKinah();
				switch (dialog) {
				case QUEST_SELECT: {
					switch (var) {
					case 0:
						return sendQuestDialog(env, 1011);
					case 2:
						return sendQuestDialog(env, 4080);
					}
				}
				case SETPRO10:
					if (kinah >= 167500) // Need check how many kinah decrased
					{
						//TODO: This is odd handling; look to replace with a call to super.
						if (!giveQuestItem(env, 152203543, 1)) {
							return true;
						}
						player.getInventory().decreaseKinah(167500);
						//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//						qs.setQuestVarById(0, 1);
//						updateQuestStatus(env);
//						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//						return true;
						return defaultCloseDialog(env, var, 1);
					} else {
						return sendQuestDialog(env, 4400);
					}
				case SETPRO20:
					if (kinah >= 223000) {
						//TODO: This is odd handling; look to replace with a call to super.
						if (!giveQuestItem(env, 152203544, 1)) {
							return true;
						}
						player.getInventory().decreaseKinah(223000);
						//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//						qs.setQuestVarById(0, 1);
//						updateQuestStatus(env);
//						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//						return true;
						return defaultCloseDialog(env, var, 1);
					} else {
						return sendQuestDialog(env, 4400);
					}
				}
			case 798450: // Undin
				switch (env.getDialog()) {
				case QUEST_SELECT: {
					return sendQuestDialog(env, 1352);
				}
				case CHECK_USER_HAS_QUEST_ITEM:
					if (QuestService.collectItemCheck(env, true)) {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 5);
					} else {
						int checkFailId = 3398;
						if (player.getRecipeList().isRecipePresent(recipesIds[0]) || player.getRecipeList().isRecipePresent(recipesIds[1])) {
							checkFailId = 2716;
						} else if (player.getInventory().getItemCountByItemId(recipesItemIds[0]) > 0 || player.getInventory().getItemCountByItemId(recipesItemIds[1]) > 0) {
							checkFailId = 3057;
						}
						
						if (checkFailId == 3398) {
							qs.setQuestVar(2);
							updateQuestStatus(env);
						}
						return sendQuestDialog(env, checkFailId);
					}
				}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798450) { // Undin
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}

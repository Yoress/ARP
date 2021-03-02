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
package quest.beluslan;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * @author kecimis
 * @modified Yon (Aion Reconstruction Project) -- No longer keeps the quest dialog page open.
 */
public class _4200ASuspiciousCall extends QuestHandler {
	
	private final static int questId = 4200;
	private final static int[] npc_ids = {204839, 798332, 700522, 279006, 204286};
	
	/*
	 * 204839 - Uikinerk 798332 - Haorunerk 700522 - Haorunerks Bag 279006 - Garkbinerk 204286 - Payrinrinerk
	 */
	public _4200ASuspiciousCall() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(204839).addOnQuestStart(questId); // Uikinerk
		qe.registerQuestItem(182209097, questId); // Teleport Scroll
		for (int npc_id : npc_ids) {
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204839) // Uikinerk
			{
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				} else {
					return sendQuestStartDialog(env);
				}
			}
			return false;
		}
		
		int var = qs.getQuestVarById(0);
		
		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204286)// Payrinrinerk
			{
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				} else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id()) {
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
			return false;
		} else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 204839)// Uikinerk
			{
				switch (env.getDialog()) {
				case QUEST_SELECT:
					return sendQuestDialog(env, 1003);
				case SELECT_ACTION_1011:
					return sendQuestDialog(env, 1011);
				case SETPRO1:
					// Create instance
					WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(300100000);
					InstanceService.registerPlayerWithInstance(newInstance, player);
					// teleport to cell in steel rake: 300100000 403.55
					// 508.11 885.77 0
					TeleportService2.teleportTo(player, 300100000, newInstance.getInstanceId(), 403.55f, 508.11f, 885.77f);
					qs.setQuestVarById(0, var + 1);
					updateQuestStatus(env);
					return true;
				default:
					break;
				}
			} else if (targetId == 798332 && var == 1) // Haorunerk
			{
				switch (env.getDialog()) {
				case QUEST_SELECT:
					return sendQuestDialog(env, 1352);
				case SELECT_ACTION_1353:
					playQuestMovie(env, 431);
					break;
				case SETPRO2:
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, var + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, var, var + 1);
				default:
					break;
				}
			} else if (targetId == 700522 && var == 2) // Haorunerks Bag,
			{
				qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
				updateQuestStatus(env);
				TeleportService2.teleportTo(player, 400010000, 3419.16f, 2445.43f, 2766.54f, (byte) 57);
				return false;
				
			} else if (targetId == 279006 && var == 3)// Garkbinerk
			{
				switch (env.getDialog()) {
				case QUEST_SELECT:
					return sendQuestDialog(env, 2034);
				case SET_SUCCEED:
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					qs.setStatus(QuestStatus.REWARD);
//					updateQuestStatus(env);
//					return true;
					return defaultCloseDialog(env, var, var, true);
				default:
					break;
				}
			}
		}
		return false;
	}
}

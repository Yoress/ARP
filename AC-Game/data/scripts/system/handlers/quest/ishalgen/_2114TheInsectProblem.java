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
package quest.ishalgen;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * @author Mr. Poke
 * @modified Yon (Aion Reconstruction Project) -- No longer keeps the quest dialog page open.
 */
public class _2114TheInsectProblem extends QuestHandler {
	
	private final static int questId = 2114;
	
	public _2114TheInsectProblem() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(203533).addOnQuestStart(questId);
		qe.registerQuestNpc(203533).addOnTalkEvent(questId);
		qe.registerQuestNpc(210734).addOnKillEvent(questId);
		qe.registerQuestNpc(210380).addOnKillEvent(questId);
		qe.registerQuestNpc(210381).addOnKillEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (targetId == 203533) {
			if (qs == null || qs.getStatus() == QuestStatus.NONE) {
				switch (env.getDialog()) {
				case QUEST_SELECT:
					return sendQuestDialog(env, 1011);
				case SETPRO1:
					if (QuestService.startQuest(env)) {
						//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//						qs = player.getQuestStateList().getQuestState(questId);
//						qs.setQuestVar(1); //This sets vars to {1, 0, 0, 0, 0, 0}
//						this.updateQuestStatus(env);
//						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//						return true;
						return defaultCloseDialog(env, 0, 1);
					}
				case SETPRO2:
					if (QuestService.startQuest(env)) {
						//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//						qs = player.getQuestStateList().getQuestState(questId);
//						qs.setQuestVar(11); //This sets vars to {11, 0, 0, 0, 0, 0}
//						this.updateQuestStatus(env);
//						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//						return true;
						return defaultCloseDialog(env, 0, 11);
					}
				}
			} else if (qs.getStatus() == QuestStatus.REWARD) {
				int var = qs.getQuestVarById(0);
				switch (env.getDialog()) {
				case USE_OBJECT:
					if (var == 10) {
						return sendQuestDialog(env, 5);
					} else if (var == 20) {
						return sendQuestDialog(env, 6);
					}
				case SELECTED_QUEST_NOREWARD:
					//THIS SHIT SHOULDN'T BE HERE CALL #sendQuestEndDialog()
//					if (QuestService.finishQuest(env, var / 10 - 1)) {
//						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//						return true;
//					}
					return sendQuestEndDialog(env, var / 10 - 1);
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			return false;
		}
		
		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		
		if (qs.getStatus() != QuestStatus.START) {
			return false;
		}
		switch (targetId) {
		case 210734:
			if (var >= 1 && var < 10) {
				qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
				updateQuestStatus(env);
				return true;
			} else if (var == 10) {
				//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//				qs.setStatus(QuestStatus.REWARD);
//				updateQuestStatus(env);
//				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//				return true;
				return defaultCloseDialog(env, var, var, true);
			}
		case 210380:
		case 210381:
			if (var >= 11 && var < 20) {
				qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
				updateQuestStatus(env);
				return true;
			} else if (var == 20) {
				//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//				qs.setStatus(QuestStatus.REWARD);
//				updateQuestStatus(env);
//				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//				return true;
				return defaultCloseDialog(env, var, var, true);
			}
		}
		return false;
	}
}

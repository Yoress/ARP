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
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * @author pralinka
 * @modified Yon (Aion Reconstruction Project) -- No longer keeps the quest dialog page open.
 */
public class _14052RestlessSouls extends QuestHandler {
	
	private final static int questId = 14052;
	private final static int[] npc_ids = {204629, 204625, 204628, 204627, 204626, 204622, 700270};
	
	public _14052RestlessSouls() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		for (int npc_id : npc_ids) {
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
		}
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 14050, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			return false;
		}
		
		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		
		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204629) {
				return sendQuestEndDialog(env);
			}
		} else if (qs.getStatus() != QuestStatus.START) {
			return false;
		}
		if (targetId == 204629) {
			switch (env.getDialog()) {
			case QUEST_SELECT:
				if (var == 0) {
					return sendQuestDialog(env, 1011);
				} else if (var == 2) {
					return sendQuestDialog(env, 1693);
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
			case SETPRO2:
				if (var == 1) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, var + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, var, var + 1);
				}
				return false;
			}
		} else if (targetId == 204625) {
			switch (env.getDialog()) {
			case QUEST_SELECT:
				if (var == 1) {
					return sendQuestDialog(env, 1352);
				} else if (var == 2) {
					return sendQuestDialog(env, 1693);
				} else if (var == 4) {
					return sendQuestDialog(env, 2375);
				}
			case CHECK_USER_HAS_QUEST_ITEM:
				if (QuestService.collectItemCheck(env, true)) {
					if (!giveQuestItem(env, 182215344, 1)) {
						return true;
					}
					qs.setQuestVarById(0, var + 1);
					updateQuestStatus(env);
					return sendQuestDialog(env, 10000);
				} else {
					return sendQuestDialog(env, 10001);
				}
			case SETPRO2:
				if (var == 1) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, var + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, var, var + 1);
				}
			case SET_SUCCEED:
				if (var == 4) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setStatus(QuestStatus.REWARD);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, var, var, true);
				}
				return false;
			}
		} else if (targetId == 204628) {
			switch (env.getDialog()) {
			case QUEST_SELECT:
				if (var == 2) {
					return sendQuestDialog(env, 1694);
				}
			case SETPRO3:
				if (var == 2) {
					//TODO: This is odd handling; look to replace with a call to super.
					if (player.getInventory().getItemCountByItemId(182215340) == 0) {
						if (!giveQuestItem(env, 182215340, 1)) {
							return true;
						}
					}
					//THIS SHIT SHOULDN'T BE HERE
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return closeDialogWindow(env);
				}
				return false;
			}
		} else if (targetId == 204627) {
			switch (env.getDialog()) {
			case QUEST_SELECT:
				if (var == 2) {
					return sendQuestDialog(env, 1781);
				}
			case SETPRO3:
				if (var == 2) {
					//TODO: This is odd handling; look to replace with a call to super.
					if (player.getInventory().getItemCountByItemId(182215341) == 0) {
						if (!giveQuestItem(env, 182215341, 1)) {
							return true;
						}
					}
					//THIS SHIT SHOULDN'T BE HERE
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return closeDialogWindow(env);
				}
				return false;
			}
		} else if (targetId == 204626) {
			switch (env.getDialog()) {
			case QUEST_SELECT:
				if (var == 2) {
					return sendQuestDialog(env, 1864);
				}
			case SETPRO3:
				if (var == 2) {
					//TODO: This is odd handling; look to replace with a call to super.
					if (player.getInventory().getItemCountByItemId(182215342) == 0) {
						if (!giveQuestItem(env, 182215342, 1)) {
							return true;
						}
					}
					//THIS SHIT SHOULDN'T BE HERE
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return closeDialogWindow(env);
				}
				return false;
			}
		} else if (targetId == 204622) {
			switch (env.getDialog()) {
			case QUEST_SELECT:
				if (var == 2) {
					return sendQuestDialog(env, 1949);
				}
			case SETPRO3:
				if (var == 2) {
					//TODO: This is odd handling; look to replace with a call to super.
					if (player.getInventory().getItemCountByItemId(182215343) == 0) {
						if (!giveQuestItem(env, 182215343, 1)) {
							return true;
						}
					}
					//THIS SHIT SHOULDN'T BE HERE
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return closeDialogWindow(env);
				}
				return false;
			}
		} else if (targetId == 700270) {
			if (env.getDialog() == DialogAction.USE_OBJECT) {
				return useQuestObject(env, 3, 4, false, 0, 0, 1, 182215344, 1); // 4
			}
		}
		return false;
	}
}

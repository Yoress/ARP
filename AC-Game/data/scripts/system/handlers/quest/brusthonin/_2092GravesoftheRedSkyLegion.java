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
package quest.brusthonin;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * @author Hellboy Aion4Free
 * @modified Gigi
 * @modified Yon (Aion Reconstruction Project) -- No longer keeps the quest dialog page open.
 */
public class _2092GravesoftheRedSkyLegion extends QuestHandler {
	
	private final static int questId = 2092;
	private final static int[] npc_ids = {205150, 205188, 700394, 205190, 205208, 205214, 205213, 205212, 205210, 205209};
	
	public _2092GravesoftheRedSkyLegion() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(214402).addOnKillEvent(questId);
		qe.registerQuestNpc(214403).addOnKillEvent(questId);
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
		return defaultOnLvlUpEvent(env, 2091, true);
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
		case 214402:
		case 214403:
			if (var >= 6 && var < 20) {
				qs.setQuestVarById(0, var + 1);
				updateQuestStatus(env);
				return true;
			} else if (var == 20) {
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
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
			if (targetId == 205150) {
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				} else {
					return sendQuestEndDialog(env);
				}
			}
			return false;
		} else if (qs.getStatus() != QuestStatus.START) {
			return false;
		}
		if (targetId == 205150) {
			switch (env.getDialog()) {
			case QUEST_SELECT:
				if (var == 0) {
					return sendQuestDialog(env, 1011);
				}
			case SELECT_ACTION_1012:
				playQuestMovie(env, 395);
				break;
			case SETPRO1:
				if (var == 0) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, var + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, var, var + 1);
				}
			}
		} else if (targetId == 205188) {
			switch (env.getDialog()) {
			case QUEST_SELECT:
				if (var == 2) {
					return sendQuestDialog(env, 1693);
				}
			case SETPRO3:
				if (var == 2) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, var + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, var, var + 1);
				}
			}
		} else if (targetId == 205190) {
			switch (env.getDialog()) {
			case QUEST_SELECT:
				if (var == 3) {
					return sendQuestDialog(env, 2034);
				}
				if (var == 4) {
					return sendQuestDialog(env, 2375);
				}
			case SETPRO4:
				if (var == 3) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, var + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, var, var + 1);
				}
			case SETPRO5:
				if (var == 4) {
					//THIS SHIT SHOULDN'T BE HERE
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return closeDialogWindow(env);
				}
			case CHECK_USER_HAS_QUEST_ITEM:
				if (var == 4) {
					if (QuestService.collectItemCheck(env, true)) {
						if (!giveQuestItem(env, 182209009, 1)) {
							return true;
						}
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						return sendQuestDialog(env, 10000);
					} else {
						return sendQuestDialog(env, 10001);
					}
				}
			}
		} else if (targetId == 205208) {
			switch (env.getDialog()) {
			case QUEST_SELECT:
				if (var == 5) {
					return sendQuestDialog(env, 2717);
				}
			case SETPRO6:
				if (var == 5) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, var + 1);
//					updateQuestStatus(env);
//					removeQuestItem(env, 182209009, 1);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, var, var + 1, 0, 0, 182209009, 1);
				}
			}
		} else if (targetId == 205209) {
			switch (env.getDialog()) {
			case QUEST_SELECT:
				if (var == 5) {
					return sendQuestDialog(env, 2802);
				}
			case SETPRO6:
				if (var == 5) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, var + 1);
//					updateQuestStatus(env);
//					removeQuestItem(env, 182209009, 1);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, var, var + 1, 0, 0, 182209009, 1);
				}
			}
		} else if (targetId == 205210) {
			switch (env.getDialog()) {
			case QUEST_SELECT:
				if (var == 5) {
					return sendQuestDialog(env, 2887);
				}
			case SETPRO6:
				if (var == 5) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, var + 1);
//					updateQuestStatus(env);
//					removeQuestItem(env, 182209009, 1);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, var, var + 1, 0, 0, 182209009, 1);
				}
			}
		} else if (targetId == 205212) {
			switch (env.getDialog()) {
			case QUEST_SELECT:
				if (var == 5) {
					return sendQuestDialog(env, 2972);
				}
			case SETPRO6:
				if (var == 5) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, var + 1);
//					updateQuestStatus(env);
//					removeQuestItem(env, 182209009, 1);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, var, var + 1, 0, 0, 182209009, 1);
				}
			}
		} else if (targetId == 205213) {
			switch (env.getDialog()) {
			case QUEST_SELECT:
				if (var == 5) {
					return sendQuestDialog(env, 3058);
				}
			case SETPRO6:
				if (var == 5) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, var + 1);
//					updateQuestStatus(env);
//					removeQuestItem(env, 182209009, 1);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, var, var + 1, 0, 0, 182209009, 1);
				}
			}
		} else if (targetId == 205214) {
			switch (env.getDialog()) {
			case QUEST_SELECT:
				if (var == 5) {
					return sendQuestDialog(env, 3143);
				}
			case SETPRO6:
				if (var == 5) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, var + 1);
//					updateQuestStatus(env);
//					removeQuestItem(env, 182209009, 1);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, var, var + 1, 0, 0, 182209009, 1);
				}
			}
		} else if (targetId == 700394) {
			switch (env.getDialog()) {
			case USE_OBJECT:
				return useQuestObject(env, 1, 2, false, 0); // 2
			}
		}
		return false;
	}
}

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
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Rhys2002
 * @modified Yon (Aion Reconstruction Project) -- No longer keeps the quest dialog page open.
 */
public class _1057CreatingaMonster extends QuestHandler {
	
	private final static int questId = 1057;
	private final static int[] npc_ids = {204502, 204619, 700218, 700279, 204500};
	
	public _1057CreatingaMonster() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterWorld(questId);
		qe.registerQuestNpc(700219).addOnKillEvent(questId);
		qe.registerQuestNpc(212211).addOnKillEvent(questId);
		for (int npc_id : npc_ids) {
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
		}
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env, 1056);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] quests = {1500, 1056};
		return defaultOnLvlUpEvent(env, quests, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			return false;
		}
		
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		
		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204500) {
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				} else {
					int[] questItems = {182201616};
					return sendQuestEndDialog(env, questItems);
				}
			}
		} else if (qs.getStatus() != QuestStatus.START) {
			return false;
		}
		if (targetId == 204502) {
			switch (env.getDialog()) {
			case QUEST_SELECT:
				if (var == 0) {
					return sendQuestDialog(env, 1011);
				} else if (var == 3) {
					return sendQuestDialog(env, 2034);
				}
			case SELECT_ACTION_2036:
				if (var == 3) {
					playQuestMovie(env, 190);
				}
				return false;
			case SETPRO1:
				if (var == 0) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, var + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, var, var + 1);
				}
			case SETPRO4:
				if (var == 3) {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					removeQuestItem(env, 182201616, 1);
//					qs.setQuestVarById(0, var + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, var, var + 1, 0, 0, 182201616, 1);
				}
				return false;
			}
		} else if (targetId == 204619) {
			switch (env.getDialog()) {
			case QUEST_SELECT:
				if (var == 1) {
					return sendQuestDialog(env, 1352);
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
		} else if (targetId == 700218 && qs.getQuestVarById(0) == 2) {
			if (env.getDialog() == DialogAction.USE_OBJECT) {
				return sendQuestDialog(env, 1693);
			} else if (env.getDialog() == DialogAction.SETPRO3) {
				if (!giveQuestItem(env, 182201616, 1)) {
					return false;
				}
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
				changeQuestStep(env, 2, 3, false); // 3
				return true;
			}
		} else if (targetId == 700279 && qs.getQuestVarById(0) == 9) {
			if (env.getDialog() == DialogAction.USE_OBJECT) {
				return useQuestObject(env, 9, 9, true, false); // reward
			}
		}
		return false;
	}
	
	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (player.getWorldId() == 310050000 && qs.getQuestVarById(0) == 4) {
				qs.setQuestVar(5);
				updateQuestStatus(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START) {
			return false;
		}
		
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		
		if (targetId == 700219 && qs.getQuestVarById(0) < 8) {
			qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
			updateQuestStatus(env);
		} else if (targetId == 212211 && qs.getQuestVarById(0) == 8) {
			qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
			updateQuestStatus(env);
		}
		return false;
	}
}

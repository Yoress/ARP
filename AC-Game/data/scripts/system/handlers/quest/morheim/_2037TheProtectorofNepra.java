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
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author Hellboy aion4Free
 * @modified Yon (Aion Reconstruction Project) -- No longer keeps the quest dialog page open.
 */
public class _2037TheProtectorofNepra extends QuestHandler {
	
	private final static int questId = 2037;
	private final static int[] npc_ids = {204369, 204361, 278004};
	
	public _2037TheProtectorofNepra() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(212861).addOnKillEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("ALTAR_OF_THE_BLACK_DRAGON_220020000"), questId);
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
		return defaultOnLvlUpEvent(env, 2300, true);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
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
		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
			case 204369: {
				switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 0) {
						return sendQuestDialog(env, 1011);
					}
				case SELECT_ACTION_1012:
					playQuestMovie(env, 80);
					break;
				case SETPRO1:
					if (var == 0) {
						//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//						qs.setQuestVarById(0, var + 1);
//						updateQuestStatus(env);
//						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//						return true;
						return defaultCloseDialog(env, var, var + 1);
					}
				}
			}
			case 204361: {
				switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 1) {
						return sendQuestDialog(env, 1352);
					} else if (var == 3 && (player.getInventory().getItemCountByItemId(182204015) == 1)) {
						return sendQuestDialog(env, 2034);
					} else if (var == 5) {
						return sendQuestDialog(env, 2716);
					} else if (var == 7) {
						return sendQuestDialog(env, 3057);
					}
				case SETPRO2:
					if (var == 1) {
						//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//						qs.setQuestVarById(0, var + 1);
//						updateQuestStatus(env);
//						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//						return true;
						return defaultCloseDialog(env, var, var + 1);
					}
				case SETPRO4:
					if (var == 3) {
						//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//						removeQuestItem(env, 182204015, 1);
//						qs.setQuestVarById(0, var + 1);
//						updateQuestStatus(env);
//						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//						return true;
						return defaultCloseDialog(env, var, var + 1, 0, 0, 182204015, 1);
					}
				case SETPRO6:
					if (var == 5) {
						//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//						qs.setQuestVarById(0, var + 1);
//						updateQuestStatus(env);
//						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//						return true;
						return defaultCloseDialog(env, var, var + 1);
					}
				case SET_SUCCEED:
					if (var == 7) {
						//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//						qs.setStatus(QuestStatus.REWARD);
//						updateQuestStatus(env);
//						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//						return true;
						return defaultCloseDialog(env, var, var, true);
					}
				}
			}
			case 278004: {
				switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 2) {
						return sendQuestDialog(env, 1693);
					}
				case SETPRO3:
					if (var == 2) {
						//TODO: This is odd handling; look to replace with a call to super.
						if (!giveQuestItem(env, 182204015, 1)) {
							return true;
						}
						//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//						qs.setQuestVarById(0, var + 1);
//						updateQuestStatus(env);
//						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//						return true;
						return defaultCloseDialog(env, var, var + 1);
					}
				}
			}
			
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204369) {
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				} else {
					return sendQuestEndDialog(env);
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
		case 212861:
			if (var == 6) {
				qs.setQuestVarById(0, var + 1);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
		Player player = env.getPlayer();
		if (player == null) {
			return false;
		}
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (zoneName != ZoneName.get("ALTAR_OF_THE_BLACK_DRAGON_220020000")) {
			return false;
		}
		if (qs != null && qs.getQuestVarById(0) == 4) {
			env.setQuestId(questId);
			qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
			updateQuestStatus(env);
			playQuestMovie(env, 81);
			return true;
		}
		return false;
	}
}

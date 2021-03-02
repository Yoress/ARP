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
package quest.poeta;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author MrPoke
 * @modified Yon (Aion Reconstruction Project) -- No longer keeps the quest dialog page open.
 */
public class _1001TheKerubThreat extends QuestHandler {
	
	private final static int questId = 1001;
	
	public _1001TheKerubThreat() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(210670).addOnKillEvent(questId);
		qe.registerQuestNpc(203071).addOnTalkEvent(questId);
		qe.registerQuestNpc(203067).addOnTalkEvent(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
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
		if (targetId == 210670) {
			if (var > 0 && var < 6) {
				qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1100, true);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
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
		
		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 203071) {
				switch (env.getDialog()) {
				case SELECT_ACTION_1012:
					playQuestMovie(env, 15);
					return false;
				case QUEST_SELECT:
					if (var == 0) {
						return sendQuestDialog(env, 1011);
					} else if (var == 6) {
						return sendQuestDialog(env, 1352);
					} else if (var == 7) {
						return sendQuestDialog(env, 1693);
					}
					return false;
				case SETPRO3:
				case CHECK_USER_HAS_QUEST_ITEM:
					if (var == 7) {
						long itemCount = player.getInventory().getItemCountByItemId(182200001);
						if (itemCount >= 3) {
							if (env.getDialogId() == DialogAction.CHECK_USER_HAS_QUEST_ITEM.id()) {
								return sendQuestDialog(env, 1694);
							} else {
								//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//								removeQuestItem(env, 182200001, itemCount);
//								qs.setQuestVarById(0, var + 1);
//								qs.setStatus(QuestStatus.REWARD);
//								updateQuestStatus(env);
//								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//								return true;
								//Verify itemCount is not going to overflow if cast to an int -- shouldn't even be possible.
								if (Long.compare(itemCount, Integer.MAX_VALUE) <= 0) {
									return defaultCloseDialog(env, var, var + 1, true, 0, 0, 182200001, (int) itemCount);
								} else {
									removeQuestItem(env, 182200001, itemCount);
									return defaultCloseDialog(env, var, var + 1, true);
								}
							}
						} else {
							return sendQuestDialog(env, 1779);
						}
					}
//					return true; //FIX-ME: This violates the contract of this method. Why is it here?
					return false;
				case SETPRO1:
				case SETPRO2:
					if (var == 0 || var == 6) {
						//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//						qs.setQuestVarById(0, var + 1);
//						updateQuestStatus(env);
//						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return defaultCloseDialog(env, var, var + 1);
					}
//					return true; //FIX-ME: This violates the contract of this method. Why is it here?
					return false;
				default:
					return false;
				}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203067) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}

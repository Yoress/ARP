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
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author Hellboy aion4Free
 * @modified Yon (Aion Reconstruction Project) -- Changed step 13 to step 9 to allow for completion of the quest with 4.7.5.18 client;
 * No longer keeps the quest dialog page open.
 */
public class _2061SuppressingtheBakarmaLegion extends QuestHandler {
	
	private final static int questId = 2061;
	private final static int[] npc_ids = {204702, 278001, 204807, 700295, 204052};
	
	public _2061SuppressingtheBakarmaLegion() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnEnterZone(ZoneName.get("MALEK_MINE_220040000"), questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(700290).addOnKillEvent(questId);
		qe.registerQuestNpc(214026).addOnKillEvent(questId);
		for (int npc_id : npc_ids) {
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
		}
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		int[] quests = {2051, 2052, 2053, 2054, 2055, 2056, 2057, 2058, 2059, 2060};
		return defaultOnZoneMissionEndEvent(env, quests);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] quests = {2500, 2051, 2052, 2053, 2054, 2055, 2056, 2057, 2058, 2059, 2060};
		return defaultOnLvlUpEvent(env, quests, true);
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
		switch (targetId) {
		case 700290:
			if (qs.getQuestVarById(0) >= 5 && qs.getQuestVarById(0) < 8) {
				qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
				updateQuestStatus(env);
			}
			break;
		case 214026:
			if (qs.getQuestVarById(0) == 8) {
				qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
				updateQuestStatus(env);
			}
			break;
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
			if (targetId == 204052) {
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
		
		if (targetId == 204702) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 0) {
						return sendQuestDialog(env, 1011);
					} else if (var == 2) {
						return sendQuestDialog(env, 1693);
					} else if (var == 9) { //Formerly var == 13, quest updates at 4/8 towers, so 4 of this value are missing.
						return sendQuestDialog(env, 2375);
					}
				case SELECT_ACTION_1694:
					playQuestMovie(env, 255);
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
				case SETPRO3:
					if (var == 2) {
						//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//						qs.setQuestVarById(0, var + 1);
//						updateQuestStatus(env);
//						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//						return true;
						return defaultCloseDialog(env, var, var + 1);
					}
				case SET_SUCCEED:
					if (var == 9) {
//						qs.setStatus(QuestStatus.REWARD);
//						updateQuestStatus(env);
//						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//						return true;
						return defaultCloseDialog(env, var, var, true);
					}
				default: break;
			}
		} else if (targetId == 278001) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 1) {
						return sendQuestDialog(env, 1352);
					}
				case SETPRO2:
					if (var == 1) {
						//TODO: This handling is odd; look to replace it with a call to super.
						if (!giveQuestItem(env, 182204320, 1)) {
							return true;
						}
						//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//						qs.setQuestVarById(0, var + 1);
//						updateQuestStatus(env);
//						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//						return true;
						return defaultCloseDialog(env, var, var + 1);
					}
				default: break;
			}
		} else if (targetId == 204807) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 3) {
						return sendQuestDialog(env, 2034);
					}
				case SETPRO4:
					if (var == 3) {
						//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//						qs.setQuestVarById(0, var + 1);
//						updateQuestStatus(env);
//						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//						return true;
						return defaultCloseDialog(env, var, var + 1);
					}
				default: break;
			}
		} else if (targetId == 700295 && var >= 4 && var < 8) {
			if (env.getDialog() == DialogAction.USE_OBJECT) {
				if (var == 7) {
					removeQuestItem(env, 182204320, 1);
				}
				changeQuestStep(env, var, var + 1, false);
				Creature creature = (Creature) player.getTarget();
				creature.getController().onAttack(player, creature.getLifeStats().getMaxHp() + 1, true, null);
			}
		}
		return false;
	}
}

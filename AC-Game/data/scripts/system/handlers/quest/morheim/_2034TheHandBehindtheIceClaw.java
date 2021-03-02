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
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author Rhys2002
 * @reworked vlog
 */
public class _2034TheHandBehindtheIceClaw extends QuestHandler {
	
	private final static int questId = 2034;
	
	public _2034TheHandBehindtheIceClaw() {
		super(questId);
	}
	
	@Override
	public void register() {
		int[] npcs = {204303, 204332, 700246, 204301};
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(204417).addOnKillEvent(questId);
		qe.registerQuestNpc(212877).addOnKillEvent(questId);
		qe.registerQuestItem(182204008, questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		if (qs == null) {
			return false;
		}
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		
		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
			case 204303: { // Nina
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 0) {
						return sendQuestDialog(env, 1011);
					} else if (var == 5) {
						return sendQuestDialog(env, 2716);
					}
				}
				case SETPRO1: {
					return defaultCloseDialog(env, 0, 1); // 1
				}
				case SET_SUCCEED: {
					return defaultCloseDialog(env, 5, 5, true, false); // reward
				}
				}
				break;
			}
			case 204332: { // Jorund
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 1) {
						return sendQuestDialog(env, 1352);
					} else if (var == 2) {
						if (player.getInventory().getItemCountByItemId(182204008) == 0) {
							return sendQuestDialog(env, 1694);
						} else {
							return sendQuestDialog(env, 1693);
						}
					} else if (var == 3) {
						return sendQuestDialog(env, 2034);
					}
				}
				case SETPRO2: {
					if (var == 1) {
						return defaultCloseDialog(env, 1, 2, 182204008, 1, 0, 0); // 2
					} else if (var == 2) {
						return defaultCloseDialog(env, 2, 2, 182204008, 1, 0, 0); // 2
					}
				}
				case SETPRO4: {
					player.getTitleList().addTitle(58, true, 0);
					return defaultCloseDialog(env, 3, 4); // 4
				}
				}
				break;
			}
			case 700246: { // Dead Fire
				if (dialog == DialogAction.USE_OBJECT) {
					if (var == 2) {
						if (player.getInventory().getItemCountByItemId(182204019) > 0) {
							final Npc npc = (Npc) env.getVisibleObject();
							QuestService.addNewSpawn(220020000, 1, 204417, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
							removeQuestItem(env, 182204008, 1);
							removeQuestItem(env, 182204019, 1);
						}
					}
				}
			}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204301) { // Aegir
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
	
	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		Player player = env.getPlayer();
		if (player.isInsideZone(ZoneName.get("ALTAR_OF_TRIAL_220020000"))) {
			return HandlerResult.fromBoolean(useQuestItem(env, item, 2, 2, false));
		}
		return HandlerResult.FAILED;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int targetId = env.getTargetId();
			switch (targetId) {
			case 204417: {
				return defaultOnKillEvent(env, 204417, 2, 3); // 3
			}
			case 212877: {
				return defaultOnKillEvent(env, 212877, 4, 5); // 5
			}
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
		return defaultOnLvlUpEvent(env, 2300, true);
	}
}

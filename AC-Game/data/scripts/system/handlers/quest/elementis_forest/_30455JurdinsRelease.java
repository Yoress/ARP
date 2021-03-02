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
package quest.elementis_forest;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 *
 * @author Ritsu
 */
public class _30455JurdinsRelease extends QuestHandler {
	
	private static final int questId = 30455;
	
	public _30455JurdinsRelease() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(205492).addOnQuestStart(questId);
		qe.registerQuestNpc(205492).addOnTalkEvent(questId);
		qe.registerQuestNpc(282203).addOnTalkEvent(questId);
		qe.registerQuestNpc(282204).addOnTalkEvent(questId);
		qe.registerQuestNpc(217249).addOnKillEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 205492) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
			case 282203: {
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 0) {
						return sendQuestDialog(env, 1011);
					}
				}
				case SETPRO1: {
					final Npc npc = (Npc) env.getVisibleObject();
					npc.getController().scheduleRespawn();
					npc.getController().onDelete();
					QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 217249, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
					return defaultCloseDialog(env, 0, 1);
				}
				}
			}
			
			case 282204: {
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 2) {
						return sendQuestDialog(env, 1693);
					}
				}
				case SET_SUCCEED: {
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return closeDialogWindow(env);
				}
				}
			}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 205492) {
				switch (dialog) {
				case USE_OBJECT: {
					return sendQuestDialog(env, 10002);
				}
				default: {
					return sendQuestEndDialog(env);
				}
				}
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
		
		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		
		switch (targetId) {
		case 217249:
			if (qs.getQuestVarById(0) == 1) {
				Npc npc = (Npc) env.getVisibleObject();
				qs.setQuestVarById(0, var + 1);
				updateQuestStatus(env);
				QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 282204, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
			}
		}
		return false;
	}
}

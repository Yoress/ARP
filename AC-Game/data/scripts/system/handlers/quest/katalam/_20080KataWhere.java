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
package quest.katalam;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.teleport.TeleportService2;

/**
 * @author Cheatkiller
 */
public class _20080KataWhere extends QuestHandler {
	
	private final static int questId = 20080;
	
	public _20080KataWhere() {
		super(questId);
	}
	
	@Override
	public void register() {
		int[] npcIds = {800170, 205625, 800528, 800529};
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterWorld(questId);
		for (int npcId : npcIds) {
			qe.registerQuestNpc(npcId).addOnTalkEvent(questId);
		}
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		DialogAction dialog = env.getDialog();
		
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 800170) {
				switch (dialog) {
				case QUEST_SELECT: {
					if (qs.getQuestVarById(0) == 0) {
						return sendQuestDialog(env, 1011);
					}
				}
				case SETPRO1: {
					return defaultCloseDialog(env, 0, 1);
				}
				}
			} else if (targetId == 205625) {
				switch (dialog) {
				case QUEST_SELECT: {
					if (qs.getQuestVarById(0) == 1) {
						return sendQuestDialog(env, 1352);
					}
				}
				case SETPRO2: {
					TeleportService2.teleportTo(player, 600050000, player.getInstanceId(), 173, 546, 250.31f, (byte) 102, TeleportAnimation.BEAM_ANIMATION);
					return defaultCloseDialog(env, 1, 2);
				}
				}
			} else if (targetId == 800528) {
				switch (dialog) {
				case QUEST_SELECT: {
					if (qs.getQuestVarById(0) == 3) {
						return sendQuestDialog(env, 2034);
					}
				}
				case SET_SUCCEED: {
					return defaultCloseDialog(env, 3, 3, true, false);
				}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 800529) {
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
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (player.getWorldId() == 600050000) {
				int var = qs.getQuestVarById(0);
				if (var == 2) {
					playQuestMovie(env, 822);
					changeQuestStep(env, 2, 3, false);
					return true;
				}
			}
		}
		return false;
	}
}

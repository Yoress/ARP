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
package quest;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * This is a template for everyone, who will write a new quest handler<br>
 * It's only example. All IDs should be changed to the from your quest<br>
 * And use only needed events<br>
 * The main things are the structure and using default methods from the QuestHandler class<br>
 * - First the quest status<br>
 * - Then the target ID<br>
 * - Then DialogAction
 *
 * @author vlog
 * @modified Yon (Aion Reconstruction Project) -- Left a note about not keeping the Npc dialogue open.
 */
public class _9999QuestHandlerTemplate extends QuestHandler {
	
	private static final int questId = 9999;
	
	public _9999QuestHandlerTemplate() {
		super(questId);
	}
	
	@Override
	public void register() {
		// register needed events here
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();
		
		// If this is a mission, the qs should be != null and you will not need this
		if (qs == null || qs.canRepeat()) {
			if (targetId == 000000) { // Viktor Logwin
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1011); // can be different
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
				case 111111: { // Oliver
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1352);
							} else if (var == 4) {
								return sendQuestDialog(env, 1693);
							} else if (var == 5) {
								return sendQuestDialog(env, 2034);
							}
						}
						case SELECT_ACTION_1353: {
							playQuestMovie(env, 0);
							return sendQuestDialog(env, 1353);
						}
						case SETPRO1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
						case SETPRO2: {
							return defaultCloseDialog(env, 4, 5); // 5
						}
						case CHECK_USER_HAS_QUEST_ITEM: {
							return checkQuestItems(env, 5, 6, true, 2375, 10001);
						}
						case FINISH_DIALOG: {
							//DO NOT send quest selection unless DialogAction is USE_OBJECT, or you are adding a failsafe.
							//This case shouldn't be possible, as the client sends CM_CLOSE_DIALOG, but if we do trigger it,
							//Then the intention is to close the dialog, not reset it to the quest selection page.
//							return sendQuestSelectionDialog(env);
							return defaultCloseDialog(env, var, var); //If var is unavailable because qs is null, use closeDialogWindow(QuestEnv).
						}
					}
				}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 000000) { // Viktor Logwin
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 20001);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		return defaultOnKillEvent(env, 010101, 1, 3); // 1 - 3
	}
	
	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (player.isInsideZone(ZoneName.get("DF1A_ITEMUSEAREA_Q2016"))) { // example zone
				return HandlerResult.fromBoolean(useQuestItem(env, item, 3, 4, false));
			}
		}
		return HandlerResult.FAILED;
	}
}

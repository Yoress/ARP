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
package quest.inggison;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author zhkchi
 */
public class _11077AWeaponOfWorth extends QuestHandler {
	
	private final static int questId = 11077;
	
	public _11077AWeaponOfWorth() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(798926).addOnQuestStart(questId); // Outremus
		qe.registerQuestNpc(798926).addOnTalkEvent(questId); // Outremus
		qe.registerQuestNpc(799028).addOnTalkEvent(questId); // Brontes
		qe.registerQuestNpc(798918).addOnTalkEvent(questId); // Pilipides
		qe.registerQuestNpc(798903).addOnTalkEvent(questId); // Drenia
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798926) {
				switch (dialog) {
				case QUEST_SELECT:
					return sendQuestDialog(env, 1011);
				default: {
					if (!giveQuestItem(env, 182214016, 1)) {
						updateQuestStatus(env);
					}
					return sendQuestStartDialog(env);
				}
				}
			}
		} else if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
			case 799028: // Brontes
			{
				switch (dialog) {
				case QUEST_SELECT: {
					return sendQuestDialog(env, 1353);
				}
				case SELECT_ACTION_1353: {
					return sendQuestDialog(env, 1353);
				}
				case SETPRO1: {
					return defaultCloseDialog(env, 0, 1);
				}
				}
			}
			case 798918: // Pilipides
			{
				switch (dialog) {
				case QUEST_SELECT: {
					return sendQuestDialog(env, 1693);
				}
				case SELECT_ACTION_1694: {
					return sendQuestDialog(env, 1694);
				}
				case SETPRO2: {
					return defaultCloseDialog(env, 1, 2);
				}
				}
			}
			case 798903: // Drenia
			{
				switch (dialog) {
				case QUEST_SELECT: {
					return sendQuestDialog(env, 2375);
				}
				case SELECT_QUEST_REWARD: {
					return defaultCloseDialog(env, 2, 3, true, true);
				}
				}
			}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798903) // Drenia
			{
				switch (env.getDialogId()) {
				case 1009: {
					return sendQuestDialog(env, 5);
				}
				default:
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}

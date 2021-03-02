/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 * Aion-Lightning is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your option) any later version.
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
package quest.altgard;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @Author Majka Ajural
 * @modified Yon (Aion Reconstruction Project) -- No longer misuses the sendQuestSelectionDialog(QuestEnv) method.
 */
public class _24112NoLaissezFaireForLepharists extends QuestHandler {
	
	private final static int questId = 24112;
	private final static int questStartNpcId = 203631; // Nokir
	private final static int questEndNpcId = 832821; // Brodir
	private final static int questKillNpcId = 210510; // Comrade Sumarhon
	
	public _24112NoLaissezFaireForLepharists() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(questStartNpcId).addOnQuestStart(questId);
		qe.registerQuestNpc(questStartNpcId).addOnTalkEvent(questId);
		qe.registerQuestNpc(questEndNpcId).addOnTalkEvent(questId);
		qe.registerQuestNpc(questKillNpcId).addOnKillEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		
		if (targetId == questStartNpcId) { // Nokir
			if (qs == null || qs.getStatus() == QuestStatus.NONE) {
				switch (dialog) {
				case QUEST_SELECT:
					return sendQuestDialog(env, 1011);
				default:
					return sendQuestStartDialog(env);
				}
			}
		}
		
		if (targetId == questEndNpcId) { // Brodir
			
			if (qs.getStatus() == QuestStatus.START) {
				int var = qs.getQuestVarById(0);
				
				switch (dialog) {
				case QUEST_SELECT:
					if (var == 0) { // @ToDo: find a way to reach this part. It works with var = -1 but doesn't activate following step
						return sendQuestDialog(env, 1352);
					} else if (var == 1) {
						return sendQuestDialog(env, 2375);
					}
				case SETPRO1:
//					qs.setQuestVarById(0, 1);
					//Don't call this here; in general, the only valid place to call this is when the DialogAction is USE_OBJECT
//					return sendQuestSelectionDialog(env);
					return defaultCloseDialog(env, var, 1);
				case SELECT_QUEST_REWARD:
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return sendQuestEndDialog(env);
				}
			}
			
			if (qs.getStatus() == QuestStatus.REWARD) {
				return sendQuestEndDialog(env);
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
		
		int targetId = env.getTargetId();
		int var = qs.getQuestVarById(0);
		
		if (var == 0 && targetId == questKillNpcId) {
			// qs.setStatus(QuestStatus.REWARD);
			qs.setQuestVarById(0, 1);
			updateQuestStatus(env);
			return true;
		}
		return false;
	}
}

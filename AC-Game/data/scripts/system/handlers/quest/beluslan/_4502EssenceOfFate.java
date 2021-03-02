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
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * Quest Starter: Hresvelgr (204837). Go to Dark Poeta and find the Balaur Operation Order (730192). Destroy the Telepathy Controller (214894) (1).
 * Destroy power generators to close the Balaur Abyss Gate: Main Power Generator (214895) (1), Auxiliary Power Generator (214896) (1), Emergency
 * Generator (214897) (1). Get rid of Brigade General Anuhart (214904), and take the Concentrated Vitality (182204534) to Heimdall (204182).
 *
 * @author vlog
 * @modified Yon (Aion Reconstruction Project) -- No longer misuses the sendQuestSelectionDialog(QuestEnv) method.
 */
public class _4502EssenceOfFate extends QuestHandler {
	
	private final static int questId = 4502;
	private final static int[] npcs = {204837, 730192, 204182};
	private final static int[] mobs = {214894, 214895, 214896, 214897, 214904};
	
	public _4502EssenceOfFate() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(204837).addOnQuestStart(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
		for (int mob : mobs) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		Npc npc = (Npc) env.getVisibleObject();
		int targetId = npc.getNpcId();
		DialogAction dialog = env.getDialog();
		
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204837) { // Hresvelgr
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
			case 730192: { // Balaur Operation Orders
				if (var == 0) {
					if (dialog == DialogAction.USE_OBJECT) {
						return sendQuestDialog(env, 1011);
					} else {
						changeQuestStep(env, 0, 1, false); // 1
						return sendQuestDialog(env, 0);
					}
				}
				break;
			}
			case 204182: { // Heimdall
				switch (dialog) {
				case QUEST_SELECT: {
					if (var == 2) {
						return sendQuestDialog(env, 1352);
					}
				}
				case CHECK_USER_HAS_QUEST_ITEM: {
					return checkQuestItems(env, 2, 2, true, 5, 10001); // reward
				}
				case FINISH_DIALOG: {
					//It shouldn't be possible to trigger this case, but if we do the dialog should close, not reset to quest selection.
//					return sendQuestSelectionDialog(env);
					return defaultCloseDialog(env, var, var);
				}
				}
			}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204182) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		Npc npc = (Npc) env.getVisibleObject();
		int targetId = npc.getNpcId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			int var1 = qs.getQuestVarById(1);
			int var2 = qs.getQuestVarById(2);
			int var3 = qs.getQuestVarById(3);
			
			switch (targetId) {
			case 214894: { // Telepathy Controller
				if (var == 1) {
					return defaultOnKillEvent(env, 214894, 1, 2, 0); // 2
				}
				break;
			}
			case 214895: { // Main Power Generator
				if (var == 2 && var1 != 1) {
					defaultOnKillEvent(env, 214895, 0, 1, 1); // 1: 1
					return true;
				}
				break;
			}
			case 214896: { // Auxiliary Power Generator
				if (var == 2 && var2 != 1) {
					defaultOnKillEvent(env, 214896, 0, 1, 2); // 2: 1
					return true;
				}
				break;
			}
			case 214897: { // Emergency Generator
				if (var == 2 && var3 != 1) {
					defaultOnKillEvent(env, 214897, 0, 1, 3); // 3: 1
					return true;
				}
				break;
			}
			}
		}
		return false;
	}
}

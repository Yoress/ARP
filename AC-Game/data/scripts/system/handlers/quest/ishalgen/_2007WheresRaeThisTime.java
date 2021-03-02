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
package quest.ishalgen;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author Mr. Poke
 * @modified apozema
 * @modified Yon (Aion Reconstruction Project) -- No longer misuses the sendQuestSelectionDialog(QuestEnv) method.
 */
public class _2007WheresRaeThisTime extends QuestHandler {
	
	private final static int questId = 2007;
	
	public _2007WheresRaeThisTime() {
		super(questId);
	}
	
	@Override
	public void register() {
		int[] talkNpcs = {203516, 203519, 203539, 203552, 203554, 700085, 700086, 700087};
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		for (int id : talkNpcs) {
			qe.registerQuestNpc(id).addOnTalkEvent(questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
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
			switch (targetId) {
			case 203516: // Ulgorn
				switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 0) {
						return sendQuestDialog(env, 1011);
					}
				case SETPRO1:
					if (var == 0) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						closeDialogWindow(env);
						return true;
					}
				default:
					break;
				}
				break;
			case 203519: // Nobekk
				switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 1) {
						return sendQuestDialog(env, 1352);
					}
				case SETPRO2:
					if (var == 1) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						closeDialogWindow(env);
						return true;
					}
				default:
					break;
				}
				break;
			case 203539: // Derot
				switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 2) {
						return sendQuestDialog(env, 1693);
					}
				case SELECT_ACTION_1694:
					playQuestMovie(env, 55);
					break;
				case SETPRO3:
					if (var == 2) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						closeDialogWindow(env);
						return true;
					}
				default:
					break;
				}
				break;
			case 203552: // Nalto
				switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 3) {
						return sendQuestDialog(env, 2034);
					}
				case SETPRO4:
					if (var == 3) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						closeDialogWindow(env);
						return true;
					}
				default:
					break;
				}
				break;
			case 203554: // Rae
				switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 4) {
						return sendQuestDialog(env, 2375);
					} else if (var == 8) {
						return sendQuestDialog(env, 2716);
					}
				case SETPRO5:
					if (var == 4) {
//						qs.setQuestVarById(0, var + 1);
//						updateQuestStatus(env);
						//Don't call this here; in general, the only valid place to call this is when the DialogAction is USE_OBJECT
//						sendQuestSelectionDialog(env);
//						return true;
						return defaultCloseDialog(env, var, var + 1);
					}
					break;
				case SETPRO6:
					if (var == 8) {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						TeleportService2.teleportTo(env.getPlayer(), 220010000, 590.01886f, 2453.0552f, 278.375f, (byte) 86, TeleportAnimation.BEAM_ANIMATION);
						return true;
					}
				default:
					break;
				}
				break;
			case 700085: // Green Power Generator
				if (var == 5) {
					destroy(6, env);
					return false;
				}
				break;
			case 700086: // Blue Power Generator
				if (var == 6) {
					destroy(7, env);
					return false;
				}
				break;
			case 700087: // Violet Power Generator
				if (var == 7) {
					destroy(8, env);
					return false;
				}
				break;
			}
			
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203516) { // Ulgorn
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					playQuestMovie(env, 58);
					return sendQuestDialog(env, 3057);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		int[] quests = {2006, 2005, 2004, 2003, 2002, 2001};
		return defaultOnZoneMissionEndEvent(env, quests);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] quests = {2100, 2006, 2005, 2004, 2003, 2002, 2001};
		return defaultOnLvlUpEvent(env, quests, true);
	}
	
	private void destroy(final int var, final QuestEnv env) { //TODO: add Emotion for destroying generators
		final int targetObjectId = env.getVisibleObject().getObjectId();
		final Player player = env.getPlayer();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			
			@Override
			public void run() {
				if (player.getTarget().getObjectId() != targetObjectId) {
					return;
				}
				QuestState qs = player.getQuestStateList().getQuestState(questId);
				switch (var) {
				case 6:
				case 7:
					qs.setQuestVar(var);
					break;
				case 8:
					
					qs.setQuestVar(var);
					playQuestMovie(env, 56);
					break;
				}
				updateQuestStatus(env);
			}
		}, 100);
	}
}

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
package quest.sanctum;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author edynamic90
 * @modified Yon (Aion Reconstruction Project) -- No longer keeps the quest dialog page open.
 */
public class _1901KrallicPotion extends QuestHandler {
	
	private final static int questId = 1901;
	
	public _1901KrallicPotion() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(203830).addOnQuestStart(questId);
		qe.registerQuestNpc(203830).addOnTalkEvent(questId);
		qe.registerQuestNpc(798026).addOnTalkEvent(questId);
		qe.registerQuestNpc(798025).addOnTalkEvent(questId);
		qe.registerQuestNpc(203131).addOnTalkEvent(questId);
		qe.registerQuestNpc(798003).addOnTalkEvent(questId);
		qe.registerQuestNpc(203864).addOnTalkEvent(questId);
		/* qe.setQuestItemIds(182204115).add(questId); */
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (targetId == 203830)// Marmeia
		{
			if (env.getDialog() == DialogAction.QUEST_SELECT) {
				return sendQuestDialog(env, 1011);
			} else {
				return sendQuestStartDialog(env);
			}
		} else {
			if (targetId == 203864) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 2375);
				} else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id() && qs.getStatus() != QuestStatus.COMPLETE && qs.getStatus() != QuestStatus.NONE) {
					qs.setQuestVar(7);
					updateQuestStatus(env);
					qs.setStatus(QuestStatus.REWARD);
					return sendQuestEndDialog(env);
				} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
					if (env.getDialog() == DialogAction.USE_OBJECT) {
						return sendQuestDialog(env, 3398);
					}
					return sendQuestEndDialog(env);
				}
			} else if (qs != null) {
				if (qs.getStatus() == QuestStatus.START) {
					int var = qs.getQuestVarById(0);
					switch (targetId) {
					case 798026:// Kunberunerk
						switch (env.getDialog()) {
						case QUEST_SELECT:
							if (var == 0) {
								return sendQuestDialog(env, 1352);
							} else if (var == 5) {
								return sendQuestDialog(env, 3057);
							}
						case SELECT_ACTION_1438:
							Storage inventory = player.getInventory();
							if (inventory.tryDecreaseKinah(10000)) {
								//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//								qs.setQuestVarById(0, var + 1);
//								updateQuestStatus(env);
//								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//								return true;
								return defaultCloseDialog(env, var, var + 1);
							} else {
								return sendQuestDialog(env, 1523);
							}
						case SETPRO1:// oui 10000
							//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//							qs.setQuestVarById(0, var + 1);// var==1
//							updateQuestStatus(env);
//							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//							return true;
							return defaultCloseDialog(env, var, var + 1);
						case SETPRO2:// non
							//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//							qs.setQuestVarById(0, var + 1);// var==1
//							updateQuestStatus(env);
//							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//							return true;
							return defaultCloseDialog(env, var, var + 1);
						case SETPRO7:
							//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//							qs.setQuestVarById(0, var + 1);// var==5
//							qs.setStatus(QuestStatus.REWARD);
//							updateQuestStatus(env);
//							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//							return true;
							return defaultCloseDialog(env, var, var, true);
						default:
							return sendQuestStartDialog(env);
						}
					case 798025:// Mapireck
						switch (env.getDialog()) {
						case QUEST_SELECT:
							if (var == 1) {
								return sendQuestDialog(env, 1693);
							} else if (var == 4) {
								return sendQuestDialog(env, 2716);
							}
						case SETPRO3:
							//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//							qs.setQuestVarById(0, var + 1);// var==2
//							updateQuestStatus(env);
//							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//							return true;
							return defaultCloseDialog(env, var, var + 1);
						case SETPRO6:
							//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//							removeQuestItem(env, 182206000, 1);
//							qs.setQuestVarById(0, var + 1);// var==5
//							updateQuestStatus(env);
//							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//							return true;
							return defaultCloseDialog(env, var, var + 1, 0, 0, 182206000, 1);
						}
					case 203131:// Maniparas
						switch (env.getDialog()) {
						case QUEST_SELECT:
							return sendQuestDialog(env, 2034);
						case SETPRO4:
							//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//							qs.setQuestVarById(0, var + 1);// var==3
//							updateQuestStatus(env);
//							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//							return true;
							return defaultCloseDialog(env, var, var + 1);
						}
					case 798003:// Gaphyrk
						switch (env.getDialog()) {
						case QUEST_SELECT:
							return sendQuestDialog(env, 2375);
						case SETPRO5:
							//TODO: This is odd handling; look to replace it with a method from super.
							if (player.getInventory().getItemCountByItemId(182206000) == 0) {
								if (!giveQuestItem(env, 182206000, 1)) {
									return true;
								}
							}
							//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//							qs.setQuestVarById(0, var + 1);// var==4
//							updateQuestStatus(env);
//							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//							return true;
							return defaultCloseDialog(env, var, var + 1);
						}
					}
				}
			}
			return false;
		}
	}
}

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

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author dta3000
 * @modified Yon (Aion Reconstruction Project) -- No longer keeps the quest dialog page open.
 */
public class _10026SecretOfInggison extends QuestHandler {
	
	private final static int questId = 10026;
	
	public _10026SecretOfInggison() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(799052).addOnQuestStart(questId); // Steropes
		qe.registerQuestNpc(799052).addOnTalkEvent(questId);
		qe.registerQuestNpc(799053).addOnTalkEvent(questId); // Nydrea
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 10000, true);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			return false;
		}
		
		final int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		
		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799053) {
				return sendQuestEndDialog(env);
			}
		} else if (qs.getStatus() != QuestStatus.START) {
			return false;
		}
		if (targetId == 799052) {
			switch (env.getDialog()) {
			case QUEST_SELECT:
				if (var == 0) {
					return sendQuestDialog(env, 1011);
				}
			case SETPRO1:
				if (var == 0) {
					PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(1, 14));
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVarById(0, var + 1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return defaultCloseDialog(env, var, var + 1);
				}
				return false;
			}
		} else if (targetId == 799053) {
			switch (env.getDialog()) {
			case QUEST_SELECT:
				if (var == 1) {
					return sendQuestDialog(env, 1352);
				} else if (var == 1) {
					return sendQuestDialog(env, 1353);
				}
			case SET_SUCCEED:
				if (var == 1) {
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(1, 15));
					//THIS SHIT SHOULDN'T BE HERE
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return closeDialogWindow(env);
				}
			case SETPRO3:
				if (qs.getStatus() == QuestStatus.REWARD) {
					qs.setStatus(QuestStatus.COMPLETE);
					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
//					return true;
					return closeDialogWindow(env);
				}
				return false;
			}
		}
		return false;
	}
}

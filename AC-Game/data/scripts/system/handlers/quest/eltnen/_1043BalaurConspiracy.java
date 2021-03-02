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
package quest.eltnen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.SystemMessageId;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapType;

/**
 * @author Balthazar
 * @modified Yon (Aion Reconstruction Project) -- No longer keeps the quest dialog page open.
 */
public class _1043BalaurConspiracy extends QuestHandler {
	
	private final static int questId = 1043;
	private static List<Integer> mobs = new ArrayList<Integer>();
	
	static {
		mobs.add(211628);
		mobs.add(211630);
		mobs.add(213575);
	}
	
	public _1043BalaurConspiracy() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnDie(questId);
		qe.registerOnLogOut(questId);
		qe.registerOnQuestTimerEnd(questId);
		qe.registerQuestNpc(203901).addOnTalkEvent(questId);
		qe.registerQuestNpc(204020).addOnTalkEvent(questId);
		qe.registerQuestNpc(204044).addOnTalkEvent(questId);
		for (int mob : mobs) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerQuestNpc(700141).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		
		if (qs == null) {
			return false;
		}
		
		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
			case 203901: {
				switch (env.getDialog()) {
				case QUEST_SELECT: {
					if (qs.getQuestVarById(0) == 0) {
						return sendQuestDialog(env, 1011);
					}
				}
				case SETPRO1: {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVar(1);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					defaultCloseDialog(env, qs.getQuestVarById(0), 1); //The teleport will close the dialog if this fails.
					TeleportService2.teleportTo(player, WorldMapType.ELTNEN.getId(), 1596.1948f, 1529.9152f, 317, (byte) 120, TeleportAnimation.BEAM_ANIMATION);
					return true;
				}
				}
			}
			case 204020: {
				switch (env.getDialog()) {
				case QUEST_SELECT: {
					if (qs.getQuestVarById(0) == 1) {
						return sendQuestDialog(env, 1352);
					}
				}
				case SETPRO2: {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVar(2);
//					updateQuestStatus(env);
//					giveQuestItem(env, 182201013, 1);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					defaultCloseDialog(env, qs.getQuestVarById(0), 2, 182201013, 1, 0, 0); //The teleport will close the dialog if this fails.
					TeleportService2.teleportTo(player, WorldMapType.ELTNEN.getId(), 2500.15f, 780.9f, 409, (byte) 15, TeleportAnimation.BEAM_ANIMATION);
					return true;
				}
				}
			}
			case 204044: {
				switch (env.getDialog()) {
				case QUEST_SELECT: {
					switch (qs.getQuestVarById(0)) {
					case 2: {
						return sendQuestDialog(env, 1693);
					}
					case 4: {
						return sendQuestDialog(env, 2034);
					}
					}
				}
				case SETPRO3: {
					//THIS SHIT SHOULDN'T BE HERE CALL #defaultCloseDialog()
//					qs.setQuestVar(3);
//					updateQuestStatus(env);
//					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					QuestService.questTimerStart(env, 180);
					spawn(player);
//					return true;
					return defaultCloseDialog(env, qs.getQuestVarById(0), 3);
				}
				case SETPRO4: {
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					TeleportService2.teleportTo(player, WorldMapType.ELTNEN.getId(), 271.69f, 2787.04f, 272.47f, (byte) 50, TeleportAnimation.BEAM_ANIMATION);
					return true;
				}
				}
			}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203901) {
				switch (env.getDialog()) {
				case QUEST_SELECT: {
					return sendQuestDialog(env, 2375);
				}
				case SELECT_QUEST_REWARD: {
					removeQuestItem(env, 182201013, 1);
					return sendQuestDialog(env, 5);
				}
				default:
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		int[] quests = {1300, 1031, 1032, 1033, 1034, 1036, 1037, 1035, 1038, 1039, 1040, 1041, 1042};
		return defaultOnZoneMissionEndEvent(env, quests);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] quests = {1300, 1031, 1032, 1033, 1034, 1036, 1037, 1035, 1038, 1039, 1040, 1041, 1042};
		return defaultOnLvlUpEvent(env, quests, true);
	}
	
	@Override
	public boolean onQuestTimerEndEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 3) {
				qs.setQuestVar(4);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onDieEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 3) {
				qs.setQuestVar(2);
				updateQuestStatus(env);
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1, DataManager.QUEST_DATA.getQuestById(questId).getName()));
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onLogOutEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 3) {
				qs.setQuestVar(2);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 3) {
				int targetId = env.getTargetId();
				if (mobs.contains(targetId)) {
					spawn(player);
					return true;
				}
			}
		}
		return false;
	}
	
	private void spawn(Player player) {
		int mobToSpawn = mobs.get(Rnd.get(0, 2));
		float x = 0;
		float y = 0;
		final float z = 217.48f;
		switch (mobToSpawn) {
		case 211628: {
			x = 254.74f;
			y = 236.72f;
			break;
		}
		case 211630: {
			x = 257.92f;
			y = 237.39f;
			break;
		}
		case 213575: {
			x = 261.86f;
			y = 237.5f;
			break;
		}
		}
		Npc spawn = (Npc) QuestService.spawnQuestNpc(310040000, player.getInstanceId(), mobToSpawn, x, y, z, (byte) 95);
		Collection<Npc> allNpcs = World.getInstance().getNpcs();
		Npc target = null;
		for (Npc npc : allNpcs) {
			if (npc.getNpcId() == 204044) {
				target = npc;
			}
		}
		if (target != null) {
			spawn.setTarget(target);
			((AbstractAI) spawn.getAi2()).setStateIfNot(AIState.WALKING);
			spawn.setState(1);
			spawn.getMoveController().moveToTargetObject();
			PacketSendUtility.broadcastPacket(spawn, new SM_EMOTION(spawn, EmotionType.START_EMOTE2, 0, spawn.getObjectId()));
		}
	}
}

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

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author MrPoke + Dune11
 * @rework pralinka
 */
public class _1300OrdersfromTelemachus extends QuestHandler {
	
	private final static int questId = 1300;
	
	public _1300OrdersfromTelemachus() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(203901).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("ELTNEN_FORTRESS_210020000"), questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			return false;
		}
		
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc) {
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		if (targetId != 203901) {
			return false;
		}
		if (qs.getStatus() == QuestStatus.START) {
			if (env.getDialog() == DialogAction.QUEST_SELECT) {
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				return sendQuestDialog(env, 1011);
			} else {
				return sendQuestStartDialog(env);
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (env.getDialogId() == DialogAction.SELECTED_QUEST_NOREWARD.id()) {
				int[] ids = {1031, 1032, 1033, 1034, 1035, 1036, 1037, 1038, 1039, 1040, 1041, 1042, 1043};
				for (int id : ids) {
					QuestEngine.getInstance().onEnterZoneMissionEnd(new QuestEnv(env.getVisibleObject(), env.getPlayer(), id, env.getDialogId()));
				}
			}
			return sendQuestEndDialog(env);
		}
		return false;
	}
	
	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
		Player player = env.getPlayer();
		PlayerClass playerClass = player.getCommonData().getPlayerClass();
		if (playerClass == PlayerClass.RIDER) {
			return false;
		}
		return defaultOnEnterZoneEvent(env, zoneName, ZoneName.get("ELTNEN_FORTRESS_210020000"));
	}
}

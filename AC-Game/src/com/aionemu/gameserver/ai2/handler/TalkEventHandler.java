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
package com.aionemu.gameserver.ai2.handler;

import java.util.List;

import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.quest.XMLStartCondition;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.TownService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 * @modified Yon (Aion Reconstruction Project) -- Tweaked non-quest NPC handling to send DialogID 1011 in {@link #onTalk(NpcAI2, Creature)}.
 */
public class TalkEventHandler {
	
	/**
	 * @param npcAI
	 * @param creature
	 */
	public static void onTalk(NpcAI2 npcAI, Creature creature) {
		onSimpleTalk(npcAI, creature);
		
		if (creature instanceof Player) {
			Player player = (Player) creature;
			if (QuestEngine.getInstance().onDialog(new QuestEnv(npcAI.getOwner(), player, 0, -1))) {
				return;
			}
			// only player villagers can use villager npcs in oriel/pernon
			switch (npcAI.getOwner().getObjectTemplate().getTitleId()) {
			case 462877:
				int playerTownId = TownService.getInstance().getTownResidence(player);
				int currentTownId = TownService.getInstance().getTownIdByPosition(player);
				if (playerTownId != currentTownId) {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcAI.getOwner().getObjectId(), 44));
					return;
				} else {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcAI.getOwner().getObjectId(), 10));
					return;
				}
			default:
				int hasQuest = 0;
				List<Integer> questsWithDialog = QuestEngine.getInstance().getQuestNpc(npcAI.getOwner().getNpcId()).getOnTalkEvent();
				for (int questId: questsWithDialog) {
					//If we're in here, the NPC has a quest dialog for something.
					//FIXME: Check if there's a visible quest for the player to select (quest handlers are incorrectly coded)!
					//OR, if cannot fix generically, handle dialog USE_OBJECT case in all quest handlers (should do this anyway).
					QuestState qs = player.getQuestStateList().getQuestState(questId);
					List<XMLStartCondition> conditions = DataManager.QUEST_DATA.getQuestById(questId).getXMLStartConditions();
					int completedConditionCount = 0;
					for (XMLStartCondition cond: conditions) {
						if (cond.check(player, false)) {
							completedConditionCount++;
						}
					}
					if ((qs == null && completedConditionCount == conditions.size())
					|| (qs != null && (qs.getStatus() != QuestStatus.COMPLETE || qs.canRepeat()))) {
						hasQuest++;
					}
				}
				
				List<Integer> funcDialog = npcAI.getOwner().getObjectTemplate().getFuncDialogIds();
				if ((funcDialog != null && funcDialog.size() > 0) || hasQuest > 0) {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcAI.getOwner().getObjectId(), 10)); //"select_quest" dialog option
				} else {
					switch (npcAI.getOwner().getWorldId()) {
						case 210010000:
						case 220010000:
							if (player.getPlayerClass().isStartingClass()) {
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcAI.getOwner().getObjectId(), 1011)); //"select1" dialog option
							} else {
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcAI.getOwner().getObjectId(), 1352)); //"select2" dialog option
							}
							break;
						default:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcAI.getOwner().getObjectId(), 1011)); //"select1" dialog option
					}
				}
				break;
			}
		}
		
	}
	
	/**
	 * @param npcAI
	 * @param creature
	 */
	public static void onSimpleTalk(NpcAI2 npcAI, Creature creature) {
		if (npcAI.getOwner().getObjectTemplate().isDialogNpc()) {
			npcAI.setSubStateIfNot(AISubState.TALK);
			npcAI.getOwner().setTarget(creature);
		}
	}
	
	/**
	 * @param npcAI
	 * @param creature
	 */
	public static void onFinishTalk(NpcAI2 npcAI, Creature creature) {
		Npc owner = npcAI.getOwner();
		if (owner.isTargeting(creature.getObjectId())) {
			if (npcAI.getState() != AIState.FOLLOWING) {
				owner.setTarget(null);
			}
			npcAI.think();
		}
	}
	
	/**
	 * No SM_LOOKATOBJECT broadcast
	 *
	 * @param npcAI
	 * @param creature
	 */
	public static void onSimpleFinishTalk(NpcAI2 npcAI, Creature creature) {
		Npc owner = npcAI.getOwner();
		if (owner.isTargeting(creature.getObjectId()) && npcAI.setSubStateIfNot(AISubState.NONE)) {
			owner.setTarget(null);
		}
	}
}

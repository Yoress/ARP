/**
 * This file is part of the Aion Reconstruction Project Server.
 *
 * The Aion Reconstruction Project Server is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * The Aion Reconstruction Project Server is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with the Aion Reconstruction Project Server. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * @AionReconstructionProjectTeam
 */
package com.aionemu.gameserver.ai2.mechanics.conditions;

import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.context.ObjIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.QuestProgress;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.model.QuestStatus;


/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class IsTargetQuestStateCondition extends Condition {
	
	final public ObjIndicator target;
	
	final public int questId;
	
	final public QuestProgress questProgress;
	
	public IsTargetQuestStateCondition(ObjIndicator target, int questId, QuestProgress questProgress) {
		super(ConditionType.is_target_quest_state);
		this.target = target;
		this.questId = questId;
		this.questProgress = questProgress;
	}
	
	@Override
	public boolean check(MechanicEvent event, AbstractMechanicsAI2 ai) {
		Creature targ = (Creature) event.getObjectIndicator(target, ai);
		if (targ instanceof Player) {
			Player player = (Player) targ;
			QuestStatus status = player.getQuestStateList().getQuestState(questId).getStatus();
			switch (questProgress) {
				case QSTATEI_ACQUIRED:
					return status == QuestStatus.START || status == QuestStatus.REWARD;
				case QSTATEI_SUCCEED:
					return status == QuestStatus.COMPLETE;
				default:
					assert false:"Unsupported QuestProgress: " + questProgress;
			}
		}
		throw new UnsupportedOperationException(event.getClass().getName() + " did not have a Player event target for " + this.getClass().getName());
	}
	
	@Override
	public int hashCode() {
		return 3*target.ordinal() + 5*questId + 7*questProgress.ordinal();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IsTargetQuestStateCondition) {
			IsTargetQuestStateCondition o = (IsTargetQuestStateCondition) obj;
			return (o.target == target && o.questId == questId && o.questProgress == questProgress);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + target + "] --> Q" + questId + ": " + questProgress;
	}
	
}

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
package com.aionemu.gameserver.ai2.mechanics.actions;

import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.MechanicEventType;
import com.aionemu.gameserver.ai2.mechanics.context.BtimerIndicator;
import com.aionemu.gameserver.ai2.mechanics.events.BattleTimerEvent;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class AddBattleTimerAction extends Action {
	
	public final BtimerIndicator btimerIndicator;
	
	public final int delay;
	
	public AddBattleTimerAction(BtimerIndicator btimerIndicator, int delay) {
		super(ActionType.add_battle_timer);
		this.btimerIndicator = btimerIndicator;
		this.delay = delay;
	}
	
	@Override
	public void performAction(MechanicEvent event, final AbstractMechanicsAI2 ai) {
		if (delay > 0) ThreadPoolManager.getInstance().schedule(new Runnable() {
			public void run() {
				if (ai.isNonFightingState() || ai.isAlreadyDead()) return;
				ai.onMechanicEvent(new BattleTimerEvent(MechanicEventType.on_battle_timer, btimerIndicator));
			}
		}, delay);
	}
	
	@Override
	public int hashCode() {
		return 3*btimerIndicator.ordinal() + 11*delay;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AddBattleTimerAction) {
			if (((AddBattleTimerAction) obj).btimerIndicator == btimerIndicator && ((AddBattleTimerAction) obj).delay == delay)
				return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + btimerIndicator + "] --> " + delay;
	}
}

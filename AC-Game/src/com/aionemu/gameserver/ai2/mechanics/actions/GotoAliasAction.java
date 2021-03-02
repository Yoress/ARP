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

import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.context.Alias;
import com.aionemu.gameserver.ai2.mechanics.context.MoveType;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class GotoAliasAction extends Action {
	
	public final Alias alias;
	
	public final MoveType moveType;
	
	public GotoAliasAction(Alias alias, MoveType moveType) {
		super(ActionType.goto_alias);
		this.alias = alias;
		this.moveType = moveType;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		/*
		 * We're gonna make the assumption that the NPC is in the right spot for this.
		 */
		if (alias.isValid() && ai.getOwner().getWorldId() == alias.getWorldId()) {
			ai.setSubStateIfNot(AISubState.WALK_ALIAS);
			ai.getOwner().getMoveController().abortMove();
			ai.getOwner().getMoveController().moveToAlias(alias);
		}
	}
	
	@Override
	public int hashCode() {
		return 3*alias.ordinal() + 5*moveType.ordinal();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GotoAliasAction) {
			return (((GotoAliasAction) obj).alias == alias && ((GotoAliasAction) obj).moveType == moveType);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + alias + "] -->" + moveType;
	}
	
}

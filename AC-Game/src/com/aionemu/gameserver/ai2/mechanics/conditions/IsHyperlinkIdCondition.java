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
import com.aionemu.gameserver.ai2.mechanics.context.HyperlinkId;
import com.aionemu.gameserver.ai2.mechanics.events.HyperlinkEvent;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;


/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class IsHyperlinkIdCondition extends Condition {
	
	final public HyperlinkId hyperlinkId;
	
	public IsHyperlinkIdCondition(HyperlinkId hyperlinkId) {
		super(ConditionType.is_hyperlink_id);
		this.hyperlinkId = hyperlinkId;
	}
	
	@Override
	public boolean check(MechanicEvent event, AbstractMechanicsAI2 ai) {
		if (event instanceof HyperlinkEvent) {
			HyperlinkEvent hEvent = (HyperlinkEvent) event;
			return hEvent.hyperlinkId == hyperlinkId.numericId;
		}
		throw new UnsupportedOperationException(event.getClass().getSimpleName() + " is unsupported by: " + this.getClass().getSimpleName());
	}
	
	@Override
	public int hashCode() {
		return 3*hyperlinkId.ordinal();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IsHyperlinkIdCondition) {
			IsHyperlinkIdCondition o = (IsHyperlinkIdCondition) obj;
			return (o.hyperlinkId == hyperlinkId);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + hyperlinkId + "]";
	}
	
}

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
import com.aionemu.gameserver.ai2.mechanics.context.DamageFlag;
import com.aionemu.gameserver.ai2.mechanics.events.AttackedEvent;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.controllers.attack.AttackStatus;


/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class HasAttackDamageFlagCondition extends Condition {
	
	final public DamageFlag damageFlag;
	
	public HasAttackDamageFlagCondition(DamageFlag damageFlag) {
		super(ConditionType.has_attack_damage_flag);
		this.damageFlag = damageFlag;
	}
	
	@Override
	public boolean check(MechanicEvent event, AbstractMechanicsAI2 ai) {
		if (event instanceof AttackedEvent) {
			AttackedEvent aEvent = (AttackedEvent) event;
			switch (damageFlag) {
				case DODGE:
					return aEvent.attackStatus == AttackStatus.DODGE;
				default: assert false:"Unsupported DamageFlag: " + damageFlag;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return 3*damageFlag.ordinal();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof HasAttackDamageFlagCondition) {
			HasAttackDamageFlagCondition o = (HasAttackDamageFlagCondition) obj;
			return (o.damageFlag == damageFlag);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + damageFlag + "]";
	}
	
}

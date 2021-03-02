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
package com.aionemu.gameserver.ai2.mechanics.context;

import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@XmlType
public enum AbnormalStateIndicator {
	ABNSTATEI_BLEED,
	ABNSTATEI_SANCTUARY,
	ABNSTATEI_MENTAL_GROUP,
	ABNSTATEI_ROOT,
	ABNSTATEI_CANNOT_ACT_GROUP,
	ABNSTATEI_POISON,
	ABNSTATEI_STUN,
	ABNSTATEI_SLEEP,
	ABNSTATEI_STUN_LIKE_GROUP,
	ABNSTATEI_PHYSICAL_GROUP,
	ABNSTATEI_INVISIBLE,
	ABNSTATEI_DEFORM,
	ABNSTATEI_STUMBLE;
	
	
	public boolean matchesAbnormalState(AbnormalState state) {
		switch (this) {
			case ABNSTATEI_BLEED:
				return (state.getId() & AbnormalState.BLEED.getId()) > 0;
			case ABNSTATEI_CANNOT_ACT_GROUP:
				return (state.getId() & AbnormalState.CANT_ATTACK_STATE.getId()) > 0;
			case ABNSTATEI_DEFORM:
				return (state.getId() & AbnormalState.DEFORM.getId()) > 0;
			case ABNSTATEI_INVISIBLE:
				return (state.getId() & AbnormalState.HIDE.getId()) > 0;
			case ABNSTATEI_MENTAL_GROUP:
				return (state.getId() & AbnormalState.MENTAL_STATE.getId()) > 0;
			case ABNSTATEI_PHYSICAL_GROUP:
				return (state.getId() & AbnormalState.PHYSICAL_STATE.getId()) > 0;
			case ABNSTATEI_POISON:
				return (state.getId() & AbnormalState.POISON.getId()) > 0;
			case ABNSTATEI_ROOT:
				return (state.getId() & AbnormalState.ROOT.getId()) > 0;
			case ABNSTATEI_SANCTUARY:
				return (state.getId() & AbnormalState.SANCTUARY.getId()) > 0;
			case ABNSTATEI_SLEEP:
				return (state.getId() & AbnormalState.SLEEP.getId()) > 0;
			case ABNSTATEI_STUMBLE:
				return (state.getId() & AbnormalState.STUMBLE.getId()) > 0;
			case ABNSTATEI_STUN:
				return (state.getId() & AbnormalState.STUN.getId()) > 0;
			case ABNSTATEI_STUN_LIKE_GROUP:
				return (state.getId() & AbnormalState.STUN_LIKE_STATE.getId()) > 0;
			default:
				assert false:"Unsupported Abnormal State: " + this;
		}
		return false;
	}
	
	public boolean isCreatureAffected(Creature creature) {
		switch (this) {
			case ABNSTATEI_BLEED:
				return creature.getEffectController().isAbnormalSet(AbnormalState.BLEED);
			case ABNSTATEI_CANNOT_ACT_GROUP:
				return creature.getEffectController().isAbnormalSet(AbnormalState.CANT_ATTACK_STATE);
			case ABNSTATEI_MENTAL_GROUP:
				return creature.getEffectController().isAbnormalSet(AbnormalState.MENTAL_STATE);
			case ABNSTATEI_POISON:
				return creature.getEffectController().isAbnormalSet(AbnormalState.POISON);
			case ABNSTATEI_ROOT:
				return creature.getEffectController().isAbnormalSet(AbnormalState.ROOT);
			case ABNSTATEI_SANCTUARY:
				return creature.getEffectController().isAbnormalSet(AbnormalState.SANCTUARY);
			case ABNSTATEI_SLEEP:
				return creature.getEffectController().isAbnormalSet(AbnormalState.SLEEP);
			case ABNSTATEI_STUN:
				return creature.getEffectController().isAbnormalSet(AbnormalState.STUN);
			case ABNSTATEI_STUN_LIKE_GROUP:
				return creature.getEffectController().isAbnormalSet(AbnormalState.STUN_LIKE_STATE);
			case ABNSTATEI_DEFORM:
				return creature.getEffectController().isAbnormalSet(AbnormalState.DEFORM);
			case ABNSTATEI_INVISIBLE:
				return creature.getEffectController().isAbnormalSet(AbnormalState.HIDE);
			case ABNSTATEI_PHYSICAL_GROUP:
				return creature.getEffectController().isAbnormalSet(AbnormalState.PHYSICAL_STATE);
			case ABNSTATEI_STUMBLE:
				return creature.getEffectController().isAbnormalSet(AbnormalState.STUMBLE);
			default:
				assert false:"Unsupported Abnormal State: " + this;
		}
		return false;
	}
}

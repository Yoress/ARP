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
package com.aionemu.gameserver.skillengine.effect;

/**
 * @author ATracer
 * @Reworked Kill3r
 * @modified Yon (Aion Reconstruction Project) -- Added {@link #MENTAL_STATE}, and {@link #STUN_LIKE_STATE}, renamed {@link #UNKNOWN}
 * to {@link #SANCTUARY}, and added it to some of the combined states.
 */
public enum AbnormalState {
	
	BUFF(0),
	POISON(1),
	BLEED(2),
	PARALYZE(4),
	SLEEP(8),
	ROOT(16), // ?? cannot move ?
	BLIND(32),
	SANCTUARY(64),
	DISEASE(128),
	SILENCE(256),
	FEAR(512), // Fear I
	CURSE(1024), //TODO: Identify what this is, if it's a mental state add it to MENTAL_STATE
	CHAOS(2056), //TODO: Identify what this is, if it's a mental state add it to MENTAL_STATE
	STUN(4096),
	PETRIFICATION(8192),
	STUMBLE(16384),
	STAGGER(32768),
	OPENAERIAL(65536),
	SNARE(131072), //TODO: Identify what this is, if it's a Stun-like effect, add it to STUN_LIKE_STATE and maybe PHYSICAL_STATE
	SLOW(262144),
	SPIN(524288),
	BIND(1048576),
	DEFORM(2097152), // (Curse of Roots I, Fear I)
	CANNOT_MOVE(4194304), // (Inescapable Judgment I)
	NOFLY(8388608), // cannot fly
	KNOCKBACK(16777216), // simple_root
	HIDE(536870912), // hide 33554432
	
	/**
	 * Compound abnormal states
	 */
	CANT_ATTACK_STATE(SPIN.id | SLEEP.id | STUN.id | STUMBLE.id | STAGGER.id | OPENAERIAL.id | PARALYZE.id | FEAR.id | CANNOT_MOVE.id | SANCTUARY.id),
	CANT_MOVE_STATE(SPIN.id | ROOT.id | SLEEP.id | STUMBLE.id | STUN.id | STAGGER.id | OPENAERIAL.id | PARALYZE.id | CANNOT_MOVE.id | SANCTUARY.id),
	CANT_MOVE_STATE2(SPIN.id | SLEEP.id | STUMBLE.id | STUN.id | STAGGER.id | OPENAERIAL.id | PARALYZE.id | CANNOT_MOVE.id | SANCTUARY.id), // without root , for CM_EMOTION, because you can buff up and go attack mode while rooted
	DISMOUT_RIDE(SPIN.id | ROOT.id | SLEEP.id | STUMBLE.id | STUN.id | STAGGER.id | OPENAERIAL.id | PARALYZE.id | CANNOT_MOVE.id | FEAR.id | SNARE.id),
	MENTAL_STATE(SLEEP.id | FEAR.id | /*CURSE.id | CHAOS.id |*/ PETRIFICATION.id | DEFORM.id),
	STUN_LIKE_STATE(STUN.id | STUMBLE.id | STAGGER.id | OPENAERIAL.id | /*SNARE.id |*/ SPIN.id | CANNOT_MOVE.id | KNOCKBACK.id | SANCTUARY.id),
	PHYSICAL_STATE(STUN.id | STUMBLE.id | STAGGER.id | OPENAERIAL.id | /*SNARE.id |*/ SPIN.id | CANNOT_MOVE.id | KNOCKBACK.id | SANCTUARY.id);
	
	private int id;
	
	private AbnormalState(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public static AbnormalState getIdByName(String name) {
		for (AbnormalState id : values()) {
			if (id.name().equals(name)) {
				return id;
			}
		}
		return null;
	}
	
	public static AbnormalState getStateById(int id) {
		for (AbnormalState as : values()) {
			if (as.getId() == id) {
				return as;
			}
		}
		return null;
	}
}

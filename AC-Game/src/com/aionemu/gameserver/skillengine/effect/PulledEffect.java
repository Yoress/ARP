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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FORCED_MOVE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillMoveType;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.geo.GeoService;
import com.aionemu.gameserver.world.geo.nav.NavService;

/**
 * @author Sarynth modified by Wakizashi, Sippolo
 * @modified Yon (Aion Reconstruction Project) -- Changed the conditions upon which targets are moved, and added an
 * abortMove() call in {@link #startEffect(Effect)}
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PulledEffect")
public class PulledEffect extends EffectTemplate {
	
	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
		final Creature effected = effect.getEffected();
		effected.setCriticalEffectMulti(0);
		effected.getController().cancelCurrentSkill();
		if (effect.getTargetX() != 0 || effect.getTargetY() != 0 || effect.getTargetZ() != 0) {
			effected.getMoveController().abortMove();
			World.getInstance().updatePosition(effected, effect.getTargetX(), effect.getTargetY(), effect.getTargetZ(), effected.getHeading());
			PacketSendUtility.broadcastPacketAndReceive(effected, new SM_FORCED_MOVE(effect.getEffector(), effected.getObjectId(), effect.getTargetX(), effect.getTargetY(), effect.getTargetZ()));
		}
	}
	
	@Override
	public void calculate(Effect effect) {
		if (effect.getEffected().getEffectController().hasPhysicalStateEffect()) {
			return;
		}
		
		if (!super.calculate(effect, StatEnum.PULLED_RESISTANCE, null)) {
			return;
		}
		
		effect.setSkillMoveType(SkillMoveType.PULL);
		final Creature effector = effect.getEffector();
		final Creature effected = effect.getEffected();
		
		if (NavService.getInstance().canPullTarget(effector, effected)) {
			/*
			 * Target must be pulled just one meter away from effector, not IN place of effector
			 * Pull targets along the ground to target! If there is no path, then don't move them unless they are flying.
			 * canPullTarget() checks if the target is flying; if both the caster and target are flying,
			 * then we can avoid a geo lookup and just pull the target in front of the caster. If only the target
			 * is flying, then we should pull it to the ground in front of the caster.
			 */
			double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effector.getHeading()));
			final float x1 = (float) Math.cos(radian);
			final float y1 = (float) Math.sin(radian);
			if (effected.isFlying() && effector.isFlying()) {
				//Note: Might be some awkward edge case where the target is pulled into a wall; not gonna handle it here.
				effect.setTargetLoc(effector.getX() + x1, effector.getY() + y1, effector.getZ() + 0.25F);
			} else {
				final float z1 = GeoService.getInstance().getZ(effector.getWorldId(), effector.getX() + x1, effector.getY() + y1, effector.getZ(), 0.5F, effector.getInstanceId());
				effect.setTargetLoc(effector.getX() + x1, effector.getY() + y1, z1);
			}
		}
	}
	
	@Override
	public void startEffect(Effect effect) {
		final Creature effected = effect.getEffected();
		effected.getEffectController().setAbnormal(AbnormalState.CANNOT_MOVE.getId(), effect.getEffector());
		effected.getMoveController().abortMove(); //Needed here or the entity's pull animation won't be correct.
		effect.setAbnormal(AbnormalState.CANNOT_MOVE.getId());
	}
	
	@Override
	public void endEffect(Effect effect) {
		effect.setIsPhysicalState(false);
		effect.getEffected().setCriticalEffectMulti(1);
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.CANNOT_MOVE.getId());
	}
}

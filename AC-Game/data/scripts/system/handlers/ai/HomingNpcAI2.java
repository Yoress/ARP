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
package ai;

import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.ai.GeneralNpcAI2;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Homing;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;

/**
 * @author ATracer
 * @modified Yon (Aion Reconstruction Project)
 */
@AIName("homing")
public class HomingNpcAI2 extends GeneralNpcAI2 {
	
	@Override
	public void think() {
		// homings are not thinking to return :)
		if (getTarget() == null) {
			Creature c = getOwner();
			if (c instanceof Homing) {
				Homing owner = (Homing) c;
				//This will make it despawn if it was summoned by SummonHomingEffect
				owner.setAttackCount(1);
				owner.getObserveController().notifyAttackObservers(owner);
			}
		}
	}
	
	@Override
	protected void handleSpawned() {
		//This would normally call think(), and we don't want that!
		setStateIfNot(AIState.IDLE);
	}
	
//	@Override
//	public AttackIntention chooseAttackIntention(Creature creature) {
//		// TO-DO skill type homings
//		return AttackIntention.SIMPLE_ATTACK;
//	}
	
	@Override
	protected void handleAttackComplete() {
		VisibleObject target = getTarget();
		Creature targ = target instanceof Creature ? (Creature) getTarget() : null;
		if (targ != null) {
			skillId = 0;
			skillLevel = 0;
			AI2Logger.info(this, "handleAttackComplete() --> Last attack " + getOwner().getGameStats().getLastAttackTimeDelta() + "s ago.");
			getOwner().getGameStats().renewLastAttackTime();
			onIntentionToAttack(targ);
		} else super.handleAttackComplete();
	}
	
//	@Override
//	protected void handleAttackComplete() {
//		super.handleAttackComplete();
//		Homing owner = (Homing) getOwner();
//		if (owner.getActiveSkillId() != 0) {
//			AttackManager.scheduleNextAttack(this); //Handled by super call
//		}
//	}
	
	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
		case SHOULD_DECAY:
			return AIAnswers.NEGATIVE;
		case SHOULD_RESPAWN:
			return AIAnswers.NEGATIVE;
		case SHOULD_REWARD:
			return AIAnswers.NEGATIVE;
		default:
			return null;
		}
	}
}

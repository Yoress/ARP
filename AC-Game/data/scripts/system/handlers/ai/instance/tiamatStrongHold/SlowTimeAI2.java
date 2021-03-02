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
package ai.instance.tiamatStrongHold;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * @author Cheatkiller
 */
@AIName("slowedtime")
public class SlowTimeAI2 extends NpcAI2 {
	
	@Override
	protected void handleCreatureSee(Creature creature) {
		checkDistance(this, creature);
	}
	
	@Override
	protected void handleCreatureMoved(Creature creature) {
		checkDistance(this, creature);
	}
	
	private void checkDistance(NpcAI2 ai, Creature creature) {
		if (creature instanceof Player) {
			if (MathUtil.isIn3dRange(getOwner(), creature, 50) && !creature.getEffectController().hasAbnormalEffect(20728)) {
				AI2Actions.useSkill(this, 20728);
			}
		}
	}
	
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

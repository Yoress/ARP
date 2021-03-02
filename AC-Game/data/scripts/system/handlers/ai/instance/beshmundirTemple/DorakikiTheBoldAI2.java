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
package ai.instance.beshmundirTemple;

import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.ai.AggressiveNpcAI2;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.services.NpcShoutsService;

/**
 * @author Cheatkiller
 * @author Antraxx
 * @modified Yon (Aion Reconstruction Project) -- {@link #handleAttackComplete()} now calls super
 * implementation to avoid a deprecated method call.
 */
@AIName("dorakiki_the_bold")
public class DorakikiTheBoldAI2 extends AggressiveNpcAI2 {
	
	private AtomicBoolean isHome = new AtomicBoolean(true);
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			NpcShoutsService.getInstance().sendMsg(getOwner(), 1500079, getObjectId(), 0, 1000);
		}
	}
	
	@Override
	protected void handleAttackComplete() {
		super.handleAttackComplete();
		if (getEffectController().hasAbnormalEffect(18901)) {
			getEffectController().removeEffect(18901);
		}
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		if (getEffectController().hasAbnormalEffect(18901)) {
			getEffectController().removeEffect(18901);
		}
		isHome.set(true);
	}
}

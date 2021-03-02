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
package ai.worlds.heiron;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.ai.GeneralNpcAI2;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * @author cheatkiller
 */
@AIName("klawspawn")
public class KlawspawnAI2 extends GeneralNpcAI2 {
	
	@Override
	public boolean canThink() {
		return false;
	}
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		Npc npc = getOwner().getPosition().getWorldMapInstance().getNpc(212120);
		if (npc == null) {
			if (Rnd.get(0, 100) < 10) {
				spawn(212120, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
				AI2Actions.dieSilently(this, creature);
			}
		}
	}
	
	@Override
	public int modifyDamage(int damage) {
		return 1;
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		AI2Actions.deleteOwner(this);
	}
}

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
package ai.worlds.kataramunderground;

import java.util.concurrent.Future;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import ai.NoActionAI2;

/**
 * @author zxl001
 */
@AIName("luciastuffedegg")
public class Luciastuffedeggs extends NoActionAI2 {
	
	private Future<?> spawnTask;
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		if (spawnTask == null) startTask();
	}
	
	private void startTask() {
		
		spawnTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
			
			@Override
			public void run() {
				spawn(284278, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
				getOwner().getController().die();
				getOwner().getController().delete();
			}
		}, 30000);
	}
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		if (spawnTask != null) spawnTask.cancel(true);
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		if (spawnTask != null) spawnTask.cancel(true);
	}
}

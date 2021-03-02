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
package com.aionemu.gameserver.controllers.observer;

import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.gameserver.geoEngine.bounding.BoundingBox;
import com.aionemu.gameserver.geoEngine.collision.CollisionResults;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.geoEngine.scene.Spatial;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.templates.BoundRadius;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author MrPoke
 * @moved Rolandas
 * @reworked Yon (Aion Reconstruction Project) -- Uncommented collision detection; it's needed.
 * Reworked ray casting into bounding box intersection to check if the player is standing on
 * this collision, as opposed to pathing through it (which failed on flat surfaces).
 * Ray casting is still preferable, but the collision in BIHTree is not computed correctly.
 */
public abstract class AbstractCollisionObserver extends ActionObserver {
	
	protected Creature creature;
//	protected Vector3f oldPos;
	protected Spatial geometry;
	protected byte intentions;
	private AtomicBoolean isRunning = new AtomicBoolean();
	
	public AbstractCollisionObserver(Creature creature, Spatial geometry, byte intentions) {
		super(ObserverType.MOVE_OR_DIE);
		this.creature = creature;
		this.geometry = geometry;
//		this.oldPos = new Vector3f(creature.getX(), creature.getY(), creature.getZ());
		this.intentions = intentions;
	}
	
	@Override
	public void moved() {
		if (!isRunning.getAndSet(true)) {
			ThreadPoolManager.getInstance().execute(new Runnable() {
				
				@Override
				public void run() {
					try {
						Vector3f pos = new Vector3f(creature.getX(), creature.getY(), creature.getZ());
//						Vector3f dir = oldPos.clone();
//						Float limit = pos.distance(dir);
//						dir.subtractLocal(pos).normalizeLocal();
//						Ray r = new Ray(pos, dir);
//						r.setLimit(limit);
						BoundRadius rad = creature.getObjectTemplate().getBoundRadius();
						BoundingBox r = new BoundingBox(pos, rad.getCollision(), rad.getCollision(), rad.getUpper());
						CollisionResults results = new CollisionResults(intentions, true, creature.getInstanceId());
						geometry.collideWith(r, results);
						onMoved(results);
//						oldPos = pos;
					} finally {
						isRunning.set(false);
					}
				}
			});
		}
	}
	
	public abstract void onMoved(CollisionResults result);
}

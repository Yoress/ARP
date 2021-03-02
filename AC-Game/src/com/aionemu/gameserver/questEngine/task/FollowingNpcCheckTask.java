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
package com.aionemu.gameserver.questEngine.task;

import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author ATracer
 */
public class FollowingNpcCheckTask implements Runnable {
	
	private final QuestEnv env;
	private final DestinationChecker destinationChecker;
	
	/**
	 * @param player
	 * @param npc
	 * @param destinationChecker
	 */
	FollowingNpcCheckTask(QuestEnv env, DestinationChecker destinationChecker) {
		this.env = env;
		this.destinationChecker = destinationChecker;
	}
	
	@Override
	public void run() {
		final Player player = env.getPlayer();
		Npc npc = (Npc) destinationChecker.follower;
		if (player.getLifeStats().isAlreadyDead() || npc.getLifeStats().isAlreadyDead()) {
			onFail(env);
		}
		if (!MathUtil.isIn3dRange(player, npc, 50)) {
			onFail(env);
		}
		
		if (destinationChecker.check()) {
			onSuccess(env);
		}
	}
	
	/**
	 * Following task succeeded, proceed with quest
	 */
	private final void onSuccess(QuestEnv env) {
		stopFollowing(env);
		QuestEngine.getInstance().onNpcReachTarget(env);
	}
	
	/**
	 * Following task failed, abort further progress
	 */
	protected void onFail(QuestEnv env) {
		stopFollowing(env);
		QuestEngine.getInstance().onNpcLostTarget(env);
	}
	
	private final void stopFollowing(QuestEnv env) {
		Player player = env.getPlayer();
		Npc npc = (Npc) destinationChecker.follower;
		player.getController().cancelTask(TaskId.QUEST_FOLLOW);
		npc.getAi2().onCreatureEvent(AIEventType.STOP_FOLLOW_ME, player);
		
		//Why is this here? It's handled in the AI when it stops following.
		if (!npc.getAi2().getName().equals("following")) {
			npc.getController().onDelete();
		}
	}
}

abstract class DestinationChecker {
	
	protected Creature follower;
	
	abstract boolean check();
}

final class TargetDestinationChecker extends DestinationChecker {
	
	private final Creature target;
	
	/**
	 * @param follower
	 * @param target
	 */
	TargetDestinationChecker(Creature follower, Creature target) {
		this.follower = follower;
		this.target = target;
	}
	
	@Override
	boolean check() {
		return MathUtil.isIn3dRange(target, follower, 3);
	}
}

final class CoordinateDestinationChecker extends DestinationChecker {
	
	private final float x;
	private final float y;
	private final float z;
	
	/**
	 * @param follower
	 * @param x
	 * @param y
	 * @param z
	 */
	CoordinateDestinationChecker(Creature follower, float x, float y, float z) {
		this.follower = follower;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	boolean check() {
		return MathUtil.isNearCoordinates(follower, x, y, z, 3);
	}
}

final class ZoneChecker extends DestinationChecker {
	
	private final ZoneName zoneName;
	
	ZoneChecker(Creature follower, ZoneName zoneName) {
		this.follower = follower;
		this.zoneName = zoneName;
	}
	
	@Override
	boolean check() {
		return follower.isInsideZone(zoneName);
	}
}

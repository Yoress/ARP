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
package com.aionemu.gameserver.ai2.handler;

import java.util.Collections;

import com.aionemu.gameserver.ai2.AI2;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.controllers.attack.AttackResult;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npc.NpcTemplateType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.services.TribeRelationService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.geo.GeoService;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * @author ATracer
 * @modified Yon (Aion Reconstruction Project) -- {@link #onCreatureNeedsSupport(NpcAI2, Creature)}, and 
 * {@link #onGuardAgainstAttacker(NpcAI2, Creature)} tweaked to solve some oddities that've been
 * hiding (they caused the client to behave oddly).
 */
public class AggroEventHandler {
	
	/**
	 * @param npcAI
	 * @param myTarget
	 */
	public static void onAggro(NpcAI2 npcAI, final Creature myTarget) {
		final Npc owner = npcAI.getOwner();
		// TODO move out?
		if (myTarget.getAdminNeutral() == 1 || myTarget.getAdminNeutral() == 3 || myTarget.getAdminEnmity() == 1 || myTarget.getAdminEnmity() == 3 || TribeRelationService.isFriend(owner, myTarget)) {
			return;
		}
		PacketSendUtility.broadcastPacket(owner, new SM_ATTACK(owner, myTarget, 0, 633, 0, Collections.singletonList(new AttackResult(0, AttackStatus.NORMALHIT))));
		
		ThreadPoolManager.getInstance().schedule(new AggroNotifier(owner, myTarget, true), 500); //TODO: Investigate this delay
	}
	
	public static boolean onCreatureNeedsSupport(NpcAI2 npcAI, Creature notMyTarget) {
		Npc owner = npcAI.getOwner();
		if (notMyTarget == owner || notMyTarget.getTarget() == owner) {
			/*
			 * This can happen, and arguably shouldn't. Either way, something about how
			 * things are handled here (perhaps the packet that gets sent) makes things on
			 * the client go a bit screwy when it does happen. No need for the AI to support
			 * itself, it should already be aware.
			 */
			return false;
		}
		if (TribeRelationService.isSupport(notMyTarget, owner) && MathUtil.isInRange(owner, notMyTarget, owner.getAggroRange()) && GeoService.getInstance().canSee(owner, notMyTarget)) {
			VisibleObject myTarget = notMyTarget.getTarget();
			if (myTarget != null && myTarget instanceof Creature) {
				if (npcAI.isNonFightingState()) {
					//Doesn't need to send a packet or add aggro if it's already fighting something.
					Creature targetCreature = (Creature) myTarget;
					PacketSendUtility.broadcastPacket(owner, new SM_ATTACK(owner, targetCreature, 0, 633, 0, Collections.singletonList(new AttackResult(0, AttackStatus.NORMALHIT))));
					ThreadPoolManager.getInstance().schedule(new AggroNotifier(owner, targetCreature, false), 500); //TODO: Investigate this delay
				}
				return true;
			}
		}
		return false;
	}
	
	public static boolean onGuardAgainstAttacker(NpcAI2 npcAI, Creature attacker) {
		Npc owner = npcAI.getOwner();
		if (attacker == owner || attacker.getTarget() == owner) {
			/*
			 * This can happen, and arguably shouldn't. Either way, no need for the AI to support
			 * itself, it should already be aware.
			 */
			return false;
		}
		TribeClass tribe = owner.getTribe();
		if (!tribe.isGuard() && owner.getObjectTemplate().getNpcTemplateType() != NpcTemplateType.GUARD) {
			return false;
		}
		VisibleObject target = attacker.getTarget();
		if (target != null && target instanceof Player) {
			Player playerTarget = (Player) target;
			if (!owner.isEnemy(playerTarget) && owner.isEnemy(attacker) && MathUtil.isInRange(owner, playerTarget, owner.getAggroRange()) && GeoService.getInstance().canSee(owner, attacker)) {
				owner.getAggroList().startHate(attacker);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @modified Yon (Aion Reconstruction Project) -- added different handling for Mechanics System.
	 */
	private static final class AggroNotifier implements Runnable {
		
		private Npc aggressive;
		private Creature target;
		private boolean broadcast;
		
		AggroNotifier(Npc aggressive, Creature target, boolean broadcast) {
			this.aggressive = aggressive;
			this.target = target;
			this.broadcast = broadcast;
		}
		
		@Override
		public void run() {
			aggressive.getAggroList().addHate(target, 1);
			if (broadcast) {
				aggressive.getKnownList().doOnAllNpcs(new Visitor<Npc>() {
					
					@Override
					public void visit(Npc object) {
//						object.getAi2().onCreatureEvent(AIEventType.CREATURE_NEEDS_SUPPORT, aggressive);
						AI2 ai = object.getAi2();
						if (ai instanceof AbstractMechanicsAI2) {
							//Mechanic AI's are different.
							((AbstractMechanicsAI2) ai).onSeeCreatureAggro(aggressive, target);
						} else {
							ai.onCreatureEvent(AIEventType.CREATURE_NEEDS_SUPPORT, aggressive);
						}
					}
				});
			}
			aggressive = null;
			target = null;
		}
	}
}

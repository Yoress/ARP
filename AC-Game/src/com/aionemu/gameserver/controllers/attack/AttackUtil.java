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
package com.aionemu.gameserver.controllers.attack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.ItemAttackType;
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_SELECTED;
import com.aionemu.gameserver.skillengine.change.Func;
import com.aionemu.gameserver.skillengine.effect.modifier.ActionModifier;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.HitType;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.StatFunctions;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * @author ATracer
 * @modified Yon (Aion Reconstruction Project) -- {@link #calculateMainHandResult(Creature, Creature, AttackStatus, int, List)} tweaked
 * to no longer have more than one hit for Npc's, {@link #splitPhysicalDamage(Creature, Creature, int, int, AttackStatus, List)} tweaked
 * to make parrying attacks less effective (damage reduction changed from 40% to 30%) and to not allow 0 damage unless the attack was dodged,
 * {@link #calculateMagicalSkillResult(Effect, int, ActionModifier, SkillElement, boolean, boolean, boolean, Func, int, int, boolean, boolean)}
 * and {@link #calculateMagicalSkillResult(Effect, int, ActionModifier, SkillElement)} modified to return the {@link AttackStatus} of the result,
 * {@link #calculateSkillResult(Effect, int, ActionModifier, Func, int, int, int, int, boolean, boolean, boolean, boolean)} tweaked to have
 * a less effective parry (DR from 40% to 30%) and to not allow returning 0 damage unless the attack was dodged.
 */
public class AttackUtil {
	
	/**
	 * Calculate physical attack status and damage
	 */
	public static List<AttackResult> calculatePhysicalAttackResult(Creature attacker, Creature attacked) {
		AttackStatus attackerStatus = null;
		int damage = StatFunctions.calculateAttackDamage(attacker, attacked, true, SkillElement.NONE);
		List<AttackResult> attackList = new ArrayList<AttackResult>();
		AttackStatus mainHandStatus = calculateMainHandResult(attacker, attacked, attackerStatus, damage, attackList);
		
		if (attacker instanceof Player && ((Player) attacker).getEquipment().getOffHandWeaponType() != null) {
			calculateOffHandResult(attacker, attacked, mainHandStatus, attackList);
		}
		attacked.getObserveController().checkShieldStatus(attackList, null, attacker);
		return attackList;
	}
	
	/**
	 * Calculate physical attack status and damage of the MAIN hand
	 */
	private static final AttackStatus calculateMainHandResult(Creature attacker, Creature attacked, AttackStatus attackerStatus, int damage, List<AttackResult> attackList) {
		AttackStatus mainHandStatus = attackerStatus;
		if (mainHandStatus == null) {
			mainHandStatus = calculatePhysicalStatus(attacker, attacked, true);
		}
		
		int mainHandHits = 1;
		if (attacker instanceof Player) {
			Item mainHandWeapon = ((Player) attacker).getEquipment().getMainHandWeapon();
			if (mainHandWeapon != null) {
				mainHandHits = Rnd.get(1, mainHandWeapon.getItemTemplate().getWeaponStats().getHitCount());
			}
		}/* else {
			mainHandHits = Rnd.get(1, 3);
		}*/
		splitPhysicalDamage(attacker, attacked, mainHandHits, damage, mainHandStatus, attackList);
		return mainHandStatus;
	}
	
	/**
	 * Calculate physical attack status and damage of the OFF hand
	 */
	private static final void calculateOffHandResult(Creature attacker, Creature attacked, AttackStatus mainHandStatus, List<AttackResult> attackList) {
		AttackStatus offHandStatus = AttackStatus.getOffHandStats(mainHandStatus);
		Item offHandWeapon = ((Player) attacker).getEquipment().getOffHandWeapon();
		int offHandDamage = StatFunctions.calculateAttackDamage(attacker, attacked, false, SkillElement.NONE);
		int offHandHits = Rnd.get(1, offHandWeapon.getItemTemplate().getWeaponStats().getHitCount());
		splitPhysicalDamage(attacker, attacked, offHandHits, offHandDamage, offHandStatus, attackList);
	}
	
	/**
	 * Generate attack results based on weapon hit count
	 */
	private static final List<AttackResult> splitPhysicalDamage(final Creature attacker, final Creature attacked, int hitCount, int damage, AttackStatus status, List<AttackResult> attackList) {
		WeaponType weaponType;
		
		switch (AttackStatus.getBaseStatus(status)) {
		case BLOCK:
			int reduce = damage - attacked.getGameStats().getPositiveReverseStat(StatEnum.DAMAGE_REDUCE, damage);
			if (attacked instanceof Player) {
				Item shield = ((Player) attacked).getEquipment().getEquippedShield();
				if (shield != null) {
					int reduceMax = shield.getItemTemplate().getWeaponStats().getReduceMax();
					if (reduceMax > 0 && reduceMax < reduce) {
						reduce = reduceMax;
					}
				}
			}
			damage -= reduce;
			if (damage < 1) damage = 1;
			break;
		case DODGE:
			damage = 0;
			break;
		case PARRY:
			damage *= 0.7;
			if (damage < 1) damage = 1;
			break;
		default:
			break;
		}
		
		if (status.isCritical()) {
			if (attacker instanceof Player) {
				weaponType = ((Player) attacker).getEquipment().getMainHandWeaponType();
				damage = (int) calculateWeaponCritical(attacked, damage, weaponType, StatEnum.PHYSICAL_CRITICAL_DAMAGE_REDUCE);
				// Proc Stumble/Stagger on Crit calculation
				applyEffectOnCritical((Player) attacker, attacked);
			} else {
				damage = (int) calculateWeaponCritical(attacked, damage, null, StatEnum.PHYSICAL_CRITICAL_DAMAGE_REDUCE);
			}
		}
		
		if (damage < 1) {
			damage = 0;
		}
		
		int firstHit = (int) (damage * (1f - (0.1f * (hitCount - 1))));
		if (firstHit == 0 && damage > 0) firstHit = 1;
		int otherHits = Math.round(damage * 0.1f);
		for (int i = 0; i < hitCount; i++) {
			int dmg = (i == 0 ? firstHit : otherHits);
			if (dmg > 0 | i == 0) attackList.add(new AttackResult(dmg, status, HitType.PHHIT));
		}
		return attackList;
	}
	
	/**
	 * @param damages
	 * @param weaponType
	 * @return
	 */
	private static float calculateWeaponCritical(Creature attacked, float damages, WeaponType weaponType, StatEnum stat) {
		return calculateWeaponCritical(attacked, damages, weaponType, 0, stat);
	}
	
	private static float calculateWeaponCritical(Creature attacked, float damages, WeaponType weaponType, int critAddDmg, StatEnum stat) {
		float coeficient = 2f;
		
		if (weaponType != null) {
			switch (weaponType) {
			case GUN_1H:
			case DAGGER_1H:
				coeficient = 2.3f;
				break;
			case SWORD_1H:
				coeficient = 2.2f;
				break;
			case MACE_1H:
				coeficient = 2f;
				break;
			case SWORD_2H:
			case CANNON_2H:
			case POLEARM_2H:
			case KEYBLADE_2H:
			case KEYHAMMER_2H:
				coeficient = 1.8f;
				break;
			case STAFF_2H:
			case BOW:
				coeficient = 1.7f;
				break;
			default:
				coeficient = 1.5f;
				break;
			}
			
			if (stat.equals(StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE)) {
				coeficient = 1.5f; // Magical skill with physical weapon TODO: confirm this
			}
		}
		
		if (attacked instanceof Player) { // Strike Fortitude lowers the crit multiplier
			Player player = (Player) attacked;
			int fortitude = 0;
			switch (stat) {
			case PHYSICAL_CRITICAL_DAMAGE_REDUCE:
			case MAGICAL_CRITICAL_DAMAGE_REDUCE:
				fortitude = player.getGameStats().getStat(stat, 0).getCurrent();
				coeficient -= Math.round(fortitude / 1000f);
				break;
			default:
				break;
			}
		}
		
		// add critical add dmg
		coeficient += (float) critAddDmg / 100f;
		
		damages = Math.round(damages * coeficient);
		
		if (attacked instanceof Npc) {
			damages = attacked.getAi2().modifyDamage((int) damages);
		}
		
		return damages;
	}
	
	/**
	 * @param effect
	 * @param skillDamage
	 * @param func (add/percent)
	 * @param randomDamage
	 * @param accMod
	 */
	public static void calculateSkillResult(Effect effect, int skillDamage, ActionModifier modifier, Func func, int randomDamage, int accMod, int criticalProb, int critAddDmg, boolean cannotMiss, boolean shared, boolean ignoreShield, boolean isMainHand) {
		Creature effector = effect.getEffector();
		Creature effected = effect.getEffected();
		
		int damage = 0;
		int baseAttack = 0;
		if (effector.getAttackType() == ItemAttackType.PHYSICAL) {
			baseAttack = effector.getGameStats().getMainHandPAttack().getBase();
			damage = StatFunctions.calculatePhysicalAttackDamage(effector, effected, true);
		} else {
			if (isMainHand) {
				baseAttack = effector.getGameStats().getMainHandMAttack().getBase();
			} else {
				baseAttack = effector.getGameStats().getOffHandMAttack().getBase();
			}
			damage = StatFunctions.calculateMagicalAttackDamage(effector, effected, effector.getAttackType().getMagicalElement(), isMainHand);
		}
		
		// add skill damage
		if (func != null) {
			switch (func) {
			case ADD:
				damage += skillDamage;
				break;
			case PERCENT:
				damage += baseAttack * skillDamage / 100f;
				break;
			default:
				break;
			}
		}
		
		// add bonus damage
		if (modifier != null) {
			int bonus = modifier.analyze(effect);
			switch (modifier.getFunc()) {
			case ADD:
				damage += bonus;
				break;
			case PERCENT:
				damage += baseAttack * bonus / 100f;
				break;
			default:
				break;
			}
		}
		
		// adjusting baseDamages according to attacker and target level
		damage = (int) StatFunctions.adjustDamages(effect.getEffector(), effect.getEffected(), damage, effect.getPvpDamage(), true);
		
		float damageMultiplier = effector.getObserveController().getBasePhysicalDamageMultiplier(true);
		damage = Math.round(damage * damageMultiplier);
		
		// implementation of random damage for skills like Stunning Shot, etc
		if (randomDamage > 0) {
			int randomChance = Rnd.get(100);
			// TODO Hard fix
			if (effect.getSkillId() == 20033) {
				damage *= 10;
			}
			
			switch (randomDamage) {
			case 1:
				if (randomChance <= 40) {
					damage /= 2;
				} else if (randomChance <= 70) {
					damage *= 1.5;
				}
				break;
			case 2:
				if (randomChance <= 25) {
					damage *= 3;
				}
				break;
			case 6:
				if (randomChance <= 30) {
					damage *= 2;
				}
				break;
			// TODO rest of the cases
			default:
				/*
				 * chance to do from 50% to 200% damage This must NOT be calculated after critical status check, or it will be over powered and not
				 * retail
				 */
				damage *= (Rnd.get(25, 100) * 0.02f);
				break;
			}
		}
		
		AttackStatus status = AttackStatus.NORMALHIT;
		if (effector.getAttackType() == ItemAttackType.PHYSICAL) {
			status = calculatePhysicalStatus(effector, effected, true, accMod, criticalProb, true, cannotMiss);
		} else {
			status = calculateMagicalStatus(effector, effected, criticalProb, true);
		}
		
		switch (AttackStatus.getBaseStatus(status)) {
			case BLOCK:
				int reduce = damage - effected.getGameStats().getPositiveReverseStat(StatEnum.DAMAGE_REDUCE, damage);
				if (effected instanceof Player) {
					Item shield = ((Player) effected).getEquipment().getEquippedShield();
					if (shield != null) {
						int reduceMax = shield.getItemTemplate().getWeaponStats().getReduceMax();
						if (reduceMax > 0 && reduceMax < reduce) {
							reduce = reduceMax;
						}
					}
				}
				damage -= reduce;
				break;
			case PARRY:
				damage *= 0.7;
				break;
			default:
				break;
		}
		
		if (status.isCritical()) {
			if (effector instanceof Player) {
				WeaponType weaponType = ((Player) effector).getEquipment().getMainHandWeaponType();
				damage = (int) calculateWeaponCritical(effected, damage, weaponType, critAddDmg, StatEnum.PHYSICAL_CRITICAL_DAMAGE_REDUCE);
				// Proc Stumble/Stagger on Crit calculation
				applyEffectOnCritical((Player) effector, effected);
			} else {
				damage = (int) calculateWeaponCritical(effected, damage, null, critAddDmg, StatEnum.PHYSICAL_CRITICAL_DAMAGE_REDUCE);
			}
		}
		
		if (effected instanceof Npc) {
			damage = effected.getAi2().modifyDamage(damage);
		}
		if (effector instanceof Npc) {
			damage = effector.getAi2().modifyOwnerDamage(damage);
		}
		
		if (shared && !effect.getSkill().getEffectedList().isEmpty()) {
			damage /= effect.getSkill().getEffectedList().size();
		}
		
		if (damage < 1) {
			damage = 1; //TODO: Investigate
			if (AttackStatus.getBaseStatus(status) == AttackStatus.DODGE) damage = 0;
		}
		
		calculateEffectResult(effect, effected, damage, status, HitType.PHHIT, ignoreShield);
	}
	
	/**
	 * @param effect
	 * @param effected
	 * @param damage
	 * @param status
	 * @param hitType
	 */
	private static void calculateEffectResult(Effect effect, Creature effected, int damage, AttackStatus status, HitType hitType, boolean ignoreShield) {
		AttackResult attackResult = new AttackResult(damage, status, hitType);
		if (!ignoreShield) {
			effected.getObserveController().checkShieldStatus(Collections.singletonList(attackResult), effect, effect.getEffector());
		}
		effect.setReserved1(attackResult.getDamage());
		effect.setAttackStatus(attackResult.getAttackStatus());
		effect.setLaunchSubEffect(attackResult.isLaunchSubEffect());
		effect.setReflectedDamage(attackResult.getReflectedDamage());
		effect.setReflectedSkillId(attackResult.getReflectedSkillId());
		effect.setMpShield(attackResult.getShieldMp());
		effect.setProtectedDamage(attackResult.getProtectedDamage());
		effect.setProtectedSkillId(attackResult.getProtectedSkillId());
		effect.setProtectorId(attackResult.getProtectorId());
		effect.setShieldDefense(attackResult.getShieldType());
	}
	
	public static List<AttackResult> calculateMagicalAttackResult(Creature attacker, Creature attacked, SkillElement elem) {
		/*
		 * int damage = StatFunctions.calculateAttackDamage(attacker, attacked, true, elem);
		 * 
		 * AttackStatus status = calculateMagicalStatus(attacker, attacked, 100, false); List<AttackResult> attackList = new
		 * ArrayList<AttackResult>(); switch (status) { case RESIST: damage = 0; break; case CRITICAL: if (attacker instanceof Player) damage = (int)
		 * calculateWeaponCritical(attacked, damage, ((Player) attacker).getEquipment().getMainHandWeaponType(),
		 * StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE); else damage = (int) calculateWeaponCritical(attacked, damage, null,
		 * StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE); break; } attackList.add(new AttackResult(damage, status));
		 * attacked.getObserveController().checkShieldStatus(attackList, attacker); return attackList;
		 */
		List<AttackResult> attackList = new ArrayList<AttackResult>();
		
		int damage = StatFunctions.calculateAttackDamage(attacker, attacked, true, elem);
		
		// calculate status
		AttackStatus status = calculateMagicalStatus(attacker, attacked, 100, false);
		
		if (status == AttackStatus.CRITICAL) {
			damage = (int) calculateWeaponCritical(attacked, damage, ((Player) attacker).getEquipment().getMainHandWeaponType(), StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE);
		}
		
		// adjusting baseDamages according to attacker and target level
		damage = (int) StatFunctions.adjustDamages(attacker, attacked, damage, 0, false);
		
		if (damage <= 0) {
			damage = 1;
		}
		
		switch (status) {
		case RESIST:
		case CRITICAL_RESIST:
			damage = 0;
			break;
		default:
			break;
		}
		
		attackList.add(new AttackResult(damage, status));
		
		// calculate offhand damage
		if (attacker instanceof Player && ((Player) attacker).getEquipment().getOffHandWeaponType() != null) {
			int offHandDamage = StatFunctions.calculateAttackDamage(attacker, attacked, false, elem);
			
			AttackStatus offHandStatus = calculateMagicalStatus(attacker, attacked, 100, false);
			if (offHandStatus == AttackStatus.CRITICAL) {
				offHandDamage = (int) calculateWeaponCritical(attacked, damage, ((Player) attacker).getEquipment().getMainHandWeaponType(), StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE);
			}
			
			offHandDamage = (int) StatFunctions.adjustDamages(attacker, attacked, damage, 0, false);
			
			if (offHandDamage <= 0) {
				offHandDamage = 1;
			}
			
			switch (offHandStatus) {
			case RESIST:
			case CRITICAL_RESIST:
				offHandDamage = 0;
				break;
			default:
				break;
			}
			
			attackList.add(new AttackResult(offHandDamage, status));
		}
		
		// check for shield
		attacked.getObserveController().checkShieldStatus(attackList, null, attacker);
		
		return attackList;
	}
	
	public static List<AttackResult> calculateHomingAttackResult(Creature attacker, Creature attacked, SkillElement elem) {
		int damage = StatFunctions.calculateAttackDamage(attacker, attacked, true, elem);
		
		AttackStatus status = calculateHomingAttackStatus(attacker, attacked);
		List<AttackResult> attackList = new ArrayList<AttackResult>();
		switch (status) {
		case RESIST:
		case DODGE:
			damage = 0;
			break;
		case PARRY:
			damage *= 0.6;
			break;
		case BLOCK:
			damage /= 2;
			break;
		default:
			break;
		}
		attackList.add(new AttackResult(damage, status));
		attacked.getObserveController().checkShieldStatus(attackList, null, attacker);
		return attackList;
	}
	
	/**
	 * @param effect
	 * @param skillDamage
	 * @param element
	 * @param position
	 * @param useMagicBoost
	 * @param criticalProb
	 * @param critAddDmg
	 * @return
	 */
	public static int calculateMagicalOverTimeSkillResult(Effect effect, int skillDamage, SkillElement element, int position, boolean useMagicBoost, int criticalProb, int critAddDmg) {
		Creature effector = effect.getEffector();
		Creature effected = effect.getEffected();
		
		// TODO is damage multiplier used on dot?
		float damageMultiplier = effector.getObserveController().getBaseMagicalDamageMultiplier();
		
		int damage = Math.round(
				StatFunctions.calculateMagicalSkillDamage(effect.getEffector(), effect.getEffected(), skillDamage, 0, element, useMagicBoost, false, false, effect.getSkillTemplate().getPvpDamage())
						* damageMultiplier);
		
		AttackStatus status = effect.getAttackStatus();
		// calculate attack status only if it has not been forced already
		if (status == AttackStatus.NORMALHIT && position == 1) {
			status = calculateMagicalStatus(effector, effected, criticalProb, true);
		}
		switch (status) {
		case CRITICAL:
			if (effector instanceof Player) {
				WeaponType weaponType = ((Player) effector).getEquipment().getMainHandWeaponType();
				damage = (int) calculateWeaponCritical(effected, damage, weaponType, critAddDmg, StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE);
			} else {
				damage = (int) calculateWeaponCritical(effected, damage, null, critAddDmg, StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE);
			}
			break;
		default:
			break;
		}
		
		if (damage <= 0) {
			damage = 1;
		}
		
		if (effected instanceof Npc) {
			damage = effected.getAi2().modifyDamage(damage);
		}
		
		return damage;
	}
	
	/**
	 * @param effect
	 * @param skillDamage
	 * @param element
	 * @param isNoReduceSpell
	 */
	public static AttackStatus calculateMagicalSkillResult(Effect effect, int skillDamage, ActionModifier modifier, SkillElement element) {
		return calculateMagicalSkillResult(effect, skillDamage, modifier, element, true, true, false, Func.ADD, 100, 0, false, false);
	}
	
	public static AttackStatus calculateMagicalSkillResult(Effect effect, int skillDamage, ActionModifier modifier, SkillElement element, boolean useMagicBoost, boolean useKnowledge, boolean noReduce, Func func, int criticalProb, int critAddDmg, boolean shared, boolean ignoreShield) {
		Creature effector = effect.getEffector();
		Creature effected = effect.getEffected();
		
		float damageMultiplier = effector.getObserveController().getBaseMagicalDamageMultiplier();
		int baseAttack = effector.getGameStats().getMainHandPAttack().getBase(); // Npc spells scale with this
		int damages = 0;
		int bonus = 0;
		
		if (func.equals(Func.PERCENT) && effector instanceof Npc) {
			damages = Math.round(baseAttack * skillDamage / 100f);
		} else {
			damages = skillDamage;
		}
		
		// add bonus damage
		if (modifier != null) {
			bonus = modifier.analyze(effect);
			switch (modifier.getFunc()) {
				case ADD:
					break;
				case PERCENT:
					if (effector instanceof Npc) {
						bonus = Math.round(baseAttack * bonus / 100f);
					}
					break;
				default: break;
			}
		}
		
		int damage = Math.round(StatFunctions.calculateMagicalSkillDamage(effect.getEffector(), effect.getEffected(), damages, bonus, element, useMagicBoost, useKnowledge, noReduce,
				effect.getSkillTemplate().getPvpDamage()) * damageMultiplier);
		
		AttackStatus status = calculateMagicalStatus(effector, effected, criticalProb, true);
		switch (status) {
			case CRITICAL:
				if (effector instanceof Player) {
					WeaponType weaponType = ((Player) effector).getEquipment().getMainHandWeaponType();
					damage = (int) calculateWeaponCritical(effected, damage, weaponType, critAddDmg, StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE);
				} else {
					damage = (int) calculateWeaponCritical(effected, damage, null, critAddDmg, StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE);
				}
				break;
			default: break;
		}
		if (shared && !effect.getSkill().getEffectedList().isEmpty()) {
			damage /= effect.getSkill().getEffectedList().size();
		}
		
		calculateEffectResult(effect, effected, damage, status, HitType.MAHIT, ignoreShield);
		return status;
	}
	
	/**
	 * Manage attack status rate
	 *
	 * @return AttackStatus
	 * @source http://www.aionsource.com/forum/mechanic-analysis/42597-character-stats-xp-dp-origin-gerbator-team-july-2009 -a.html
	 */
	public static AttackStatus calculatePhysicalStatus(Creature attacker, Creature attacked, boolean isMainHand) {
		return calculatePhysicalStatus(attacker, attacked, isMainHand, 0, 100, false, false);
	}
	
	public static AttackStatus calculatePhysicalStatus(Creature attacker, Creature attacked, boolean isMainHand, int accMod, int criticalProb, boolean isSkill, boolean cannotMiss) {
		AttackStatus status = AttackStatus.NORMALHIT;
		if (!isMainHand) {
			status = AttackStatus.OFFHAND_NORMALHIT;
		}
		
		if (!cannotMiss) {
			if (attacked instanceof Player && ((Player) attacked).getEquipment().isShieldEquipped() && StatFunctions.calculatePhysicalBlockRate(attacker, attacked))// TODO accMod
			{
				status = AttackStatus.BLOCK;
			} // Parry can only be done with weapon, also weapon can have humanoid mobs, but for now there isnt implementation of monster category
			else if (attacked instanceof Player && ((Player) attacked).getEquipment().getMainHandWeaponType() != null && StatFunctions.calculatePhysicalParryRate(attacker, attacked))// TODO accMod
			{
				status = AttackStatus.PARRY;
			} else if (!isSkill && StatFunctions.calculatePhysicalDodgeRate(attacker, attacked, accMod)) {
				status = AttackStatus.DODGE;
			}
		} else {
			/**
			 * Check AlwaysDodge Check AlwaysParry Check AlwaysBlock
			 */
			StatFunctions.calculatePhysicalDodgeRate(attacker, attacked, accMod);
			StatFunctions.calculatePhysicalParryRate(attacker, attacked);
			StatFunctions.calculatePhysicalBlockRate(attacker, attacked);
		}
		
		if (StatFunctions.calculatePhysicalCriticalRate(attacker, attacked, isMainHand, criticalProb, isSkill)) {
			switch (status) {
			case BLOCK:
				if (isMainHand) {
					status = AttackStatus.CRITICAL_BLOCK;
				} else {
					status = AttackStatus.OFFHAND_CRITICAL_BLOCK;
				}
				break;
			case PARRY:
				if (isMainHand) {
					status = AttackStatus.CRITICAL_PARRY;
				} else {
					status = AttackStatus.OFFHAND_CRITICAL_PARRY;
				}
				break;
			case DODGE:
				if (isMainHand) {
					status = AttackStatus.CRITICAL_DODGE;
				} else {
					status = AttackStatus.OFFHAND_CRITICAL_DODGE;
				}
				break;
			default:
				if (isMainHand) {
					status = AttackStatus.CRITICAL;
				} else {
					status = AttackStatus.OFFHAND_CRITICAL;
				}
				break;
			}
		}
		
		return status;
	}
	
	/**
	 * Every + 100 delta of (MR - MA) = + 10% to resist<br>
	 * if the difference is 1000 = 100% resist
	 */
	public static AttackStatus calculateMagicalStatus(Creature attacker, Creature attacked, int criticalProb, boolean isSkill) {
		if (!isSkill) {
			if (Rnd.get(0, 1000) < StatFunctions.calculateMagicalResistRate(attacker, attacked, 0)) {
				return AttackStatus.RESIST;
			}
		}
		
		if (StatFunctions.calculateMagicalCriticalRate(attacker, attacked, criticalProb)) {
			return AttackStatus.CRITICAL;
		}
		
		return AttackStatus.NORMALHIT;
	}
	
	private static AttackStatus calculateHomingAttackStatus(Creature attacker, Creature attacked) {
		if (Rnd.get(0, 1000) < StatFunctions.calculateMagicalResistRate(attacker, attacked, 0)) {
			return AttackStatus.RESIST;
		} else if (StatFunctions.calculatePhysicalDodgeRate(attacker, attacked, 0)) {
			return AttackStatus.DODGE;
		} else if (StatFunctions.calculatePhysicalParryRate(attacker, attacked)) {
			return AttackStatus.PARRY;
		} else if (StatFunctions.calculatePhysicalBlockRate(attacker, attacked)) {
			return AttackStatus.BLOCK;
		} else {
			return AttackStatus.NORMALHIT;
		}
		
	}
	
	public static void cancelCastOn(final Creature target) {
		target.getKnownList().doOnAllPlayers(new Visitor<Player>() {
			
			@Override
			public void visit(Player observer) {
				if (observer.getTarget() == target) {
					cancelCast(observer, target);
				}
			}
		});
		
		target.getKnownList().doOnAllNpcs(new Visitor<Npc>() {
			
			@Override
			public void visit(Npc observer) {
				if (observer.getTarget() == target) {
					cancelCast(observer, target);
				}
			}
		});
		
	}
	
	private static void cancelCast(Creature creature, Creature target) {
		if (target != null && creature.getCastingSkill() != null) {
			if (creature.getCastingSkill().getFirstTarget().equals(target)) {
				creature.getController().cancelCurrentSkill();
			}
		}
	}
	
	/**
	 * Send a packet to everyone who is targeting creature.
	 *
	 * @param object
	 */
	public static void removeTargetFrom(final Creature object) {
		removeTargetFrom(object, false);
	}
	
	public static void removeTargetFrom(final Creature object, final boolean validateSee) {
		object.getKnownList().doOnAllPlayers(new Visitor<Player>() {
			
			@Override
			public void visit(Player observer) {
				if (validateSee && observer.getTarget() == object) {
					if (!observer.canSee(object)) {
						observer.setTarget(null);
						// retail packet (//fsc 0x44 dhdd 0 0 0 0) right after
						// SM_PLAYER_STATE
						PacketSendUtility.sendPacket(observer, new SM_TARGET_SELECTED(observer));
					}
				} else if (observer.getTarget() == object) {
					observer.setTarget(null);
					// retail packet (//fsc 0x44 dhdd 0 0 0 0) right after
					// SM_PLAYER_STATE
					PacketSendUtility.sendPacket(observer, new SM_TARGET_SELECTED(observer));
				}
			}
		});
	}
	
	public static void applyEffectOnCritical(Player attacker, Creature attacked) {
		int skillId = 0;
		WeaponType mainHandWeaponType = attacker.getEquipment().getMainHandWeaponType();
		if (mainHandWeaponType != null) {
			switch (mainHandWeaponType) {
			case POLEARM_2H:
			case CANNON_2H:
			case STAFF_2H:
			case SWORD_2H:
			case KEYBLADE_2H:
			case KEYHAMMER_2H:
				skillId = 8218;
				break;
			case BOW:
				skillId = 8217;
			default:
				break;
			}
		}
		
		if (skillId == 0) {
			return;
		}
		// On retail this effect apply on each crit with 10% of base chance
		// plus bonus effect penetration calculated above
		if (Rnd.get(100) > (6 * attacked.getCriticalEffectMulti())) {
			return;
		}
		
		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (template == null) {
			return;
		}
		Effect e = new Effect(attacker, attacked, template, template.getLvl(), 0);
		e.initialize();
		e.applyEffect();
	}
}

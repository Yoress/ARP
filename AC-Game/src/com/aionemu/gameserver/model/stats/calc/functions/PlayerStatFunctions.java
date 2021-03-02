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
package com.aionemu.gameserver.model.stats.calc.functions;

import static ch.lambdaj.Lambda.forEach;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectMax;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * @author ATracer
 */
public class PlayerStatFunctions {
	
	private static final List<IStatFunction> FUNCTIONS = new ArrayList<IStatFunction>();
	
	static {
		FUNCTIONS.add(new PhysicalAttackFunction());
		FUNCTIONS.add(new MagicalAttackFunction());
		FUNCTIONS.add(new AttackSpeedFunction());
		FUNCTIONS.add(new BoostCastingTimeFunction());
		FUNCTIONS.add(new PvPAttackRatioFunction());
		FUNCTIONS.add(new PDefFunction());
		FUNCTIONS.add(new MaxHpFunction());
		FUNCTIONS.add(new MaxMpFunction());
		
		FUNCTIONS.add(new AgilityModifierFunction(StatEnum.BLOCK, 0.25f));
		FUNCTIONS.add(new AgilityModifierFunction(StatEnum.PARRY, 0.25f));
		FUNCTIONS.add(new AgilityModifierFunction(StatEnum.EVASION, 0.3f));
	}
	
	public static final List<IStatFunction> getFunctions() {
		return FUNCTIONS;
	}
	
	public static final void addPredefinedStatFunctions(Player player) {
		player.getGameStats().addEffectOnly(null, FUNCTIONS);
	}
}

class PhysicalAttackFunction extends StatFunction {
	
	PhysicalAttackFunction() {
		stat = StatEnum.PHYSICAL_ATTACK;
	}
	
	@Override
	public void apply(Stat2 stat) {
		float power = stat.getOwner().getGameStats().getPower().getCurrent();
		stat.setBase(Math.round(stat.getBase() * power / 100f));
	}
	
	@Override
	public int getPriority() {
		return 30;
	}
}

class AgilityModifierFunction extends StatFunction {
	
	private float modifier;
	
	AgilityModifierFunction(StatEnum stat, float modifier) {
		this.stat = stat;
		this.modifier = modifier;
	}
	
	@Override
	public void apply(Stat2 stat) {
		float agility = stat.getOwner().getGameStats().getAgility().getCurrent();
		stat.setBase(Math.round(stat.getBase() + stat.getBase() * (agility - 100) * modifier / 100f));
	}
	
	@Override
	public int getPriority() {
		return 30;
	}
}

class MaxHpFunction extends StatFunction {
	
	MaxHpFunction() {
		stat = StatEnum.MAXHP;
	}
	
	@Override
	public void apply(Stat2 stat) {
		float health = stat.getOwner().getGameStats().getHealth().getCurrent();
		stat.setBase(Math.round(stat.getBase() * health / 100f));
	}
	
	@Override
	public int getPriority() {
		return 30;
	}
}

class MaxMpFunction extends StatFunction {
	
	MaxMpFunction() {
		stat = StatEnum.MAXMP;
	}
	
	@Override
	public void apply(Stat2 stat) {
		float will = stat.getOwner().getGameStats().getWill().getCurrent();
		stat.setBase(Math.round(stat.getBase() * will / 100f));
	}
	
	@Override
	public int getPriority() {
		return 30;
	}
}

class MagicalAttackFunction extends StatFunction {
	
	MagicalAttackFunction() {
		stat = StatEnum.MAGICAL_ATTACK;
	}
	
	@Override
	public void apply(Stat2 stat) {
		float knowledge = stat.getOwner().getGameStats().getKnowledge().getCurrent();
		stat.setBase(Math.round(stat.getBase() * knowledge / 100f));
	}
	
	@Override
	public int getPriority() {
		return 30;
	}
}

class PDefFunction extends StatFunction {
	
	PDefFunction() {
		stat = StatEnum.PHYSICAL_DEFENSE;
	}
	
	@Override
	public void apply(Stat2 stat) {
		if (stat.getOwner().isFlying()) {
			stat.setBonus(stat.getBonus() - (stat.getBase() / 2));
		}
	}
	
	@Override
	public int getPriority() {
		return 60;
	}
}

class AttackSpeedFunction extends DuplicateStatFunction {
	
	AttackSpeedFunction() {
		stat = StatEnum.ATTACK_SPEED;
	}
}

class BoostCastingTimeFunction extends DuplicateStatFunction {
	
	BoostCastingTimeFunction() {
		stat = StatEnum.BOOST_CASTING_TIME;
	}
}

class PvPAttackRatioFunction extends DuplicateStatFunction {
	
	PvPAttackRatioFunction() {
		stat = StatEnum.PVP_ATTACK_RATIO;
	}
}

class DuplicateStatFunction extends StatFunction {
	
	@Override
	public void apply(Stat2 stat) {
		Item mainWeapon = ((Player) stat.getOwner()).getEquipment().getMainHandWeapon();
		Item offWeapon = ((Player) stat.getOwner()).getEquipment().getOffHandWeapon();
		
		if (mainWeapon != null) {
			StatFunction func1 = null;
			StatFunction func2 = null;
			List<StatFunction> functions = new ArrayList<StatFunction>();
			List<StatFunction> functions1 = mainWeapon.getItemTemplate().getModifiers();
			
			if (functions1 != null) {
				List<StatFunction> f1 = getFunctions(functions1, stat, mainWeapon);
				if (!f1.isEmpty()) {
					func1 = f1.get(0);
					functions.addAll(f1);
				}
			}
			
			if (mainWeapon.hasFusionedItem()) {
				ItemTemplate template = mainWeapon.getFusionedItemTemplate();
				List<StatFunction> functions2 = template.getModifiers();
				if (functions2 != null) {
					List<StatFunction> f2 = getFunctions(functions2, stat, mainWeapon);
					if (!f2.isEmpty()) {
						func2 = f2.get(0);
						functions.addAll(f2);
					}
				}
			} else if (offWeapon != null) {
				List<StatFunction> functions2 = offWeapon.getItemTemplate().getModifiers();
				if (functions2 != null) {
					functions.addAll(getFunctions(functions2, stat, offWeapon));
				}
			}
			
			if (func1 != null && func2 != null) { // for fusioned weapons
				if (Math.abs(func1.getValue()) >= Math.abs(func2.getValue())) {
					functions.remove(func2);
				} else {
					functions.remove(func1);
				}
			}
			if (!functions.isEmpty()) {
				if (getName() == StatEnum.PVP_ATTACK_RATIO) {
					forEach(functions).apply(stat);
				} else {
					((StatFunction) selectMax(functions, on(StatFunction.class).getValue())).apply(stat);
				}
				functions.clear();
			}
		}
	}
	
	private List<StatFunction> getFunctions(List<StatFunction> list, Stat2 stat, Item item) {
		List<StatFunction> functions = new ArrayList<StatFunction>();
		for (StatFunction func : list) {
			StatFunctionProxy func2 = new StatFunctionProxy(item, func);
			if (func.getName() == getName() && func2.validate(stat, func2)) {
				functions.add(func);
			}
		}
		return functions;
	}
	
	@Override
	public int getPriority() {
		return 60;
	}
}

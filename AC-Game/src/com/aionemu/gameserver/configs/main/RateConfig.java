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
package com.aionemu.gameserver.configs.main;

import java.lang.reflect.Field;

import com.aionemu.commons.configuration.Property;
import com.aionemu.commons.configuration.PropertyTransformer;
import com.aionemu.commons.configuration.TransformationException;

import gnu.trove.map.hash.TIntFloatHashMap;

/**
 * @author ATracer
 * @author GiGatR00n v4.7.5.x
 */
public class RateConfig {
	
	/**
	 * Display server rates when player enter in world
	 */
	@Property(key = "gameserver.rate.display.rates", defaultValue = "false")
	public static boolean DISPLAY_RATE;
	/**
	 * XP Rates - Regular (1), Premium (2), VIP (3)
	 */
	@Property(key = "gameserver.rate.regular.xp", defaultValue = "1.0")
	public static float XP_RATE;
	@Property(key = "gameserver.rate.premium.xp", defaultValue = "2.0")
	public static float PREMIUM_XP_RATE;
	@Property(key = "gameserver.rate.vip.xp", defaultValue = "3.0")
	public static float VIP_XP_RATE;
	/**
	 * Group XP Rates - Regular (1), Premium (2), VIP (3)
	 */
	@Property(key = "gameserver.rate.regular.group.xp", defaultValue = "1.0")
	public static float GROUPXP_RATE;
	@Property(key = "gameserver.rate.premium.group.xp", defaultValue = "2.0")
	public static float PREMIUM_GROUPXP_RATE;
	@Property(key = "gameserver.rate.vip.group.xp", defaultValue = "3.0")
	public static float VIP_GROUPXP_RATE;
	/**
	 * Quest XP Rates - Regular (1), Premium (2), VIP (3)
	 */
	@Property(key = "gameserver.rate.regular.quest.xp", defaultValue = "2")
	public static float QUEST_XP_RATE;
	@Property(key = "gameserver.rate.premium.quest.xp", defaultValue = "4")
	public static float PREMIUM_QUEST_XP_RATE;
	@Property(key = "gameserver.rate.vip.quest.xp", defaultValue = "6")
	public static float VIP_QUEST_XP_RATE;
	/**
	 * Gathering XP Rates - Regular (1), Premium (2), VIP (3)
	 */
	@Property(key = "gameserver.rate.regular.gathering.xp", defaultValue = "1.0")
	public static float GATHERING_XP_RATE;
	@Property(key = "gameserver.rate.premium.gathering.xp", defaultValue = "2.0")
	public static float PREMIUM_GATHERING_XP_RATE;
	@Property(key = "gameserver.rate.vip.gathering.xp", defaultValue = "3.0")
	public static float VIP_GATHERING_XP_RATE;
	/**
	 * Gathering Count Rates - Regular (1), Premium (1), VIP (1)
	 */
	@Property(key = "gameserver.rate.regular.gathering.count", defaultValue = "1")
	public static int GATHERING_COUNT_RATE;
	@Property(key = "gameserver.rate.premium.gathering.count", defaultValue = "1")
	public static int PREMIUM_GATHERING_COUNT_RATE;
	@Property(key = "gameserver.rate.vip.gathering.count", defaultValue = "1")
	public static int VIP_GATHERING_COUNT_RATE;
	/**
	 * Crafting XP Rates - Regular (1), Premium (2), VIP (3)
	 */
	@Property(key = "gameserver.rate.regular.crafting.xp", defaultValue = "1.0")
	public static float CRAFTING_XP_RATE;
	@Property(key = "gameserver.rate.premium.crafting.xp", defaultValue = "2.0")
	public static float PREMIUM_CRAFTING_XP_RATE;
	@Property(key = "gameserver.rate.vip.crafting.xp", defaultValue = "3.0")
	public static float VIP_CRAFTING_XP_RATE;
	/**
	 * Quest Kinah Rates - Regular (1), Premium (2), VIP (3)
	 */
	@Property(key = "gameserver.rate.regular.quest.kinah", defaultValue = "1.0")
	public static float QUEST_KINAH_RATE;
	@Property(key = "gameserver.rate.premium.quest.kinah", defaultValue = "2.0")
	public static float PREMIUM_QUEST_KINAH_RATE;
	@Property(key = "gameserver.rate.vip.quest.kinah", defaultValue = "3.0")
	public static float VIP_QUEST_KINAH_RATE;
	/**
	 * Quest AP Rates - Regular (1), Premium (2), VIP (3)
	 */
	@Property(key = "gameserver.rate.regular.quest.ap", defaultValue = "1.0")
	public static float QUEST_AP_RATE;
	@Property(key = "gameserver.rate.premium.quest.ap", defaultValue = "2.0")
	public static float PREMIUM_QUEST_AP_RATE;
	@Property(key = "gameserver.rate.vip.quest.ap", defaultValue = "3.0")
	public static float VIP_QUEST_AP_RATE;
	/**
	 * Quest GP Rates - Regular (1), Premium (1.5), VIP (2)
	 */
	@Property(key = "gameserver.rate.regular.quest.gp", defaultValue = "1.0")
	public static float QUEST_GP_RATE;
	@Property(key = "gameserver.rate.premium.quest.gp", defaultValue = "1.5")
	public static float PREMIUM_QUEST_GP_RATE;
	@Property(key = "gameserver.rate.vip.quest.gp", defaultValue = "2.0")
	public static float VIP_QUEST_GP_RATE;
	/**
	 * Drop Rates - Regular (1), Premium (2), VIP (3)
	 */
	@Property(key = "gameserver.rate.regular.drop", defaultValue = "1.0")
	public static float DROP_RATE;
	@Property(key = "gameserver.rate.premium.drop", defaultValue = "2.0")
	public static float PREMIUM_DROP_RATE;
	@Property(key = "gameserver.rate.vip.drop", defaultValue = "3.0")
	public static float VIP_DROP_RATE;
	/**
	 * Player Abyss Points Rates (Gain) - Regular (1), Premium (2), VIP (3)
	 */
	@Property(key = "gameserver.rate.regular.ap.player.gain", defaultValue = "1.0")
	public static float AP_PLAYER_GAIN_RATE;
	@Property(key = "gameserver.rate.premium.ap.player.gain", defaultValue = "2.0")
	public static float PREMIUM_AP_PLAYER_GAIN_RATE;
	@Property(key = "gameserver.rate.vip.ap.player.gain", defaultValue = "3.0")
	public static float VIP_AP_PLAYER_GAIN_RATE;
	/**
	 * Player Experience Points Rates (Gain) - Regular (1), Premium (2), VIP (3)
	 */
	@Property(key = "gameserver.rate.regular.xp.player.gain", defaultValue = "1.0")
	public static float XP_PLAYER_GAIN_RATE;
	@Property(key = "gameserver.rate.premium.xp.player.gain", defaultValue = "2.0")
	public static float PREMIUM_XP_PLAYER_GAIN_RATE;
	@Property(key = "gameserver.rate.vip.xp.player.gain", defaultValue = "3.0")
	public static float VIP_XP_PLAYER_GAIN_RATE;
	/**
	 * Player Abyss Points Rates - Regular (1), Premium (2), VIP (3)
	 */
	@Property(key = "gameserver.rate.regular.ap.player.loss", defaultValue = "1.0")
	public static float AP_PLAYER_LOSS_RATE;
	@Property(key = "gameserver.rate.premium.ap.player.loss", defaultValue = "2.0")
	public static float PREMIUM_AP_PLAYER_LOSS_RATE;
	@Property(key = "gameserver.rate.vip.ap.player.loss", defaultValue = "3.0")
	public static float VIP_AP_PLAYER_LOSS_RATE;
	/**
	 * NPC Abyss Points Rates - Regular (1), Premium (2), VIP (3)
	 */
	@Property(key = "gameserver.rate.regular.ap.npc", defaultValue = "1.0")
	public static float AP_NPC_RATE;
	@Property(key = "gameserver.rate.premium.ap.npc", defaultValue = "2.0")
	public static float PREMIUM_AP_NPC_RATE;
	@Property(key = "gameserver.rate.vip.ap.npc", defaultValue = "3.0")
	public static float VIP_AP_NPC_RATE;
	/**
	 * NPC Glory Points Rates - Regular (1), Premium (2), VIP (3)
	 */
	@Property(key = "gameserver.rate.regular.gp.npc", defaultValue = "1.0")
	public static float GP_NPC_RATE;
	@Property(key = "gameserver.rate.premium.gp.npc", defaultValue = "2.0")
	public static float PREMIUM_GP_NPC_RATE;
	@Property(key = "gameserver.rate.vip.gp.npc", defaultValue = "3.0")
	public static float VIP_GP_NPC_RATE;
	/**
	 * PVE DP Rates - Regular (1), Premium (2), VIP (3)
	 */
	@Property(key = "gameserver.rate.regular.dp.npc", defaultValue = "1.0")
	public static float DP_NPC_RATE;
	@Property(key = "gameserver.rate.premium.dp.npc", defaultValue = "2.0")
	public static float PREMIUM_DP_NPC_RATE;
	@Property(key = "gameserver.rate.vip.dp.npc", defaultValue = "3.0")
	public static float VIP_DP_NPC_RATE;
	/**
	 * PVP DP Rates - Regular (1), Premium (2), VIP (3)
	 */
	@Property(key = "gameserver.rate.regular.dp.player", defaultValue = "1.0")
	public static float DP_PLAYER_RATE;
	@Property(key = "gameserver.rate.premium.dp.player", defaultValue = "2.0")
	public static float PREMIUM_DP_PLAYER_RATE;
	@Property(key = "gameserver.rate.vip.dp.player", defaultValue = "3.0")
	public static float VIP_DP_PLAYER_RATE;
	/**
	 * PVP Arena and Dredgion reward rates
	 */
	@Property(key = "gameserver.rate.abyss.idgeldome", defaultValue = "4")
	public static float IDGEL_DOME_ABYSS_REWARD_RATE;
	@Property(key = "gameserver.rate.glory.idgeldome", defaultValue = "1.4")
	public static float IDGEL_DOME_GLORY_REWARD_RATE;
	@Property(key = "gameserver.rate.box.idgeldome", defaultValue = "1")
	public static float IDGEL_DOME_BOX_REWARD_RATE;
	@Property(key = "gameserver.rate.dredgion", defaultValue = "5")
	public static float DREDGION_REWARD_RATE;
	@Property(key = "gameserver.rate.kamar", defaultValue = "5")
	public static float KAMAR_REWARD_RATE;
	@Property(key = "gameserver.rate.ophidan", defaultValue = "5")
	public static float OPHIDAN_REWARD_RATE;
	@Property(key = "gameserver.rate.regular.pvparena.discipline", defaultValue = "3")
	public static float PVP_ARENA_DISCIPLINE_REWARD_RATE;
	@Property(key = "gameserver.rate.premium.pvparena.discipline", defaultValue = "3")
	public static float PREMIUM_PVP_ARENA_DISCIPLINE_REWARD_RATE;
	@Property(key = "gameserver.rate.vip.pvparena.discipline", defaultValue = "3")
	public static float VIP_PVP_ARENA_DISCIPLINE_REWARD_RATE;
	@Property(key = "gameserver.rate.regular.pvparena.chaos", defaultValue = "3")
	public static float PVP_ARENA_CHAOS_REWARD_RATE;
	@Property(key = "gameserver.rate.premium.pvparena.chaos", defaultValue = "3")
	public static float PREMIUM_PVP_ARENA_CHAOS_REWARD_RATE;
	@Property(key = "gameserver.rate.vip.pvparena.chaos", defaultValue = "3")
	public static float VIP_PVP_ARENA_CHAOS_REWARD_RATE;
	@Property(key = "gameserver.rate.regular.pvparena.harmony", defaultValue = "3")
	public static float PVP_ARENA_HARMONY_REWARD_RATE;
	@Property(key = "gameserver.rate.premium.pvparena.harmony", defaultValue = "3")
	public static float PREMIUM_PVP_ARENA_HARMONY_REWARD_RATE;
	@Property(key = "gameserver.rate.vip.pvparena.harmony", defaultValue = "3")
	public static float VIP_PVP_ARENA_HARMONY_REWARD_RATE;
	@Property(key = "gameserver.rate.regular.pvparena.glory", defaultValue = "3")
	public static float PVP_ARENA_GLORY_REWARD_RATE;
	@Property(key = "gameserver.rate.premium.pvparena.glory", defaultValue = "1")
	public static float PREMIUM_PVP_ARENA_GLORY_REWARD_RATE;
	@Property(key = "gameserver.rate.vip.pvparena.glory", defaultValue = "3")
	public static float VIP_PVP_ARENA_GLORY_REWARD_RATE;
	/**
	 * Rate which affects amount of required ap for Abyss rank
	 */
	@Property(key = "gameserver.rate.ap.rank", defaultValue = "1")
	public static int ABYSS_RANK_RATE;
	/**
	 * Sell limits rate
	 */
	@Property(key = "gameserver.rate.regular.sell.limit", defaultValue = "2000")
	public static float SELL_LIMIT_RATE;
	@Property(key = "gameserver.rate.premium.sell.limit", defaultValue = "2000")
	public static float PREMIUM_SELL_LIMIT_RATE;
	@Property(key = "gameserver.rate.vip.sell.limit", defaultValue = "2000")
	public static float VIP_SELL_LIMIT_RATE;
	/**
	 * Instance reward rate like Dredgion for new instance
	 */
	@Property(key = "gameserver.rate.instance", defaultValue = "2")
	public static float INSTANCE_REWARD_RATE;
	/*
	 * Holiday Rates
	 */
	@Property(key = "gameserver.rate.holiday.enable", defaultValue = "false")
	public static boolean HOLIDAY_RATE_ENAMBLE;
	@Property(key = "gameserver.rate.holiday.regular", defaultValue = "1")
	public static int HOLIDAY_RATE_REGULAR;
	@Property(key = "gameserver.rate.holiday.premium", defaultValue = "2")
	public static int HOLIDAY_RATE_PREMIUM;
	@Property(key = "gameserver.rate.holiday.vip", defaultValue = "4")
	public static int HOLIDAY_RATE_VIP;
	@Property(key = "gameserver.rate.holiday.days", defaultValue = "1,7")
	public static String HOLIDAY_RATE_DAYS;
	/*
	 * Tempering (Authorize) Rates
	 */
	@Property(key = "gameserver.rate.tempering", defaultValue = "5")
	public static float TEMPERING_RATE;
	
	/**
	 * A map storing exp rates (entry) for levels (key). This allows server owners to adjust exp rates by level on the fly.
	 * The value must be like "[1,65]x1.5" or "[1]x1.5", where "1" is the low end of the level bracket you want to adjust,
	 * "65" is the high end, and "x1.5" is the multiplier you want to apply. This accepts multiple brackets, separated by spaces.
	 */
	@Property(key = "gameserver.rate.per.level.all.exp.rates", defaultValue = "", propertyTransformer = LevelRangeExpRateTransformer.class)
	public static TIntFloatHashMap ALL_EXP_BONUS_BY_LEVEL;
	
	public final static class LevelRangeExpRateTransformer implements PropertyTransformer<TIntFloatHashMap> {
		@Override
		public TIntFloatHashMap transform(String value, Field field) throws TransformationException {
			if (value.isEmpty()) return new TIntFloatHashMap(0, 0.5F, 0, 1F);
			TIntFloatHashMap ret = new TIntFloatHashMap(10, 0.5F, 0, 1F);
			String[] values = value.trim().split(" ");
			
			//We're parsing this pattern: [23]x1.5
			//We're also accepting this pattern: [23,25]x1.5
			for (String val: values) {
				if (!val.startsWith("[")) throw new TransformationException("Cannot transform: " + val);
				
				int rightBracket;
				if ((rightBracket = val.lastIndexOf(']')) == -1) throw new TransformationException("Cannot transform: " + val);
				float expRateModifier = 1;
				try {
					String xRate = val.substring(rightBracket + 1);
					if (!(xRate.startsWith("x") || xRate.startsWith("X"))) throw new TransformationException("Cannot transform: " + val);
					expRateModifier = Float.parseFloat(xRate.substring(1));
				} catch (IndexOutOfBoundsException e) {
					throw new TransformationException("Cannot transform: " + val, e);
				} catch (NumberFormatException e) {
					throw new TransformationException("Cannot transform: " + val, e);
				}
				
				
				String levelRange = val.substring(1, rightBracket);
				if (levelRange.indexOf(',') != levelRange.lastIndexOf(',')) throw new TransformationException("Cannot transform: " + val);
				String[] levels = levelRange.split(",");
				if (levels.length == 2) {
					try {
						int low = Integer.parseInt(levels[0]);
						int high = Integer.parseInt(levels[1]);
						if (low > high) throw new TransformationException("Cannot transform: " + val);
						for (int i = low; i <= high; i++) {
							ret.put(i, expRateModifier);
						}
					} catch (NumberFormatException e) {
						throw new TransformationException("Cannot transform: " + val, e);
					}
				} else if (levels.length == 1) {
					try {
						int level = Integer.parseInt(levels[0]);
						ret.put(level, expRateModifier);
					} catch (NumberFormatException e) {
						throw new TransformationException("Cannot transform: " + val, e);
					}
				} else {
					//Shouldn't be possible
					throw new TransformationException("Cannot transform: " + val);
				}
			}
			ret.compact();
			return ret;
		}
	};
}

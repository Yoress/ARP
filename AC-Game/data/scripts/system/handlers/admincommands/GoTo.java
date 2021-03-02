/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 * Aion-Lightning is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your option) any later version.
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
package admincommands;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldMapType;

/**
 * Goto command
 *
 * @author Dwarfpicker
 * @Reworked Imaginary
 * @author GiGatR00n v4.7.5.x
 */
public class GoTo extends AdminCommand {
	
	public GoTo() {
		super("goto");
	}
	
	@Override
	public void execute(Player player, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(player, "syntax //goto <location>");
			return;
		}
		
		StringBuilder sbDestination = new StringBuilder();
		for (String p : params) {
			sbDestination.append(p + " ");
		}
		
		String destination = sbDestination.toString().trim();
		
		/**
		 * Elysea
		 */
		// Sanctum
		if (destination.equalsIgnoreCase("Sanctum")) {
			goTo(player, WorldMapType.SANCTUM.getId(), 1322, 1511, 568);
		} // Kaisinel
		else if (destination.equalsIgnoreCase("Kaisinel")) {
			goTo(player, WorldMapType.KAISINEL.getId(), 2155, 1567, 1205);
		} // Poeta
		else if (destination.equalsIgnoreCase("Poeta")) {
			goTo(player, WorldMapType.POETA.getId(), 806, 1242, 119);
		} else if (destination.equalsIgnoreCase("Melponeh")) {
			goTo(player, WorldMapType.POETA.getId(), 426, 1740, 119);
		} // Verteron
		else if (destination.equalsIgnoreCase("Verteron")) {
			goTo(player, WorldMapType.VERTERON.getId(), 1643, 1500, 119);
		} else if (destination.equalsIgnoreCase("Cantas") || destination.equalsIgnoreCase("Cantas Coast")) {
			goTo(player, WorldMapType.VERTERON.getId(), 2384, 788, 102);
		} else if (destination.equalsIgnoreCase("Ardus") || destination.equalsIgnoreCase("Ardus Shrine")) {
			goTo(player, WorldMapType.VERTERON.getId(), 2333, 1817, 193);
		} else if (destination.equalsIgnoreCase("Pilgrims") || destination.equalsIgnoreCase("Pilgrims Respite")) {
			goTo(player, WorldMapType.VERTERON.getId(), 2063, 2412, 274);
		} else if (destination.equalsIgnoreCase("Tolbas") || destination.equalsIgnoreCase("Tolbas Village")) {
			goTo(player, WorldMapType.VERTERON.getId(), 1291, 2206, 142);
		} // Eltnen
		else if (destination.equalsIgnoreCase("Eltnen")) {
			goTo(player, WorldMapType.ELTNEN.getId(), 343, 2724, 264);
		} else if (destination.equalsIgnoreCase("Golden") || destination.equalsIgnoreCase("Golden Bough Garrison")) {
			goTo(player, WorldMapType.ELTNEN.getId(), 688, 431, 332);
		} else if (destination.equalsIgnoreCase("Eltnen Observatory")) {
			goTo(player, WorldMapType.ELTNEN.getId(), 1779, 883, 422);
		} else if (destination.equalsIgnoreCase("Novan")) {
			goTo(player, WorldMapType.ELTNEN.getId(), 947, 2215, 252);
		} else if (destination.equalsIgnoreCase("Agairon")) {
			goTo(player, WorldMapType.ELTNEN.getId(), 1921, 2045, 361);
		} else if (destination.equalsIgnoreCase("Kuriullu")) {
			goTo(player, WorldMapType.ELTNEN.getId(), 2411, 2724, 361);
		} // Theobomos
		else if (destination.equalsIgnoreCase("Theobomos")) {
			goTo(player, WorldMapType.THEOBOMOS.getId(), 1398, 1557, 31);
		} else if (destination.equalsIgnoreCase("Jamanok") || destination.equalsIgnoreCase("Jamanok Inn")) {
			goTo(player, WorldMapType.THEOBOMOS.getId(), 458, 1257, 127);
		} else if (destination.equalsIgnoreCase("Meniherk")) {
			goTo(player, WorldMapType.THEOBOMOS.getId(), 1396, 1560, 31);
		} else if (destination.equalsIgnoreCase("obsvillage")) {
			goTo(player, WorldMapType.THEOBOMOS.getId(), 2234, 2284, 50);
		} else if (destination.equalsIgnoreCase("Josnack")) {
			goTo(player, WorldMapType.THEOBOMOS.getId(), 901, 2774, 62);
		} else if (destination.equalsIgnoreCase("Anangke")) {
			goTo(player, WorldMapType.THEOBOMOS.getId(), 2681, 847, 138);
		} // Heiron
		else if (destination.equalsIgnoreCase("Heiron")) {
			goTo(player, WorldMapType.HEIRON.getId(), 2540, 343, 411);
		} else if (destination.equalsIgnoreCase("Heiron Observatory")) {
			goTo(player, WorldMapType.HEIRON.getId(), 1423, 1334, 175);
		} else if (destination.equalsIgnoreCase("Senea")) {
			goTo(player, WorldMapType.HEIRON.getId(), 971, 686, 135);
		} else if (destination.equalsIgnoreCase("Jeiaparan")) {
			goTo(player, WorldMapType.HEIRON.getId(), 1635, 2693, 115);
		} else if (destination.equalsIgnoreCase("Changarnerk")) {
			goTo(player, WorldMapType.HEIRON.getId(), 916, 2256, 157);
		} else if (destination.equalsIgnoreCase("Kishar")) {
			goTo(player, WorldMapType.HEIRON.getId(), 1999, 1391, 118);
		} else if (destination.equalsIgnoreCase("Arbolu")) {
			goTo(player, WorldMapType.HEIRON.getId(), 170, 1662, 120);
		} else if (destination.equalsIgnoreCase("reaper")) {
			goTo(player, WorldMapType.HEIRON.getId(), 2767, 1867, 154);
		} /**
			 * Asmodae
			 */
		// Pandaemonium
		else if (destination.equalsIgnoreCase("Pandaemonium")) {
			goTo(player, WorldMapType.PANDAEMONIUM.getId(), 1679, 1400, 195);
		} // Marchutran
		else if (destination.equalsIgnoreCase("Marchutan")) {
			goTo(player, WorldMapType.MARCHUTAN.getId(), 1557, 1429, 266);
		} // Ishalgen
		else if (destination.equalsIgnoreCase("Ishalgen")) {
			goTo(player, WorldMapType.ISHALGEN.getId(), 529, 2449, 281);
		} else if (destination.equalsIgnoreCase("Anturon")) {
			goTo(player, WorldMapType.ISHALGEN.getId(), 940, 1707, 259);
		} // Altgard
		else if (destination.equalsIgnoreCase("Altgard")) {
			goTo(player, WorldMapType.ALTGARD.getId(), 1748, 1807, 254);
		} else if (destination.equalsIgnoreCase("Basfelt")) {
			goTo(player, WorldMapType.ALTGARD.getId(), 1903, 696, 260);
		} else if (destination.equalsIgnoreCase("Trader")) {
			goTo(player, WorldMapType.ALTGARD.getId(), 2680, 1024, 311);
		} else if (destination.equalsIgnoreCase("Impetusium")) {
			goTo(player, WorldMapType.ALTGARD.getId(), 2643, 1658, 324);
		} else if (destination.equalsIgnoreCase("Altgard Observatory")) {
			goTo(player, WorldMapType.ALTGARD.getId(), 1468, 2560, 299);
		} // Morheim
		else if (destination.equalsIgnoreCase("Morheim")) {
			goTo(player, WorldMapType.MORHEIM.getId(), 308, 2274, 449);
		} else if (destination.equalsIgnoreCase("Desert")) {
			goTo(player, WorldMapType.MORHEIM.getId(), 634, 900, 360);
		} else if (destination.equalsIgnoreCase("Slag")) {
			goTo(player, WorldMapType.MORHEIM.getId(), 1772, 1662, 197);
		} else if (destination.equalsIgnoreCase("Kellan")) {
			goTo(player, WorldMapType.MORHEIM.getId(), 1070, 2486, 239);
		} else if (destination.equalsIgnoreCase("Alsig")) {
			goTo(player, WorldMapType.MORHEIM.getId(), 2387, 1742, 102);
		} else if (destination.equalsIgnoreCase("Morheim Observatory")) {
			goTo(player, WorldMapType.MORHEIM.getId(), 2794, 1122, 171);
		} else if (destination.equalsIgnoreCase("Halabana")) {
			goTo(player, WorldMapType.MORHEIM.getId(), 2346, 2219, 127);
		} // Brusthonin
		else if (destination.equalsIgnoreCase("Brusthonin")) {
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 2917, 2421, 15);
		} else if (destination.equalsIgnoreCase("Baltasar")) {
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 1413, 2013, 51);
		} else if (destination.equalsIgnoreCase("Bollu")) {
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 840, 2016, 307);
		} else if (destination.equalsIgnoreCase("Edge")) {
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 1523, 374, 231);
		} else if (destination.equalsIgnoreCase("Bubu")) {
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 526, 848, 76);
		} else if (destination.equalsIgnoreCase("Settlers")) {
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 2917, 2417, 15);
		} // Beluslan
		else if (destination.equalsIgnoreCase("Beluslan")) {
			goTo(player, WorldMapType.BELUSLAN.getId(), 398, 400, 222);
		} else if (destination.equalsIgnoreCase("Besfer")) {
			goTo(player, WorldMapType.BELUSLAN.getId(), 533, 1866, 262);
		} else if (destination.equalsIgnoreCase("Kidorun")) {
			goTo(player, WorldMapType.BELUSLAN.getId(), 1243, 819, 260);
		} else if (destination.equalsIgnoreCase("Red Mane")) {
			goTo(player, WorldMapType.BELUSLAN.getId(), 2358, 1241, 470);
		} else if (destination.equalsIgnoreCase("Kistenian")) {
			goTo(player, WorldMapType.BELUSLAN.getId(), 1942, 513, 412);
		} else if (destination.equalsIgnoreCase("Hoarfrost")) {
			goTo(player, WorldMapType.BELUSLAN.getId(), 2431, 2063, 579);
		} /**
			 * Balaurea
			 */
		// Inggison
		else if (destination.equalsIgnoreCase("Inggison")) {
			goTo(player, WorldMapType.INGGISON.getId(), 1335, 276, 590);
		} else if (destination.equalsIgnoreCase("Ufob")) {
			goTo(player, WorldMapType.INGGISON.getId(), 382, 951, 460);
		} else if (destination.equalsIgnoreCase("Soteria")) {
			goTo(player, WorldMapType.INGGISON.getId(), 2713, 1477, 382);
		} else if (destination.equalsIgnoreCase("Hanarkand")) {
			goTo(player, WorldMapType.INGGISON.getId(), 1892, 1748, 327);
		} // Gelkmaros
		else if (destination.equalsIgnoreCase("Gelkmaros")) {
			goTo(player, WorldMapType.GELKMAROS.getId(), 1763, 2911, 554);
		} else if (destination.equalsIgnoreCase("Subterranea")) {
			goTo(player, WorldMapType.GELKMAROS.getId(), 2503, 2147, 464);
		} else if (destination.equalsIgnoreCase("Rhonnam")) {
			goTo(player, WorldMapType.GELKMAROS.getId(), 845, 1737, 354);
		} // Silentera
		else if (destination.equalsIgnoreCase("Silentera")) {
			goTo(player, 600010000, 583, 767, 300);
		} /**
			 * Abyss
			 */
		else if (destination.equalsIgnoreCase("Reshanta")) {
			goTo(player, WorldMapType.RESHANTA.getId(), 951, 936, 1667);
		} else if (destination.equalsIgnoreCase("Teminon")) {
			goTo(player, WorldMapType.RESHANTA.getId(), 2867, 1034, 1528);
		} else if (destination.equalsIgnoreCase("Primum")) {
			goTo(player, WorldMapType.RESHANTA.getId(), 1078, 2839, 1636);
		} else if (destination.equalsIgnoreCase("Tigraki")) {
			goTo(player, WorldMapType.RESHANTA.getId(), 539, 1100, 2843);
		} else if (destination.equalsIgnoreCase("Nuage noir") || destination.equalsIgnoreCase("Nuage")) {
			goTo(player, WorldMapType.RESHANTA.getId(), 3429, 2439, 2765);
		} else if (destination.equalsIgnoreCase("Leibos")) {
			goTo(player, WorldMapType.RESHANTA.getId(), 2136, 1943, 1597);
		} else if (destination.equalsIgnoreCase("ORSL") || destination.equalsIgnoreCase("Ori sup Latesran")) {
			goTo(player, WorldMapType.RESHANTA.getId(), 1596, 2952, 2943);
		} else if (destination.equalsIgnoreCase("OCSL") || destination.equalsIgnoreCase("Occi sup Latesran")) {
			goTo(player, WorldMapType.RESHANTA.getId(), 2054, 660, 2843);
		} else if (destination.equalsIgnoreCase("ORIL") || destination.equalsIgnoreCase("Ori inf Latesran")) {
			goTo(player, WorldMapType.RESHANTA.getId(), 1639, 2968, 1668);
		} else if (destination.equalsIgnoreCase("OCIL") || destination.equalsIgnoreCase("Occi inf Latesran")) {
			goTo(player, WorldMapType.RESHANTA.getId(), 2095, 679, 1567);
		} else if (destination.equalsIgnoreCase("Eye of Reshanta") || destination.equalsIgnoreCase("Eye")) {
			goTo(player, WorldMapType.RESHANTA.getId(), 1979, 2114, 2291);
		} else if (destination.equalsIgnoreCase("Divine Fortress") || destination.equalsIgnoreCase("Divine") || destination.equalsIgnoreCase("1011")) {
			goTo(player, WorldMapType.RESHANTA.getId(), 2130, 1925, 2322);
		} /**
			 * Fortos
			 */
		// Abyss
		else if (destination.equalsIgnoreCase("Soufre") || destination.equalsIgnoreCase("1141")) {
			goTo(player, WorldMapType.RESHANTA.getId(), 1379, 1187, 1537);
		} else if (destination.equalsIgnoreCase("Siel occi") || destination.equalsIgnoreCase("1131")) {
			goTo(player, WorldMapType.RESHANTA.getId(), 2792, 2609, 1504);
		} else if (destination.equalsIgnoreCase("Siel ori") || destination.equalsIgnoreCase("1132")) {
			goTo(player, WorldMapType.RESHANTA.getId(), 2608, 2853, 1530);
		} else if (destination.equalsIgnoreCase("Roah") || destination.equalsIgnoreCase("1211")) {
			goTo(player, WorldMapType.RESHANTA.getId(), 2735, 801, 2894);
		} else if (destination.equalsIgnoreCase("Asteria") || destination.equalsIgnoreCase("1251")) {
			goTo(player, WorldMapType.RESHANTA.getId(), 722, 2961, 2921);
		} else if (destination.equalsIgnoreCase("Krotan") || destination.equalsIgnoreCase("1221")) {
			goTo(player, WorldMapType.RESHANTA.getId(), 2057, 1275, 2987);
		} else if (destination.equalsIgnoreCase("Kysis") || destination.equalsIgnoreCase("1231")) {
			goTo(player, WorldMapType.RESHANTA.getId(), 2506, 2109, 3074);
		} else if (destination.equalsIgnoreCase("Miren") || destination.equalsIgnoreCase("1241")) {
			goTo(player, WorldMapType.RESHANTA.getId(), 1789, 2269, 2951);
		} // Balaurea
		else if (destination.equalsIgnoreCase("Avidite") || destination.equalsIgnoreCase("2011")) {
			goTo(player, WorldMapType.INGGISON.getId(), 887, 1979, 341);
		} else if (destination.equalsIgnoreCase("Dragon") || destination.equalsIgnoreCase("2021")) {
			goTo(player, WorldMapType.INGGISON.getId(), 1729, 2236, 329);
		} else if (destination.equalsIgnoreCase("Vorgaltem") || destination.equalsIgnoreCase("3011")) {
			goTo(player, WorldMapType.GELKMAROS.getId(), 1198, 806, 314);
		} else if (destination.equalsIgnoreCase("Pourpre") || destination.equalsIgnoreCase("3021")) {
			goTo(player, WorldMapType.GELKMAROS.getId(), 1882, 1042, 331);
		} /**
			 * Instances
			 */
		else if (destination.equalsIgnoreCase("Haramel")) {
			goTo(player, 300200000, 176, 21, 144);
		} else if (destination.equalsIgnoreCase("Nochsana") || destination.equalsIgnoreCase("NTC")) {
			goTo(player, 300030000, 513, 668, 331);
		} else if (destination.equalsIgnoreCase("Arcanis") || destination.equalsIgnoreCase("Sky Temple of Arcanis")) {
			goTo(player, 320050000, 177, 229, 536);
		} else if (destination.equalsIgnoreCase("Fire Temple") || destination.equalsIgnoreCase("FT")) {
			goTo(player, 320100000, 144, 312, 123);
		} else if (destination.equalsIgnoreCase("Kromede") || destination.equalsIgnoreCase("Kromede Trial")) {
			goTo(player, 300230000, 248, 244, 189);
		} // Steel Rake
		else if (destination.equalsIgnoreCase("Steel Rake") || destination.equalsIgnoreCase("SR")) {
			goTo(player, 300100000, 237, 506, 948);
		} else if (destination.equalsIgnoreCase("Steel Rake Lower") || destination.equalsIgnoreCase("SR Low")) {
			goTo(player, 300100000, 283, 453, 903);
		} else if (destination.equalsIgnoreCase("Steel Rake Middle") || destination.equalsIgnoreCase("SR Mid")) {
			goTo(player, 300100000, 283, 453, 953);
		} else if (destination.equalsIgnoreCase("Indratu") || destination.equalsIgnoreCase("Indratu Fortress")) {
			goTo(player, 310090000, 562, 335, 1015);
		} else if (destination.equalsIgnoreCase("Azoturan") || destination.equalsIgnoreCase("Azoturan Fortress")) {
			goTo(player, 310100000, 458, 428, 1039);
		} else if (destination.equalsIgnoreCase("Bio Lab") || destination.equalsIgnoreCase("Aetherogenetics Lab")) {
			goTo(player, 310050000, 225, 244, 133);
		} else if (destination.equalsIgnoreCase("Adma") || destination.equalsIgnoreCase("Adma Stronghold")) {
			goTo(player, 320130000, 450, 200, 168);
		} else if (destination.equalsIgnoreCase("Alquimia") || destination.equalsIgnoreCase("Alquimia Research Center")) {
			goTo(player, 320110000, 603, 527, 200);
		} else if (destination.equalsIgnoreCase("Draupnir") || destination.equalsIgnoreCase("Draupnir Cave")) {
			goTo(player, 320080000, 491, 373, 622);
		} else if (destination.equalsIgnoreCase("Theobomos Lab") || destination.equalsIgnoreCase("Theobomos Research Lab")) {
			goTo(player, 310110000, 477, 201, 170);
		} else if (destination.equalsIgnoreCase("Dark Poeta") || destination.equalsIgnoreCase("DP")) {
			goTo(player, 300040000, 1214, 412, 140);
		} // Lower Abyss
		else if (destination.equalsIgnoreCase("Sulfur") || destination.equalsIgnoreCase("Sulfur Tree Nest")) {
			goTo(player, 300060000, 462, 345, 163);
		} else if (destination.equalsIgnoreCase("Right Wing") || destination.equalsIgnoreCase("Right Wing Chamber")) {
			goTo(player, 300090000, 263, 386, 103);
		} else if (destination.equalsIgnoreCase("Left Wing") || destination.equalsIgnoreCase("Left Wing Chamber")) {
			goTo(player, 300080000, 672, 606, 321);
		} // Upper Abyss
		else if (destination.equalsIgnoreCase("Asteria Chamber")) {
			goTo(player, 300050000, 469, 568, 202);
		} else if (destination.equalsIgnoreCase("Miren Chamber")) {
			goTo(player, 300130000, 527, 120, 176);
		} else if (destination.equalsIgnoreCase("Kysis Chamber")) {
			goTo(player, 300120000, 528, 121, 176);
		} else if (destination.equalsIgnoreCase("Krotan Chamber")) {
			goTo(player, 300140000, 528, 109, 176);
		} else if (destination.equalsIgnoreCase("Roah Chamber")) {
			goTo(player, 300070000, 504, 396, 94);
		} else if (destination.equalsIgnoreCase("Miren Barrack")) {
			goTo(player, 301290000, 527, 120, 176);
		} else if (destination.equalsIgnoreCase("Kysis Barrack")) {
			goTo(player, 301280000, 528, 121, 176);
		} else if (destination.equalsIgnoreCase("Krotan Barrack")) {
			goTo(player, 301300000, 528, 109, 176);
		} else if (destination.equalsIgnoreCase("Miren Barrack Legion")) {
			goTo(player, 301250000, 527, 120, 176);
		} else if (destination.equalsIgnoreCase("Kysis Barrack Legion")) {
			goTo(player, 301240000, 528, 121, 176);
		} else if (destination.equalsIgnoreCase("Krotan Barrack Legion")) {
			goTo(player, 301260000, 528, 109, 176);
		} // Divine
		else if (destination.equalsIgnoreCase("Abyssal Splinter") || destination.equalsIgnoreCase("Core")) {
			goTo(player, 300220000, 704, 153, 453);
		} else if (destination.equalsIgnoreCase("Dredgion")) {
			goTo(player, 300110000, 414, 193, 431);
		} else if (destination.equalsIgnoreCase("Chantra") || destination.equalsIgnoreCase("Chantra Dredgion")) {
			goTo(player, 300210000, 414, 193, 431);
		} else if (destination.equalsIgnoreCase("Terath") || destination.equalsIgnoreCase("Terath Dredgion")) {
			goTo(player, 300440000, 414, 193, 431);
		} else if (destination.equalsIgnoreCase("Taloc") || destination.equalsIgnoreCase("Taloc's Hollow")) {
			goTo(player, 300190000, 200, 214, 1099);
		} // Udas
		else if (destination.equalsIgnoreCase("Udas") || destination.equalsIgnoreCase("Udas Temple")) {
			goTo(player, 300150000, 637, 657, 134);
		} else if (destination.equalsIgnoreCase("Udas Lower") || destination.equalsIgnoreCase("Udas Lower Temple")) {
			goTo(player, 300160000, 1146, 277, 116);
		} else if (destination.equalsIgnoreCase("Beshmundir") || destination.equalsIgnoreCase("BT") || destination.equalsIgnoreCase("Beshmundir Temple")) {
			goTo(player, 300170000, 1477, 237, 243);
		} // Padmaraska Cave
		else if (destination.equalsIgnoreCase("Padmaraska Cave")) {
			goTo(player, 320150000, 385, 506, 66);
		} /**
			 * Quest Instance Maps
			 */
		// TODO : Changer id maps
		else if (destination.equalsIgnoreCase("Karamatis 0")) {
			goTo(player, 310010000, 221, 250, 206);
		} else if (destination.equalsIgnoreCase("Karamatis 1")) {
			goTo(player, 310020000, 312, 274, 206);
		} else if (destination.equalsIgnoreCase("Karamatis 2")) {
			goTo(player, 310120000, 221, 250, 206);
		} else if (destination.equalsIgnoreCase("Aerdina")) {
			goTo(player, 310030000, 275, 168, 205);
		} else if (destination.equalsIgnoreCase("Geranaia")) {
			goTo(player, 310040000, 275, 168, 205);
		} // Stigma quest
		else if (destination.equalsIgnoreCase("Sliver") || destination.equalsIgnoreCase("Sliver of Darkness")) {
			goTo(player, 310070000, 247, 249, 1392);
		} else if (destination.equalsIgnoreCase("Space") || destination.equalsIgnoreCase("Space of Destiny")) {
			goTo(player, 320070000, 246, 246, 125);
		} else if (destination.equalsIgnoreCase("Ataxiar 1")) {
			goTo(player, 320010000, 221, 250, 206);
		} else if (destination.equalsIgnoreCase("Ataxiar 2")) {
			goTo(player, 320020000, 221, 250, 206);
		} else if (destination.equalsIgnoreCase("Bregirun")) {
			goTo(player, 320030000, 275, 168, 205);
		} else if (destination.equalsIgnoreCase("Nidalber")) {
			goTo(player, 320040000, 275, 168, 205);
		} /**
			 * Arenas
			 */
		else if (destination.equalsIgnoreCase("Sanctum Arena")) {
			goTo(player, 310080000, 275, 242, 159);
		} else if (destination.equalsIgnoreCase("Triniel Arena")) {
			goTo(player, 320090000, 275, 239, 159);
		} // Empyrean Crucible
		else if (destination.equalsIgnoreCase("Crucible 1-0")) {
			goTo(player, 300300000, 380, 350, 95);
		} else if (destination.equalsIgnoreCase("Crucible 1-1")) {
			goTo(player, 300300000, 346, 350, 96);
		} else if (destination.equalsIgnoreCase("Crucible 5-0")) {
			goTo(player, 300300000, 1265, 821, 359);
		} else if (destination.equalsIgnoreCase("Crucible 5-1")) {
			goTo(player, 300300000, 1256, 797, 359);
		} else if (destination.equalsIgnoreCase("Crucible 6-0")) {
			goTo(player, 300300000, 1596, 150, 129);
		} else if (destination.equalsIgnoreCase("Crucible 6-1")) {
			goTo(player, 300300000, 1628, 155, 126);
		} else if (destination.equalsIgnoreCase("Crucible 7-0")) {
			goTo(player, 300300000, 1813, 797, 470);
		} else if (destination.equalsIgnoreCase("Crucible 7-1")) {
			goTo(player, 300300000, 1785, 797, 470);
		} else if (destination.equalsIgnoreCase("Crucible 8-0")) {
			goTo(player, 300300000, 1776, 1728, 304);
		} else if (destination.equalsIgnoreCase("Crucible 8-1")) {
			goTo(player, 300300000, 1776, 1760, 304);
		} else if (destination.equalsIgnoreCase("Crucible 9-0")) {
			goTo(player, 300300000, 1357, 1748, 320);
		} else if (destination.equalsIgnoreCase("Crucible 9-1")) {
			goTo(player, 300300000, 1334, 1741, 316);
		} else if (destination.equalsIgnoreCase("Crucible 10-0")) {
			goTo(player, 300300000, 1750, 1255, 395);
		} else if (destination.equalsIgnoreCase("Crucible 10-1")) {
			goTo(player, 300300000, 1761, 1280, 395);
		} // Arena Of Chaos
		else if (destination.equalsIgnoreCase("Arena Of Chaos - 1")) {
			goTo(player, 300350000, 1332, 1078, 340);
		} else if (destination.equalsIgnoreCase("Arena Of Chaos - 2")) {
			goTo(player, 300350000, 599, 1854, 227);
		} else if (destination.equalsIgnoreCase("Arena Of Chaos - 3")) {
			goTo(player, 300350000, 663, 265, 512);
		} else if (destination.equalsIgnoreCase("Arena Of Chaos - 4")) {
			goTo(player, 300350000, 1840, 1730, 302);
		} else if (destination.equalsIgnoreCase("Arena Of Chaos - 5")) {
			goTo(player, 300350000, 1932, 1228, 270);
		} else if (destination.equalsIgnoreCase("Arena Of Chaos - 6")) {
			goTo(player, 300350000, 1949, 946, 224);
		} /**
			 * Miscellaneous
			 */
		// Prison
		else if (destination.equalsIgnoreCase("Prison LF") || destination.equalsIgnoreCase("Prison Elyos")) {
			goTo(player, 510010000, 256, 256, 49);
		} else if (destination.equalsIgnoreCase("Prison DF") || destination.equalsIgnoreCase("Prison Asmos")) {
			goTo(player, 520010000, 256, 256, 49);
		} // Test
		else if (destination.equalsIgnoreCase("Test Dungeon")) {
			goTo(player, 300020000, 104, 66, 25);
		} else if (destination.equalsIgnoreCase("Test Basic")) {
			goTo(player, 900020000, 144, 136, 20);
		} else if (destination.equalsIgnoreCase("Test Server")) {
			goTo(player, 900030000, 228, 171, 49);
		} else if (destination.equalsIgnoreCase("Test GiantMonster")) {
			goTo(player, 900100000, 196, 187, 20);
		} // Unknown
		else if (destination.equalsIgnoreCase("IDAbPro")) {
			goTo(player, 300010000, 270, 200, 206);
		} // GamezNetwork GM zone
		else if (destination.equalsIgnoreCase("gm")) {
			goTo(player, 120020000, 1457, 1194, 298);
		} /**
			 * 2.5 Maps
			 */
		else if (destination.equalsIgnoreCase("Kaisinel Academy")) {
			goTo(player, 110070000, 459, 251, 128);
		} else if (destination.equalsIgnoreCase("Marchutan Priory")) {
			goTo(player, 120080000, 577, 250, 94);
		} else if (destination.equalsIgnoreCase("Esoterrace")) {
			goTo(player, 300250000, 333, 437, 326);
		} /**
			 * 3.0 Maps
			 */
		else if (destination.equalsIgnoreCase("Pernon")) {
			goTo(player, 710010000, 1069, 1539, 98);
		} else if (destination.equalsIgnoreCase("Pernon Studio")) {
			goTo(player, 710010000, 1197, 2771, 236);
		} else if (destination.equalsIgnoreCase("Oriel")) {
			goTo(player, 700010000, 1261, 1845, 98);
		} else if (destination.equalsIgnoreCase("Oriel Studio")) {
			goTo(player, 700010000, 2569, 1960, 182);
		} else if (destination.equalsIgnoreCase("Griffoen")) {
			goTo(player, 600020000, 1134, 1312, 1360);
		} else if (destination.equalsIgnoreCase("Habrok")) {
			goTo(player, 600020000, 1602, 1611, 1361);
		} else if (destination.equalsIgnoreCase("Sarpan")) {
			goTo(player, 600020000, 1374, 1455, 600);
		} else if (destination.equalsIgnoreCase("Nalotias Cabin")) {
			goTo(player, 600020000, 176, 289, 714);
		} else if (destination.equalsIgnoreCase("Garldar Village")) {
			goTo(player, 600020000, 577, 1052, 656);
		} else if (destination.equalsIgnoreCase("Entrance elementalis")) {
			goTo(player, 600020000, 1414, 288, 626);
		} else if (destination.equalsIgnoreCase("Sgardien west")) {
			goTo(player, 600020000, 1931, 913, 567);
		} else if (destination.equalsIgnoreCase("Sgardien est")) {
			goTo(player, 600020000, 1422, 2447, 484);
		} else if (destination.equalsIgnoreCase("Expedition jotun")) {
			goTo(player, 600020000, 930, 2208, 533);
		} else if (destination.equalsIgnoreCase("Lande torturee")) {
			goTo(player, 600020000, 2150, 1643, 398);
		} else if (destination.equalsIgnoreCase("Sifflelame")) {
			goTo(player, 600020000, 2803, 2043, 550);
		} else if (destination.equalsIgnoreCase("Fatalite")) {
			goTo(player, 600020000, 2877, 2286, 567);
		} else if (destination.equalsIgnoreCase("Protectrice")) {
			goTo(player, 300330000, 250, 246, 124);
		} else if (destination.equalsIgnoreCase("Tiamaranta")) {
			goTo(player, 600030000, 71, 1732, 295);
		} else if (destination.equalsIgnoreCase("Arbre gurrik")) {
			goTo(player, 600030000, 742, 2362, 108);
		} else if (destination.equalsIgnoreCase("Conescorie")) {
			goTo(player, 600030000, 1366, 2659, 256);
		} else if (destination.equalsIgnoreCase("Notus")) {
			goTo(player, 600030000, 1715, 484, 256);
		} else if (destination.equalsIgnoreCase("Cnuage")) {
			goTo(player, 600030000, 2779, 1020, 159);
		} else if (destination.equalsIgnoreCase("Petro")) {
			goTo(player, 600030000, 2524, 1511, 232);
		} else if (destination.equalsIgnoreCase("E satra")) {
			goTo(player, 600030000, 2165, 1538, 233);
		} else if (destination.equalsIgnoreCase("E basrasa")) {
			goTo(player, 600030000, 1527, 2186, 232);
		} else if (destination.equalsIgnoreCase("E naduka")) {
			goTo(player, 600030000, 1523, 884, 233);
		} else if (destination.equalsIgnoreCase("Ee tiamaranta")) {
			goTo(player, 600030000, 1526, 1794, 249);
		} else if (destination.equalsIgnoreCase("Ew tiamaranta")) {
			goTo(player, 600030000, 1524, 1241, 245);
		} else if (destination.equalsIgnoreCase("Tiamaranta Eye")) {
			goTo(player, 600040000, 159, 768, 1202);
		} else if (destination.equalsIgnoreCase("Steel Rake Cabin") || destination.equalsIgnoreCase("Steel Rake Solo")) {
			goTo(player, 300460000, 248, 244, 189);
		} else if (destination.equalsIgnoreCase("Aturam") || destination.equalsIgnoreCase("Aturam Sky Fortress")) {
			goTo(player, 300240000, 636, 446, 655);
		} else if (destination.equalsIgnoreCase("Elementis") || destination.equalsIgnoreCase("Elementis Forest")) {
			goTo(player, 300260000, 176, 612, 231);
		} else if (destination.equalsIgnoreCase("Argent") || destination.equalsIgnoreCase("Argent Manor")) {
			goTo(player, 300270000, 1005, 1089, 70);
		} else if (destination.equalsIgnoreCase("Rentus") || destination.equalsIgnoreCase("Rentus Base")) {
			goTo(player, 300280000, 579, 606, 153);
		} else if (destination.equalsIgnoreCase("Raksang")) {
			goTo(player, 300310000, 665, 735, 1188);
		} else if (destination.equalsIgnoreCase("Muada") || destination.equalsIgnoreCase("Muada's Trencher")) {
			goTo(player, 300380000, 492, 553, 106);
		} else if (destination.equalsIgnoreCase("Satra")) {
			goTo(player, 300470000, 510, 180, 159);
		} else if (destination.equalsIgnoreCase("tract") || destination.equalsIgnoreCase("Israphel Tract")) {
			goTo(player, 300390000, 510, 180, 159);
		} else if (destination.equalsIgnoreCase("griffoen") || destination.equalsIgnoreCase("Griffoen")) {
			goTo(player, 300410000, 492, 553, 106);
		} /**
			 * 3.0 Fortress
			 */
		else if (destination.equalsIgnoreCase("fissure") || destination.equalsIgnoreCase("4011")) {
			goTo(player, 600030000, 267, 304, 318);
		} else if (destination.equalsIgnoreCase("gravity") || destination.equalsIgnoreCase("4021")) {
			goTo(player, 600030000, 2865, 262, 304);
		} else if (destination.equalsIgnoreCase("wrath") || destination.equalsIgnoreCase("4031")) {
			goTo(player, 600030000, 500, 2920, 324);
		} else if (destination.equalsIgnoreCase("petrification") || destination.equalsIgnoreCase("4041")) {
			goTo(player, 600030000, 2806, 2719, 359);
		} /**
			 * 3.5 Instance
			 */
		else if (destination.equalsIgnoreCase("Antre tiamat") || destination.equalsIgnoreCase("tiamat2")) {
			goTo(player, 300520000, 505, 520, 240);
		} else if (destination.equalsIgnoreCase("Forto tiamat") || destination.equalsIgnoreCase("tiamat1")) {
			goTo(player, 300510000, 1581, 1068, 492);
		} else if (destination.equalsIgnoreCase("Unstable Abyssal Splinter") || destination.equalsIgnoreCase("Core2")) {
			goTo(player, 300600000, 704, 153, 453);
		} else if (destination.equalsIgnoreCase("harmonyTraining") || destination.equalsIgnoreCase("Harmony Training Grounds")) {
			goTo(player, 300570000, 500, 371, 211);
		} else if (destination.equalsIgnoreCase("glory") || destination.equalsIgnoreCase("Arena Of Glory")) {
			goTo(player, 300550000, 500, 371, 211);
		} /**
			 * 3.7 Instances
			 */
		else if (destination.equalsIgnoreCase("hexway")) {
			goTo(player, 300700000, 682, 607, 320);
		} else if (destination.equalsIgnoreCase("shugotomb")) {
			goTo(player, 300560000, 178, 234, 543);
		} else if (destination.equalsIgnoreCase("unity") || destination.equalsIgnoreCase("Unity Training Grounds")) {
			goTo(player, 301100000, 500, 371, 211);
		} /**
			 * 4.0 fortress
			 */
		else if (destination.equalsIgnoreCase("Silus") || destination.equalsIgnoreCase("5011")) {
			goTo(player, 600050000, 2019, 1752, 308);
		} else if (destination.equalsIgnoreCase("Bassen") || destination.equalsIgnoreCase("6011")) {
			goTo(player, 600060000, 1472, 740, 67);
		} else if (destination.equalsIgnoreCase("Pradeth") || destination.equalsIgnoreCase("6021")) {
			goTo(player, 600060000, 2586, 2634, 277);
		} /**
			 * 4.0 Maps
			 */
		else if (destination.equalsIgnoreCase("katalamely")) {
			goTo(player, 600050000, 398, 2718, 142);
		} else if (destination.equalsIgnoreCase("katalamasmo")) {
			goTo(player, 600050000, 361, 383, 281);
		} else if (destination.equalsIgnoreCase("krerunerk")) {
			goTo(player, 600050000, 2787, 2577, 259);
		} else if (destination.equalsIgnoreCase("mairinerk")) {
			goTo(player, 600050000, 2870, 291, 296);
		} else if (destination.equalsIgnoreCase("forest shadow")) {
			goTo(player, 600050000, 1696, 83, 112);
		} else if (destination.equalsIgnoreCase("portis")) {
			goTo(player, 600050000, 1943, 2937, 293);
		} else if (destination.equalsIgnoreCase("porte nord eremion")) {
			goTo(player, 600050000, 2914, 1873, 396);
		} else if (destination.equalsIgnoreCase("danaria")) {
			goTo(player, 600060000, 2545, 1699, 141);
		} else if (destination.equalsIgnoreCase("danaria nord ely")) {
			goTo(player, 600060000, 63, 1927, 519);
		} else if (destination.equalsIgnoreCase("pepe")) {
			goTo(player, 600060000, 1018, 2798, 300);
		} else if (destination.equalsIgnoreCase("kaberinrinerk")) {
			goTo(player, 600060000, 91, 2883, 469);
		} else if (destination.equalsIgnoreCase("danaria nord asmo")) {
			goTo(player, 600060000, 58, 1587, 520);
		} else if (destination.equalsIgnoreCase("phon")) {
			goTo(player, 600060000, 816, 275, 465);
		} else if (destination.equalsIgnoreCase("wallinerk")) {
			goTo(player, 600060000, 146, 345, 625);
		} else if (destination.equalsIgnoreCase("sauro")) {
			goTo(player, 600060000, 2731, 377, 245);
		} else if (destination.equalsIgnoreCase("idian")) {
			goTo(player, 600070000, 701, 693, 514);
		} else if (destination.equalsIgnoreCase("iu")) {
			goTo(player, 600080000, 1510, 1511, 565);
		} /**
			 * 4.3 Camps
			 */
		else if (destination.equalsIgnoreCase("71")) {
			goTo(player, 600050000, 237, 840, 210);
		} else if (destination.equalsIgnoreCase("72")) {
			goTo(player, 600050000, 138, 2191, 184);
		} else if (destination.equalsIgnoreCase("73")) {
			goTo(player, 600050000, 1102, 496, 184);
		} else if (destination.equalsIgnoreCase("74")) {
			goTo(player, 600050000, 861, 2752, 182);
		} else if (destination.equalsIgnoreCase("75")) {
			goTo(player, 600050000, 1574, 1485, 129);
		} else if (destination.equalsIgnoreCase("76")) {
			goTo(player, 600050000, 1680, 1098, 181);
		} else if (destination.equalsIgnoreCase("77")) {
			goTo(player, 600050000, 1871, 2264, 174);
		} else if (destination.equalsIgnoreCase("78")) {
			goTo(player, 600050000, 1898, 1189, 259);
		} else if (destination.equalsIgnoreCase("79")) {
			goTo(player, 600050000, 2490, 1838, 325);
		} else if (destination.equalsIgnoreCase("80")) {
			goTo(player, 600060000, 967, 1169, 373);
		} else if (destination.equalsIgnoreCase("81")) {
			goTo(player, 600060000, 1040, 1811, 362);
		} else if (destination.equalsIgnoreCase("82")) {
			goTo(player, 600060000, 1044, 2249, 276);
		} else if (destination.equalsIgnoreCase("83")) {
			goTo(player, 600060000, 2802, 594, 274);
		} else if (destination.equalsIgnoreCase("84")) {
			goTo(player, 600060000, 2660, 489, 234);
		} else if (destination.equalsIgnoreCase("85")) {
			goTo(player, 600060000, 1816, 803, 153);
		} else if (destination.equalsIgnoreCase("86")) {
			goTo(player, 600060000, 1520, 1128, 92);
		} else if (destination.equalsIgnoreCase("87")) {
			goTo(player, 600060000, 1339, 2326, 183);
		} else if (destination.equalsIgnoreCase("88")) {
			goTo(player, 600060000, 2132, 2472, 268);
		} else if (destination.equalsIgnoreCase("89")) {
			goTo(player, 600060000, 2606, 2210, 249);
		} /**
			 * 4.3 Instances
			 */
		else if (destination.equalsIgnoreCase("mystic") || destination.equalsIgnoreCase("Danuar Mysticarium")) {
			goTo(player, 300480000, 179, 122, 231);
		} else if (destination.equalsIgnoreCase("idgel") || destination.equalsIgnoreCase("Idgel Research Center")) {
			goTo(player, 300530000, 571, 472, 102);
		} else if (destination.equalsIgnoreCase("eternal") || destination.equalsIgnoreCase("Eternal Bastion")) {
			goTo(player, 300540000, 763, 268, 233);
		} else if (destination.equalsIgnoreCase("cube") || destination.equalsIgnoreCase("Void Cube")) {
			goTo(player, 300580000, 181, 261, 310);
		} else if (destination.equalsIgnoreCase("ophidan") || destination.equalsIgnoreCase("Ophidan Bridge")) {
			goTo(player, 300590000, 760, 561, 572);
		} else if (destination.equalsIgnoreCase("infinity") || destination.equalsIgnoreCase("Infinity Shard")) {
			goTo(player, 300800000, 118, 115, 131);
		} else if (destination.equalsIgnoreCase("runadium") || destination.equalsIgnoreCase("Runadium")) {
			goTo(player, 300900000, 256, 257, 241);
		} else if (destination.equalsIgnoreCase("solo") || destination.equalsIgnoreCase("Solo Q")) {
			goTo(player, 301000000, 500, 500, 500);
		} else if (destination.equalsIgnoreCase("steelsolo1") || destination.equalsIgnoreCase("Steel Rose Solo 1st Deck")) {
			goTo(player, 301010000, 283, 452, 902);
		} else if (destination.equalsIgnoreCase("steelsolo2") || destination.equalsIgnoreCase("Steel Rose Solo 2nd Deck")) {
			goTo(player, 301020000, 236, 506, 948);
		} else if (destination.equalsIgnoreCase("steel1") || destination.equalsIgnoreCase("Steel Rose 1st Deck")) {
			goTo(player, 301030000, 283, 452, 902);
		} else if (destination.equalsIgnoreCase("steel2") || destination.equalsIgnoreCase("Steel Rose 2nd Deck")) {
			goTo(player, 301040000, 236, 506, 948);
		} else if (destination.equalsIgnoreCase("steel3") || destination.equalsIgnoreCase("Steel Rose 3rd Deck")) {
			goTo(player, 301050000, 713, 462, 1015);
		} else if (destination.equalsIgnoreCase("reliquary") || destination.equalsIgnoreCase("Danuar Reliquary")) {
			goTo(player, 301110000, 256, 257, 241);
		} else if (destination.equalsIgnoreCase("kamar") || destination.equalsIgnoreCase("Kamar Battlefield")) {
			goTo(player, 301120000, 1374, 1455, 600);
		} else if (destination.equalsIgnoreCase("sauro") || destination.equalsIgnoreCase("Sauro Supply Base")) {
			goTo(player, 301130000, 641, 176, 195);
		} else if (destination.equalsIgnoreCase("danuar") || destination.equalsIgnoreCase("Danuar Sanctuary")) {
			goTo(player, 301140000, 388, 1184, 55);
		} else if (destination.equalsIgnoreCase("Rumakiki") || destination.equalsIgnoreCase("Rumakiki Conspiracy")) {
			goTo(player, 301150000, 500, 500, 500);
		} else if (destination.equalsIgnoreCase("circus1") || destination.equalsIgnoreCase("Nightmare Circus")) {
			goTo(player, 301160000, 500, 500, 500);
		} else if (destination.equalsIgnoreCase("idgel2") || destination.equalsIgnoreCase("Idgel Research Center (Legion)")) {
			goTo(player, 301170000, 571, 472, 102);
		} else if (destination.equalsIgnoreCase("cube2") || destination.equalsIgnoreCase("Void Cube (Legion)")) {
			goTo(player, 301180000, 181, 261, 310);
		} else if (destination.equalsIgnoreCase("mystic2") || destination.equalsIgnoreCase("Danuar Mysticarium (Legion)")) {
			goTo(player, 301190000, 179, 122, 231);
		} else if (destination.equalsIgnoreCase("circus2") || destination.equalsIgnoreCase("The Nightmare Circus")) {
			goTo(player, 301200000, 500, 500, 500);
		} /**
			 * 4.5 Instances
			 */
		else if (destination.equalsIgnoreCase("ophidanwar") || destination.equalsIgnoreCase("Engulfed Ophidan Bridge")) {
			goTo(player, 301210000, 773, 553, 576);
		} else if (destination.equalsIgnoreCase("ironwall") || destination.equalsIgnoreCase("Iron Wall Warfront")) {
			goTo(player, 301220000, 449, 449, 270);
		} else if (destination.equalsIgnoreCase("Illuminary") || destination.equalsIgnoreCase("Illuminary obelisk")) {
			goTo(player, 301230000, 321, 323, 405);
		} /**
			 * 4.7
			 */
		else if (destination.equalsIgnoreCase("kaldor1") || destination.equalsIgnoreCase("Elyos.Kaldor")) { // ely
			goTo(player, 600090000, 1268, 1333, 194);
		} else if (destination.equalsIgnoreCase("kaldor2") || destination.equalsIgnoreCase("Asmod.Kaldor")) { // asmo
			goTo(player, 600090000, 397, 1380, 163);
		} else if (destination.equalsIgnoreCase("levinshor1") || destination.equalsIgnoreCase("Elyos.Levinshor")) { // ely
			goTo(player, 600100000, 207, 183, 374);
		} else if (destination.equalsIgnoreCase("levinshor2") || destination.equalsIgnoreCase("Asmod.Levinshor")) { // asmo
			goTo(player, 600100000, 1842, 1782, 305);
		} else if (destination.equalsIgnoreCase("pangea") || destination.equalsIgnoreCase("Belus")) { // Belus
			goTo(player, 400020000, 1238, 1232, 1518);
		} else if (destination.equalsIgnoreCase("pangeasub") || destination.equalsIgnoreCase("TransidiumAnnex")) { // Transidium
																													 // Annex
			goTo(player, 400030000, 509, 513, 675);
		} else if (destination.equalsIgnoreCase("pangea2") || destination.equalsIgnoreCase("Aspida")) { // Aspida
			goTo(player, 400040000, 1238, 1232, 1518);
		} else if (destination.equalsIgnoreCase("pangea3") || destination.equalsIgnoreCase("Atanatos")) { // Atanatos
			goTo(player, 400050000, 1238, 1232, 1518);
		} else if (destination.equalsIgnoreCase("pangea4") || destination.equalsIgnoreCase("Disillon")) { // Disillon
			goTo(player, 400060000, 1238, 1232, 1518);
		} else if (destination.equalsIgnoreCase("wisplight") || destination.equalsIgnoreCase("Elyos.WisplightAbbey")) { // Wisplight
																														 // Abbey
																														 // (Elyos)
																														 // -
																														 // Not
																														 // logged
																														 // in
																														 // for
																														 // 30-days
			goTo(player, 130090000, 247, 236, 129);
		} else if (destination.equalsIgnoreCase("fatebound") || destination.equalsIgnoreCase("Asmod.FateboundAbbey")) { // Fatebound
																														 // Abbey
																														 // (Asmodian)
																														 // -
																														 // Not
																														 // logged
																														 // in
																														 // for
																														 // 30-days
			goTo(player, 140010000, 272, 266, 96);
		} /**
			 * 4.7 Instances
			 */
		else if (destination.equalsIgnoreCase("baruna") || destination.equalsIgnoreCase("LinkgateFoundry")) { // Linkgate
																												 // Foundry
			goTo(player, 301270000, 289, 216, 311);
		} else if (destination.equalsIgnoreCase("runatorium") || destination.equalsIgnoreCase("IdgelDome")) { // Idgel
																												 // Dome
			if (player.getRace() == Race.ELYOS) {
				goTo(player, 301310000, 270.1437f, 348.6699f, 79.44365f, (byte) 105);
			} else if (player.getRace() == Race.ASMODIANS) {
				goTo(player, 301310000, 258.5553f, 169.85149f, 79.430855f, (byte) 45);
			}
		} /**
			 * 4.8
			 */
		else if (destination.equalsIgnoreCase("idsweep")) {
			goTo(player, 301400000, 464, 641, 395);
		} /**
			 * Not found
			 */
		else {
			PacketSendUtility.sendMessage(player, "Could not find the specified destination !");
		}
	}
	
	private static void goTo(final Player player, int worldId, float x, float y, float z) {
		WorldMap destinationMap = World.getInstance().getWorldMap(worldId);
		if (destinationMap.isInstanceType()) {
			TeleportService2.teleportTo(player, worldId, getInstanceId(worldId, player), x, y, z);
		} else {
			TeleportService2.teleportTo(player, worldId, x, y, z);
		}
	}
	
	private static void goTo(final Player player, int worldId, float x, float y, float z, byte h) {
		WorldMap destinationMap = World.getInstance().getWorldMap(worldId);
		if (destinationMap.isInstanceType()) {
			TeleportService2.teleportTo(player, worldId, getInstanceId(worldId, player), x, y, z, h);
		} else {
			TeleportService2.teleportTo(player, worldId, x, y, z, h);
		}
	}
	
	private static int getInstanceId(int worldId, Player player) {
		if (player.getWorldId() == worldId) {
			WorldMapInstance registeredInstance = InstanceService.getRegisteredInstance(worldId, player.getObjectId());
			if (registeredInstance != null) {
				return registeredInstance.getInstanceId();
			}
		}
		WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(worldId);
		InstanceService.registerPlayerWithInstance(newInstance, player);
		return newInstance.getInstanceId();
	}
	
	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax : //goto <location>");
	}
}

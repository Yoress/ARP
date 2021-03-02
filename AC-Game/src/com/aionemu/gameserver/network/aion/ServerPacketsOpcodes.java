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
package com.aionemu.gameserver.network.aion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.aionemu.gameserver.network.aion.serverpackets.*;

/**
 * This class is holding opcodes for all server packets. It's used only to have all opcodes in one place
 *
 * @author Luno, alexa026, ATracer, avol, orz, cura
 * @author GiGatR00n v4.7.5.x
 * @modified Yon (Aion Reconstruction Project) -- Corrected Opcode for SM_PLAYER_STANCE, added {@link SM_QUESTION_WINDOW_ACCEPT_SUMMON}.
 */
public class ServerPacketsOpcodes {
	
	private static Map<Class<? extends AionServerPacket>, Integer> opcodes = new HashMap<Class<? extends AionServerPacket>, Integer>();
	
	static {
		Set<Integer> idSet = new HashSet<Integer>();
		
		addPacketOpcode(SM_VERSION_CHECK.class, 0x00, idSet);
		addPacketOpcode(SM_STATS_INFO.class, 0x01, idSet);
		addPacketOpcode(SM_STATUPDATE_HP.class, 0x03, idSet);
		addPacketOpcode(SM_STATUPDATE_MP.class, 0x04, idSet);
		addPacketOpcode(SM_ATTACK_STATUS.class, 0x05, idSet);
		addPacketOpcode(SM_STATUPDATE_DP.class, 0x06, idSet);
		addPacketOpcode(SM_DP_INFO.class, 0x07, idSet);
		addPacketOpcode(SM_STATUPDATE_EXP.class, 0x08, idSet);
		addPacketOpcode(SM_NPC_ASSEMBLER.class, 0x09, idSet);
		//addPacketOpcode(SM_KILL_CLIENT.class, 0x0A, idSet); //TODO: This is valid, but the class doesn't exist; sending this packet makes the client close immediately.
		addPacketOpcode(SM_LEGION_UPDATE_NICKNAME.class, 0x0B, idSet);
		addPacketOpcode(SM_LEGION_TABS.class, 0x0C, idSet);
		addPacketOpcode(SM_ENTER_WORLD_CHECK.class, 0x0D, idSet);
		addPacketOpcode(SM_NPC_INFO.class, 0x0E, idSet);
		addPacketOpcode(SM_PLAYER_SPAWN.class, 0x0F, idSet);
		addPacketOpcode(SM_FORTRESS_INFO.class, 0xF3, idSet); //TODO: This is out of order! Verify opcode
		addPacketOpcode(SM_GATHERABLE_INFO.class, 0x11, idSet);
		addPacketOpcode(SM_TELEPORT_LOC.class, 0x14, idSet);
		addPacketOpcode(SM_PLAYER_MOVE.class, 0x15, idSet);
		addPacketOpcode(SM_DELETE.class, 0x16, idSet);
		addPacketOpcode(SM_LOGIN_QUEUE.class, 0x17, idSet);
		addPacketOpcode(SM_MESSAGE.class, 0x18, idSet);
		addPacketOpcode(SM_SYSTEM_MESSAGE.class, 0x19, idSet);
		addPacketOpcode(SM_INVENTORY_INFO.class, 0x1A, idSet);
		addPacketOpcode(SM_INVENTORY_ADD_ITEM.class, 0x1B, idSet);
		addPacketOpcode(SM_DELETE_ITEM.class, 0x1C, idSet);
		addPacketOpcode(SM_INVENTORY_UPDATE_ITEM.class, 0x1D, idSet);
		addPacketOpcode(SM_UI_SETTINGS.class, 0x1E, idSet);
		addPacketOpcode(SM_PLAYER_STANCE.class, 0x1F, idSet);
		addPacketOpcode(SM_PLAYER_INFO.class, 0x20, idSet);
		addPacketOpcode(SM_CASTSPELL.class, 0x21, idSet);
		addPacketOpcode(SM_GATHER_STATUS.class, 0x22, idSet);
		addPacketOpcode(SM_GATHER_UPDATE.class, 0x23, idSet);
		addPacketOpcode(SM_UPDATE_PLAYER_APPEARANCE.class, 0x24, idSet);
		addPacketOpcode(SM_EMOTION.class, 0x25, idSet);
		addPacketOpcode(SM_GAME_TIME.class, 0x26, idSet);
		addPacketOpcode(SM_TIME_CHECK.class, 0x27, idSet);
		addPacketOpcode(SM_LOOKATOBJECT.class, 0x28, idSet);
		addPacketOpcode(SM_TARGET_SELECTED.class, 0x29, idSet);
		addPacketOpcode(SM_SKILL_CANCEL.class, 0x2A, idSet);
		addPacketOpcode(SM_CASTSPELL_RESULT.class, 0x2B, idSet);
		addPacketOpcode(SM_SKILL_LIST.class, 0x2C, idSet);
		addPacketOpcode(SM_SKILL_REMOVE.class, 0x2D, idSet);
		addPacketOpcode(SM_SKILL_ACTIVATION.class, 0x2E, idSet);
		addPacketOpcode(SM_ABNORMAL_STATE.class, 0x31, idSet);
		addPacketOpcode(SM_ABNORMAL_EFFECT.class, 0x32, idSet);
		addPacketOpcode(SM_SKILL_COOLDOWN.class, 0x33, idSet);
		addPacketOpcode(SM_QUESTION_WINDOW.class, 0x34, idSet);
		addPacketOpcode(SM_ATTACK.class, 0x36, idSet);
		addPacketOpcode(SM_MOVE.class, 0x37, idSet);
		addPacketOpcode(SM_HEADING_UPDATE.class, 0x39, idSet);
		addPacketOpcode(SM_TRANSFORM.class, 0x3A, idSet);
		addPacketOpcode(SM_DIALOG_WINDOW.class, 0x3C, idSet);
		addPacketOpcode(SM_SELL_ITEM.class, 0x3E, idSet);
		addPacketOpcode(SM_VIEW_PLAYER_DETAILS.class, 0x41, idSet);
		addPacketOpcode(SM_WEATHER.class, 0x43, idSet);
		addPacketOpcode(SM_PLAYER_STATE.class, 0x44, idSet);
		addPacketOpcode(SM_QUESTION_WINDOW_ACCEPT_SUMMON.class, 0x45, idSet);
		addPacketOpcode(SM_LEVEL_UPDATE.class, 0x46, idSet);
		addPacketOpcode(SM_QUEST_LIST.class, 0x47, idSet);
		addPacketOpcode(SM_KEY.class, 0x48, idSet);
		addPacketOpcode(SM_SUMMON_PANEL_REMOVE.class, 0x49, idSet);
		addPacketOpcode(SM_EXCHANGE_REQUEST.class, 0x4A, idSet);
		addPacketOpcode(SM_EXCHANGE_ADD_ITEM.class, 0x4B, idSet);
		addPacketOpcode(SM_EXCHANGE_ADD_KINAH.class, 0x4D, idSet);
		addPacketOpcode(SM_EXCHANGE_CONFIRMATION.class, 0x4E, idSet);
		addPacketOpcode(SM_EMOTION_LIST.class, 0x4F, idSet);
		addPacketOpcode(SM_TARGET_UPDATE.class, 0x51, idSet);
		addPacketOpcode(SM_HOUSE_EDIT.class, 0x52, idSet);
		addPacketOpcode(SM_PLASTIC_SURGERY.class, 0x53, idSet);
		addPacketOpcode(SM_SERIAL_KILLER.class, 0x54, idSet);
		addPacketOpcode(SM_INFLUENCE_RATIO.class, 0x55, idSet);
		addPacketOpcode(SM_FORTRESS_STATUS.class, 0x56, idSet);
		addPacketOpcode(SM_CAPTCHA.class, 0x57, idSet);
		addPacketOpcode(SM_RENAME.class, 0x58, idSet);
		addPacketOpcode(SM_SHOW_NPC_ON_MAP.class, 0x59, idSet);
		addPacketOpcode(SM_GROUP_INFO.class, 0x5A, idSet);
		addPacketOpcode(SM_GROUP_MEMBER_INFO.class, 0x5B, idSet);
		addPacketOpcode(SM_QUIT_RESPONSE.class, 0x62, idSet);
		addPacketOpcode(SM_CHAT_WINDOW.class, 0x63, idSet);
		addPacketOpcode(SM_PET.class, 0x65, idSet);
		addPacketOpcode(SM_ITEM_COOLDOWN.class, 0x67, idSet);
		addPacketOpcode(SM_UPDATE_NOTE.class, 0x68, idSet);
		addPacketOpcode(SM_PLAY_MOVIE.class, 0x69, idSet);
		addPacketOpcode(SM_LEGION_INFO.class, 0x6E, idSet);
		addPacketOpcode(SM_LEGION_ADD_MEMBER.class, 0x6F, idSet);
		addPacketOpcode(SM_LEGION_LEAVE_MEMBER.class, 0x70, idSet);
		addPacketOpcode(SM_LEGION_UPDATE_MEMBER.class, 0x71, idSet);
		addPacketOpcode(SM_LEGION_UPDATE_TITLE.class, 0x72, idSet);
		addPacketOpcode(SM_HOUSE_REGISTRY.class, 0x74, idSet);
		addPacketOpcode(SM_LEGION_UPDATE_SELF_INTRO.class, 0x77, idSet);
		addPacketOpcode(SM_INSTANCE_SCORE.class, 0x79, idSet);
		// addPacketOpcode(SM_RIFT_STATUS.class, 0x8C, idSet); //TO-DO: This is out of order! Verify opcode
		addPacketOpcode(SM_AUTO_GROUP.class, 0x7A, idSet);
		addPacketOpcode(SM_QUEST_COMPLETED_LIST.class, 0x7B, idSet);
		addPacketOpcode(SM_QUEST_ACTION.class, 0x7C, idSet);
		addPacketOpcode(SM_NEARBY_QUESTS.class, 0x7F, idSet);
		addPacketOpcode(SM_PING_RESPONSE.class, 0x80, idSet);
		addPacketOpcode(SM_CUBE_UPDATE.class, 0x82, idSet);
		addPacketOpcode(SM_HOUSE_SCRIPTS.class, 0x83, idSet);
		addPacketOpcode(SM_FRIEND_LIST.class, 0x84, idSet);
		addPacketOpcode(SM_PRIVATE_STORE.class, 0x86, idSet);
		addPacketOpcode(SM_GROUP_LOOT.class, 0x87, idSet);
		addPacketOpcode(SM_ABYSS_RANK_UPDATE.class, 0x88, idSet);
		addPacketOpcode(SM_MAY_LOGIN_INTO_GAME.class, 0x89, idSet);
		addPacketOpcode(SM_ABYSS_RANKING_PLAYERS.class, 0x8A, idSet);
		addPacketOpcode(SM_ABYSS_RANKING_LEGIONS.class, 0x8B, idSet);
		addPacketOpcode(SM_INSTANCE_INFO.class, 0x8D, idSet);
		addPacketOpcode(SM_PONG.class, 0x8E, idSet);
		addPacketOpcode(SM_KISK_UPDATE.class, 0x90, idSet);
		addPacketOpcode(SM_PRIVATE_STORE_NAME.class, 0x91, idSet);
		addPacketOpcode(SM_BROKER_SERVICE.class, 0x92, idSet);
		addPacketOpcode(SM_MOTION.class, 0x94, idSet);
		addPacketOpcode(SM_TRADE_IN_LIST.class, 0x97, idSet);
		// addPacketOpcode(SM_BROKER_REGISTRATION_SERVICE.class, 0x93, idSet); //TO-DO: This is out of order! Verify opcode
		// addPacketOpcode(SM_BROKER_SETTLED_LIST.class, 0x95, idSet); //TO-DO: This is out of order! Verify opcode
		addPacketOpcode(SM_SUMMON_OWNER_REMOVE.class, 0x9A, idSet); //TODO: This is out of order! Verify opcode
		addPacketOpcode(SM_SUMMON_PANEL.class, 0x99, idSet); //TODO: This is out of order! Verify opcode
		addPacketOpcode(SM_SUMMON_UPDATE.class, 0x9B, idSet);
		addPacketOpcode(SM_TRANSFORM_IN_SUMMON.class, 0x9C, idSet);
		addPacketOpcode(SM_LEGION_MEMBERLIST.class, 0x9D, idSet);
		addPacketOpcode(SM_LEGION_EDIT.class, 0x9E, idSet);
		addPacketOpcode(SM_TOLL_INFO.class, 0x9F, idSet); // ingameshop
		addPacketOpcode(SM_DRAWING_TOOL.class, 0xA0, idSet); // 4.7 ?
		addPacketOpcode(SM_MAIL_SERVICE.class, 0xA1, idSet);
		addPacketOpcode(SM_SUMMON_USESKILL.class, 0xA2, idSet);
		addPacketOpcode(SM_WINDSTREAM.class, 0xA3, idSet);
		addPacketOpcode(SM_WINDSTREAM_ANNOUNCE.class, 0xA4, idSet);
		addPacketOpcode(SM_FIND_GROUP.class, 0xA6, idSet);
		addPacketOpcode(SM_REPURCHASE.class, 0xA7, idSet);
		addPacketOpcode(SM_WAREHOUSE_INFO.class, 0xA8, idSet);
		addPacketOpcode(SM_WAREHOUSE_ADD_ITEM.class, 0xA9, idSet);
		addPacketOpcode(SM_DELETE_WAREHOUSE_ITEM.class, 0xAA, idSet);
		addPacketOpcode(SM_WAREHOUSE_UPDATE_ITEM.class, 0xAB, idSet);
		addPacketOpcode(SM_IN_GAME_SHOP_CATEGORY_LIST.class, 0xAC, idSet); // ingameshop
		addPacketOpcode(SM_IN_GAME_SHOP_LIST.class, 0xAD, idSet); // ingameshop
		addPacketOpcode(SM_IN_GAME_SHOP_ITEM.class, 0xAE, idSet); // ingameshop
		addPacketOpcode(SM_ICON_INFO.class, 0xAF, idSet);
		addPacketOpcode(SM_TITLE_INFO.class, 0xB0, idSet);
		addPacketOpcode(SM_CHARACTER_SELECT.class, 0xB1, idSet);
		// addPacketOpcode(SM_BROKER_REGISTERED_LIST.class, 0xB1, idSet);
		addPacketOpcode(SM_CRAFT_ANIMATION.class, 0xB4, idSet);
		addPacketOpcode(SM_CRAFT_UPDATE.class, 0xB5, idSet);
		addPacketOpcode(SM_ASCENSION_MORPH.class, 0xB6, idSet);
		addPacketOpcode(SM_ITEM_USAGE_ANIMATION.class, 0xB7, idSet);
		addPacketOpcode(SM_CUSTOM_SETTINGS.class, 0xB8, idSet);
		addPacketOpcode(SM_DUEL.class, 0xB9, idSet);
		//addPacketOpcode(SM_FORCED_NPC_MOVE.class, 0xBA, idSet); //TODO: This is valid, but the class doesn't exist; int, float, float, float -- ObjId x y z
		addPacketOpcode(SM_PET_EMOTE.class, 0xBB, idSet);
		//addPacketOpcode(SM_FORCE_DISCONNECT.class, 0xBC, idSet); //TODO: This is valid, but the class doesn't exist. Sending this packet closes the connection, but does not force the client to close.
		addPacketOpcode(SM_QUESTIONNAIRE.class, 0xBF, idSet);
		addPacketOpcode(SM_DIE.class, 0xC1, idSet);
		addPacketOpcode(SM_RESURRECT.class, 0xC2, idSet);
		addPacketOpcode(SM_FORCED_MOVE.class, 0xC3, idSet);
		addPacketOpcode(SM_TELEPORT_MAP.class, 0xC4, idSet);
		addPacketOpcode(SM_USE_OBJECT.class, 0xC5, idSet);
		addPacketOpcode(SM_EMOTION_SWITCH.class, 0xC6, idSet);
		addPacketOpcode(SM_L2AUTH_LOGIN_CHECK.class, 0xC7, idSet);
		addPacketOpcode(SM_CHARACTER_LIST.class, 0xC8, idSet);
		addPacketOpcode(SM_CREATE_CHARACTER.class, 0xC9, idSet);
		addPacketOpcode(SM_DELETE_CHARACTER.class, 0xCA, idSet);
		addPacketOpcode(SM_RESTORE_CHARACTER.class, 0xCB, idSet);
		addPacketOpcode(SM_TARGET_IMMOBILIZE.class, 0xCC, idSet);
		addPacketOpcode(SM_LOOT_STATUS.class, 0xCD, idSet);
		addPacketOpcode(SM_LOOT_ITEMLIST.class, 0xCE, idSet);
		addPacketOpcode(SM_RECIPE_LIST.class, 0xCF, idSet);
		addPacketOpcode(SM_MANTRA_EFFECT.class, 0xD0, idSet);
		addPacketOpcode(SM_SIEGE_LOCATION_INFO.class, 0xD1, idSet);
		addPacketOpcode(SM_SIEGE_LOCATION_STATE.class, 0xD2, idSet);
		addPacketOpcode(SM_PLAYER_SEARCH.class, 0xD3, idSet);
		//addPacketOpcode(SM_EMBLEM_UPLOAD_RESPONSE.class, 0xD4, idSet); //TODO: This is valid but the class doesn't exist; byte -- 0 confirms, nonzero rejects
		addPacketOpcode(SM_LEGION_SEND_EMBLEM.class, 0xD5, idSet);
		addPacketOpcode(SM_LEGION_SEND_EMBLEM_DATA.class, 0xD6, idSet);
		addPacketOpcode(SM_LEGION_UPDATE_EMBLEM.class, 0xD7, idSet);
		// addPacketOpcode(SM_REGION_INFO.class, 0xD8, idSet); // 4.7 ?
		// addPacketOpcode(SM_PLAYER_REGION.class, 0xD9, idSet); // 4.7 ?
		addPacketOpcode(SM_SHIELD_EFFECT.class, 0xDA, idSet);
		addPacketOpcode(SM_ABYSS_ARTIFACT_INFO.class, 0x60, idSet); //TODO: This is out of order! Verify opcode
		// TODO! 0xD9 format: ccc time milis: d time milis: d
		// addPacketOpcode(SM_ABYSS_ARTIFACT_INFO2.class, 0xDA, idSet);
		// addPacketOpcode(SM_ABYSS_ARTIFACT_INFO3.class, 0xDD, idSet);
		addPacketOpcode(SM_FRIEND_RESPONSE.class, 0xDE, idSet);
		addPacketOpcode(SM_BLOCK_RESPONSE.class, 0xDF, idSet);
		addPacketOpcode(SM_BLOCK_LIST.class, 0xE0, idSet);
		addPacketOpcode(SM_FRIEND_NOTIFY.class, 0xE1, idSet);
		addPacketOpcode(SM_TOWNS_LIST.class, 0xE2, idSet);
		addPacketOpcode(SM_FRIEND_STATUS.class, 0xE3, idSet);
		addPacketOpcode(SM_CHANNEL_INFO.class, 0xE5, idSet);
		addPacketOpcode(SM_CHAT_INIT.class, 0xE6, idSet);
		addPacketOpcode(SM_MACRO_LIST.class, 0xE7, idSet);
		addPacketOpcode(SM_MACRO_RESULT.class, 0xE8, idSet);
		addPacketOpcode(SM_NICKNAME_CHECK_RESPONSE.class, 0xE9, idSet);
		addPacketOpcode(SM_BIND_POINT_INFO.class, 0xEB, idSet);
		addPacketOpcode(SM_RIFT_ANNOUNCE.class, 0xEC, idSet);
		addPacketOpcode(SM_ABYSS_RANK.class, 0xED, idSet);
		addPacketOpcode(SM_PETITION.class, 0xEF, idSet);
		addPacketOpcode(SM_FRIEND_UPDATE.class, 0xF0, idSet);
		addPacketOpcode(SM_LEARN_RECIPE.class, 0xF1, idSet);
		addPacketOpcode(SM_RECIPE_DELETE.class, 0xF2, idSet);
		addPacketOpcode(SM_FLY_TIME.class, 0xF4, idSet);
		addPacketOpcode(SM_ALLIANCE_INFO.class, 0xF5, idSet);
		addPacketOpcode(SM_ALLIANCE_MEMBER_INFO.class, 0xF6, idSet);
		addPacketOpcode(SM_LEAVE_GROUP_MEMBER.class, 0xF7, idSet);
		addPacketOpcode(SM_SHOW_BRAND.class, 0xF9, idSet);
		addPacketOpcode(SM_ALLIANCE_READY_CHECK.class, 0xFA, idSet);
		addPacketOpcode(SM_PRICES.class, 0xFC, idSet);
		addPacketOpcode(SM_TRADELIST.class, 0xFD, idSet);
		addPacketOpcode(SM_RECONNECT_KEY.class, 0xFF, idSet);
		addPacketOpcode(SM_INSTANCE_STAGE_INFO.class, 0x8C, idSet); //TODO: This is out of order! Verify opcode
		addPacketOpcode(SM_HOUSE_BIDS.class, 0x100, idSet);
		addPacketOpcode(SM_RECEIVE_BIDS.class, 0x103, idSet);
		addPacketOpcode(SM_HOUSE_PAY_RENT.class, 0x106, idSet); // 4.7 ?
		addPacketOpcode(SM_HOUSE_OWNER_INFO.class, 0x107, idSet);
		addPacketOpcode(SM_OBJECT_USE_UPDATE.class, 0x108, idSet);
		addPacketOpcode(SM_PACKAGE_INFO_NOTIFY.class, 0x10A, idSet);
		addPacketOpcode(SM_HOUSE_OBJECT.class, 0x10C, idSet);
		addPacketOpcode(SM_DELETE_HOUSE_OBJECT.class, 0x10D, idSet);
		addPacketOpcode(SM_HOUSE_OBJECTS.class, 0x10E, idSet);
		addPacketOpcode(SM_HOUSE_RENDER.class, 0x10F, idSet);
		addPacketOpcode(SM_HOUSE_UPDATE.class, 0x3D, idSet); //TODO: This is out of order! Verify opcode
		addPacketOpcode(SM_DELETE_HOUSE.class, 0x110, idSet);
		addPacketOpcode(SM_HOUSE_ACQUIRE.class, 0x113, idSet);
		addPacketOpcode(SM_GROUP_DATA_EXCHANGE.class, 0xB2, idSet); //TODO: This is out of order! Verify opcode
		addPacketOpcode(SM_INSTANCE_COUNT_INFO.class, 0x93, idSet); //TODO: This is out of order! Verify opcode
		addPacketOpcode(SM_MARK_FRIENDLIST.class, 0x117, idSet);
		addPacketOpcode(SM_DISPUTE_LAND.class, 0x11B, idSet); //TODO: This is out of order! Verify opcode
		addPacketOpcode(SM_HOUSE_TELEPORT.class, 0xDD, idSet); //TODO: This is out of order! Verify opcode
		addPacketOpcode(SM_CHALLENGE_LIST.class, 0x118, idSet);
		addPacketOpcode(SM_ACCOUNT_ACCESS_PROPERTIES.class, 0xEE, idSet); //TODO: This is out of order! Verify opcode
		addPacketOpcode(SM_MEGAPHONE.class, 0x11D, idSet);
		addPacketOpcode(SM_RIDE_ROBOT.class, 0x5C, idSet); //TODO: This is out of order! Verify opcode
		addPacketOpcode(SM_SECURITY_TOKEN.class, 0x112, idSet); //TODO: This is out of order! Verify opcode
		addPacketOpcode(SM_SELECT_ITEM_ADD.class, 0x11E, idSet);
		addPacketOpcode(SM_SELECT_ITEM_LIST.class, 0x11C, idSet); //TODO: This is out of order! Verify opcode
		addPacketOpcode(SM_FATIGUE.class, 0xB3, idSet); //TODO: This is out of order! Verify opcode
		addPacketOpcode(SM_TUNE_PREVIEW.class, 0x120, idSet);
		addPacketOpcode(SM_HOTSPOT_TELEPORT.class, 0x128, idSet);
		addPacketOpcode(SM_UPGRADE_ARCADE.class, 0x12A, idSet);
		addPacketOpcode(SM_LOGIN_REWARD.class, 0x12B, idSet);
		addPacketOpcode(SM_GAMEGUARD.class, 0x7D, idSet); //TODO: This is out of order! Verify opcode
		addPacketOpcode(SM_CUSTOM_PACKET.class, 99999, idSet); // fake packet
		// ------------------FAST TRACK PACKETS----------------------//
		addPacketOpcode(SM_SERVER_IDS.class, 0x114, idSet); //TODO: This is out of order! Verify opcode
		addPacketOpcode(SM_REQUEST_BEGINNER_SERVER.class, 0x96, idSet); //TODO: This is out of order! Verify opcode
		addPacketOpcode(SM_MOVE_BEGINNER_SERVER.class, 0x105, idSet); //TODO: This is out of order! Verify opcode
		// ------------------FAST TRACK PACKETS----------------------//
		// --------------------UNKNOWN PACKETS-----------------------//
		addPacketOpcode(SM_UNK_104.class, 0x104, idSet); //TODO: This is out of order! Verify opcode
		addPacketOpcode(SM_FC_UNK.class, 0xFB, idSet); //TODO: This is out of order! Verify opcode
		addPacketOpcode(SM_AFTER_TIME_CHECK.class, 0x124, idSet); //TODO: This is out of order! Verify opcode
		addPacketOpcode(SM_BE_UNK.class, 0xBD, idSet); //TODO: This is out of order! Verify opcode
		addPacketOpcode(SM_A6_UNK.class, 0xA5, idSet); //TODO: This is out of order! Verify opcode
		// --------------------UNKNOWN PACKETS-----------------------//
	}
	
	static int getOpcode(Class<? extends AionServerPacket> packetClass) {
		Integer opcode = opcodes.get(packetClass);
		if (opcode == null) throw new IllegalArgumentException("There is no opcode for " + packetClass + " defined.");
		
		return opcode;
	}
	
	private static void addPacketOpcode(Class<? extends AionServerPacket> packetClass, int opcode, Set<Integer> idSet) {
		if (opcode < 0) return;
		
		if (idSet.contains(opcode)) throw new IllegalArgumentException(String.format("There already exists another packet with id 0x%02X", opcode));
		
		idSet.add(opcode);
		opcodes.put(packetClass, opcode);
	}
	
}

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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.siege.ArtifactLocation;
import com.aionemu.gameserver.network.PacketLoggerService;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * Opens a yes/no question window on the client. Question based on the code given, defined in client_strings.xml
 *
 * @author Ben, avol, Lyahim
 */
public class SM_QUESTION_WINDOW extends AionServerPacket {
	
	/**
	 * %0 has challenged you to a duel. Do you accept?
	 */
	public static final int STR_DUEL_DO_YOU_ACCEPT_REQUEST = 50028;
	/**
	 * Do you want to withdraw your challenge to %0?
	 */
	public static final int STR_DUEL_DO_YOU_WITHDRAW_REQUEST = 50030;
	/**
	 * %0 has invited you to join a group. Accept the invitation?
	 */
	public static final int STR_PARTY_DO_YOU_ACCEPT_INVITATION = 60000;
	/**
	 * %0 has invited you to join an Alliance. Accept the invitation?
	 */
	public static final int STR_PARTY_ALLIANCE_DO_YOU_ACCEPT_HIS_INVITATION = 70000;
	/**
	 * %0 has asked to change the Alliance's loot distribution to 'free for all' mode. Accept?
	 */
	public static final int STR_PARTY_ALLIANCE_CHANGE_LOOT_TO_FREE_HE_ASKED = 70001;
	/**
	 * %0 has asked to change the Alliance's loot distribution to 'auto loot' mode. Accept?
	 */
	public static final int STR_PARTY_ALLIANCE_CHANGE_LOOT_TO_RANDOM_HE_ASKED = 70002;
	/**
	 * %0 requested permission to pick up %1. Grant permission?
	 */
	public static final int STR_PARTY_ALLIANCE_PICKUP_ITEM_HE_ASKED = 70003;
	/**
	 * %0 has invited you to join an Alliance. Accept the invitation?
	 */
	public static final int STR_FORCE_DO_YOU_ACCEPT_INVITATION = 70004;
	/**
	 * Creating a Legion requires &lt;font font_xml="v3_msgbox_money"&gt;%qina0&lt;/font&gt;. Create a Legion?
	 */
	public static final int STR_GUILD_CREATE_DO_YOU_ACCEPT_PAY = 80000;
	/**
	 * You have been invited to the %0 Legion (Level %1) by %2. Accept the invitation?
	 */
	public static final int STR_GUILD_INVITE_DO_YOU_ACCEPT_INVITATION = 80001;
	/**
	 * Are you sure you want to transfer Legion Brigade General authority to %0?
	 */
	public static final int STR_GUILD_TRANSFER_GUILDMASTER = 80005;
	/**
	 * Are you sure you want to leave the %0 Legion?
	 */
	public static final int STR_GUILD_DO_YOU_LEAVE = 80006;
	/**
	 * Are you sure you want to kick %0 out of the Legion?
	 */
	public static final int STR_GUILD_DO_YOU_BANISH = 80007;
	/**
	 * The Legion will remain in disbanding mode for a day following your request. Are you sure you wish to &lt;font
	 * color="ff0000"&gt;disband&lt;/font&gt; the Legion?
	 */
	public static final int STR_GUILD_DISPERSE_STAYMODE = 80008;
	/**
	 * The %0 Legion is currently waiting to be disbanded. Do you want to cancel the disbanding process?
	 */
	public static final int STR_GUILD_DISPERSE_STAYMODE_CANCEL = 80009;
	/**
	 * Level upgrade requires &lt;font font_xml="v3_msgbox_money"&gt;%qina0&lt;/font&gt;. Do you want to upgrade?
	 */
	public static final int STR_GUILD_CHANGE_LEVEL_DO_YOU_ACCEPT_PAY = 80010;
	/**
	 * %0 nominated you as Legion Brigade General. Accept the position?
	 */
	public static final int STR_GUILD_CHANGE_MASTER_DO_YOU_ACCEPT_OFFER = 80011;
	/**
	 * The price of this item is set rather high. Are you sure you want to buy it?
	 */
	public static final int STR_BUY_SELL_CONFIRM_PURCHASE_EXCESSIVE_PRICE = 90000;
	/**
	 * %0 has asked you to trade items. Accept?
	 */
	public static final int STR_EXCHANGE_DO_YOU_ACCEPT_EXCHANGE = 90001;
	/**
	 * Are you sure you want to abandon this quest?
	 */
	public static final int STR_QUEST_GIVEUP = 150000;
	/**
	 * Discard %0 and abandon the %1 quest?
	 */
	public static final int STR_QUEST_GIVEUP_WHEN_DELETE_QUEST_ITEM = 150001;
	/**
	 * &lt;p&gt;You can restore XP and remove the resurrection aftereffect if your soul is healed. Soul healing requires &lt;font
	 * font_xml="v3_msgbox_money"&gt;%qina0&lt;/font&gt;.&lt;/p&gt;&lt;p&gt;Do you want to heal your soul?&lt;/p&gt;
	 */
	public static final int STR_ASK_RECOVER_EXPERIENCE = 160011;
	/**
	 * Binding to this location costs &lt;font font_xml="v3_msgbox_money"&gt;%qina0&lt;/font&gt;. Proceed?
	 */
	public static final int STR_ASK_REGISTER_RESURRECT_POINT = 160012;
	/**
	 * Do you want to travel using the magic passage?
	 */
	public static final int STR_ASK_GROUP_GATE_DO_YOU_ACCEPT_MOVE = 160014;
	/**
	 * Using the artifact requires &lt;font font_xml="v3_msgbox_money"&gt;%1 %0(s)&lt;/font&gt;. Proceed?
	 */
	public static final int STR_ASK_USE_ARTIFACT = 160016;
	/**
	 * Do you want to pass through the castle gate?
	 */
	public static final int STR_ASK_PASS_BY_GATE = 160017;
	/**
	 * Do you want to bind yourself to this Obelisk?
	 */
	public static final int STR_ASK_REGISTER_BINDSTONE = 160018;
	/**
	 * Do you want to teleport through the Rift?
	 */
	public static final int STR_ASK_PASS_BY_DIRECT_PORTAL = 160019;
	/**
	 * The repair costs &lt;font font_xml="v3_msgbox_money"&gt;%0(%1 pieces)&lt;/font&gt;. Repair?
	 */
	public static final int STR_ASK_DOOR_REPAIR_DO_YOU_ACCEPT_REPAIR = 160021;
	/**
	 * @
	 */
	public static final int STR_ASK_DOOR_REPAIR_POPUPDIALOG = 160027;
	/**
	 * You are currently a member of %0. Do you want to leave it to join %1?
	 */
	public static final int STR_ASK_JOIN_NEW_FACTION = 160033;
	/**
	 * %0 is an untradable item. Are you sure you want to acquire it?
	 */
	public static final int STR_CONFIRM_LOOT = 900495;
	/**
	 * &lt;p&gt;To expand the cube you need &lt;font font_xml="v3_msgbox_money"&gt;%qina0&lt;/font&gt; Kinah.&lt;/p&gt; &lt;p&gt;Do you want to expand
	 * it?&lt;/p&gt;
	 */
	public static final int STR_WAREHOUSE_EXPAND_WARNING = 900686;
	/**
	 * To upgrade the %0 skill, you need &lt;font font_xml="v3_msgbox_money"&gt;%qina1&lt;/font&gt;. Are you sure you want to upgrade the skill?
	 */
	public static final int STR_CRAFT_ADDSKILL_CONFIRM = 900852;
	/**
	 * &lt;p align="left"&gt;%0 is about to summon you using %1. Will you accept?&lt;/p&gt; &lt;p&gt;&lt;/p&gt;&lt;p align="left"&gt;You must decide
	 * in %2 seconds.&lt;/p&gt;
	 */
	public static final int STR_SUMMON_PARTY_DO_YOU_ACCEPT_REQUEST = 901721;
	/**
	 * Enter %WORLDNAME0 (Difficulty: %1)?
	 */
	public static final int STR_INSTANCE_DUNGEON_WITH_DIFFICULTY_ENTER_CONFIRM = 902050;
	/**
	 * &lt;p&gt;%0 legion has invited your force to join their League.&lt;/p&gt;&lt;p&gt;Will you accept the invitation?&lt;/p&gt;
	 */
	public static final int STR_MSGBOX_UNION_INVITE_ME = 902249;
	/**
	 * &lt;p&gt;The %0 must be soulbound to equip it.&lt;/p&gt; &lt;p&gt;You cannot trade the item that has been soulbound.&lt;/p&gt;
	 * &lt;p&gt;Equip?&lt;/p&gt;
	 */
	public static final int STR_SOUL_BOUND_ITEM_DO_YOU_WANT_SOUL_BOUND = 95006;
	/**
	 * &lt;p&gt;All the equipped items will be conditioned to the maximum level. &lt;/p&gt;&lt;p&gt;You need &lt;font
	 * font_xml="v3_msgbox_money"&gt;%qina0&lt;/font&gt;to do this. Are you sure you want to condition them?&lt;/p&gt;
	 */
	public static final int STR_ITEM_CHARGE_ALL_CONFIRM = 903026;
	public static final int STR_ITEM_CHARGE2_ALL_CONFIRM = 904039;
	/**
	 * &lt;p&gt;You can condition only %0 of the registered items up to level %1. &lt;/p&gt;&lt;p&gt;You need &lt;font
	 * font_xml="v3_msgbox_money"&gt;%qina2&lt;/font&gt;to do this. &lt;/p&gt;&lt;p&gt;Are you sure you want to condition them?&lt;/p&gt;
	 */
	public static final int STR_ITEM_CHARGE_CONFIRM_SOME_ALREADY_CHARGED = 903028;
	/**
	 * You can make %1 by assembling %0. Do you want to assemble it
	 */
	public static final int STR_ASSEMBLY_ITEM_POPUP_CONFIRM = 903441;
	/**
	 * You are about to teleport to your own house. Continue?
	 */
	public static final int STR_HOUSING_TELEPORT_HOME_CONFIRM = 903533;
	/**
	 * You are about to teleport to %0's house. Continue?
	 */
	public static final int STR_HOUSING_TELEPORT_BUDDY_CONFIRM = 903534;
	/**
	 * You are about to teleport to the house of a friend of the current house owner. Continue?
	 */
	public static final int STR_HOUSING_TELEPORT_RANDOM_CONFIRM = 903535;
	/**
	 * You are about to teleport to your Legion's house. Continue?
	 */
	public static final int STR_HOUSING_TELEPORT_GUILD_CONFIRM = 903536;
	/**
	 * %0 wants to list you as a friend. Do you want to accept it?
	 */
	public static final int STR_BUDDYLIST_ADD_BUDDY_REQUEST = 1300911;
	/**
	 * %0 declined your trade offer. TODO: make it a simple box, not a question.
	 */
	public static final int STR_EXCHANGE_HE_REJECTED_EXCHANGE = 1300354;
	private int code;
	private int senderId;
	private int range;
	private Object[] params;
	private ArtifactLocation artifact;
	
	/**
	 * Creates a new <tt>SM_QUESTION_WINDOW<tt> packet
	 *
	 * @param code code The string code to display, found in client_strings.xml
	 * @param senderId sender Object id
	 * @param params params The parameters for the string, if any
	 */
	public SM_QUESTION_WINDOW(int code, int senderId, int range, Object... params) {
		this.code = code;
		this.senderId = senderId;
		this.range = range;
		this.params = params;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		PacketLoggerService.getInstance().logPacketSM(this.getPacketName());
		writeD(code);
		
		if (code == STR_INSTANCE_DUNGEON_WITH_DIFFICULTY_ENTER_CONFIRM) {
			writeH(0x33);
			writeH(0x30);
			writeH(0x30);
			writeH(0x31);
			writeH(0x37);
			writeH(0x30);
			writeH(0x30);
			writeH(0x30);
			writeH(0x30);
			writeH(0x00);
		}
		
		for (Object param : params) {
			if (param instanceof DescriptionId) {
				writeH(0x24);
				writeD(((DescriptionId) param).getValue());
				writeH(0x00); // unk
			} else if (param instanceof ArtifactLocation) {
				this.artifact = (ArtifactLocation) param;
			} else {
				writeS(String.valueOf(param));
			}
		}
		
		// Guardian Stone Activation Window
		if (code == STR_ASK_DOOR_REPAIR_POPUPDIALOG) {
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeH(0x00);
			writeC(0x01);
			writeD(senderId);
			writeD(0x05);
		} // ArtifactLocation Activation Window
		else if (code == 160028) {
			writeD(0x00);
			writeD(0x00);
			writeH(0x00);
			writeC(0x00);
			writeD(0x00);
			if (artifact == null) {
				writeD(0x00);
			} else {
				writeD(artifact.getCoolDown());// ArtifactLocation reuse
			}
		} else if (code == STR_BUDDYLIST_ADD_BUDDY_REQUEST) {
			writeB(new byte[17]);
		} else if (code == STR_INSTANCE_DUNGEON_WITH_DIFFICULTY_ENTER_CONFIRM) {
			writeD(0x00);
			writeH(0x00);
			writeC(0x01);
			writeD(senderId);
			writeD(0x05);
		} else {
			writeD(0x00);// unk
			writeD(0x00);// unk
			writeH(0x00);// unk
			writeC(range > 0 ? 0x01 : 0x00);// unk maybe boolean for rangecheck?
			writeD(senderId);
			writeD(range);// range within the Question is valod
		}
	}
}

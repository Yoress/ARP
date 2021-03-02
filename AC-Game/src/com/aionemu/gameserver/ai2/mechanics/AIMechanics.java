/**
 * This file is part of the Aion Reconstruction Project Server.
 *
 * The Aion Reconstruction Project Server is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * The Aion Reconstruction Project Server is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with the Aion Reconstruction Project Server. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * @AionReconstructionProjectTeam
 */
package com.aionemu.gameserver.ai2.mechanics;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.aionemu.gameserver.ai2.AI2Engine;
import com.aionemu.gameserver.ai2.mechanics.actions.Action;
import com.aionemu.gameserver.ai2.mechanics.actions.Action.DoNothing;
import com.aionemu.gameserver.ai2.mechanics.adapters.AIMechanicsAdapter;
import com.aionemu.gameserver.ai2.mechanics.conditions.Condition;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.configs.main.AIConfig;
import com.aionemu.gameserver.dataholders.loadingutils.XmlValidationHandler;

import javolution.util.FastMap;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@XmlJavaTypeAdapter(AIMechanicsAdapter.class)
public final class AIMechanics {
	
	final private static FastMap<String, AIMechanics> AI_MECHANICS = new FastMap<String, AIMechanics>();
	
	private static MechanicData data;
	
	private static Boolean init = Boolean.FALSE;
	
	public final String mechanicId;
	
	private final FastMap<MechanicEventType, MechanicEventHandler> handlers;
	
	public AIMechanics(String name, FastMap<MechanicEventType, MechanicEventHandler> handlers) {
		mechanicId = name;
		this.handlers = handlers;
		AI_MECHANICS.put(name.toLowerCase(), this);
	}
	
	public boolean hasHandler(MechanicEventType type) {
		return handlers.containsKey(type);
	}
	
	public MechanicEventHandler getHandler(MechanicEventType type) {
		return handlers.get(type);
	}
	
	public boolean handleMechanicEvent(MechanicEvent event, AbstractMechanicsAI2 ai) throws DoNothing {
		MechanicEventHandler handler = handlers.get(event.type);
		if (handler != null) return handler.doPattern(event, ai);
		return false;
	}
	
	public static boolean isSupportedMechanic(String mechanic) {
		if (mechanic == null || mechanic.isEmpty()) return false;
		return AI_MECHANICS.containsKey(mechanic.toLowerCase());
	}
	
	public static AIMechanics getMechanic(String mechanic) {
		if (mechanic == null || mechanic.isEmpty()) return null;
		return AI_MECHANICS.get(mechanic.toLowerCase());
	}
	
	public static synchronized Condition addOrGetCanonical(Condition condition) {
		if (init == null || !init || data == null || condition == null) return null;
		Condition ret = data.conditions.get(condition);
		if (ret == null) {
			data.conditions.put(condition, condition);
			return condition;
		}
		return ret;
	}
	
	public static synchronized Action addOrGetCanonical(Action action) {
		if (init == null || !init || data == null || action == null) return null;
		Action ret = data.actions.get(action);
		if (ret == null) {
			data.actions.put(action, action);
			return action;
		}
		return ret;
	}
	
	public static void initialize() {
		if (!AIConfig.MECHANIC_SYSTEM_ENABLED) {
			init = null;
			return;
		}
		synchronized (init) {
			if (init == null || init) return;
			init = Boolean.TRUE;
		}
		
		data = new MechanicData();
		try {
			Schema schema = null;
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schema = sf.newSchema(new File("./data/mechanics/mechanics.xsd"));
			JAXBContext jcon = JAXBContext.newInstance(MechanicData.class);
			Unmarshaller jUnmarsh = jcon.createUnmarshaller();
			jUnmarsh.setEventHandler(new XmlValidationHandler());
			jUnmarsh.setSchema(schema);
			jUnmarsh.unmarshal(new File("./data/mechanics/mechanics.xml"));
		} catch (SAXException e) {
			System.err.println("Unable to load AI Mechanics!");
			e.printStackTrace();
		} catch (JAXBException e) {
			System.err.println("Unable to load AI Mechanics!");
			e.printStackTrace();
		}
		
		/*
		 * FIXME
		 * JAXB kept giving me the finger; it left this list null or empty even though the list's contents were loaded.
		 * Working around it via the constructor of AIMechanics class until a later date.
		 */
//		if (data.mechanics != null) for (AIMechanics mechanicData: data.mechanics) {
//			AI_MECHANICS.put(mechanicData.mechanicId, mechanicData);
//		}
		
		if (AI_MECHANICS.size() > 0) AI2Engine.getInstance().registerAI(MechanicsAI2.class);
		data.actions.clear();
		data.actions = null;
		data.conditions.clear();
		data.conditions = null;
		if (data.mechanics != null) data.mechanics.clear();
		data.mechanics = null;
		data = null;
		init = null;
	}
}

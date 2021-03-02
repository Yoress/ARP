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
package com.aionemu.gameserver.ai2.ai.registration;

import java.lang.reflect.Modifier;

import com.aionemu.commons.utils.ClassUtils;
import com.aionemu.gameserver.ai2.AI2Engine;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.ai2.ai.AggressiveNpcAI2;
import com.aionemu.gameserver.ai2.ai.GeneralNpcAI2;
import com.aionemu.gameserver.ai2.ai.TrapNpcAI2;
import com.aionemu.gameserver.ai2.ai.instance.fire_temple.BlueKalgolemAI2;
import com.aionemu.gameserver.ai2.ai.instance.fire_temple.BlueObscuraAI2;
import com.aionemu.gameserver.ai2.ai.instance.fire_temple.BrokenWingKutisenAI2;
import com.aionemu.gameserver.ai2.ai.instance.fire_temple.CaryatidAI2;
import com.aionemu.gameserver.ai2.ai.instance.fire_temple.ChaliceGuardDenlavisAI2;
import com.aionemu.gameserver.ai2.ai.instance.fire_temple.GargoyleAI2;
import com.aionemu.gameserver.ai2.ai.instance.fire_temple.InfernoFireSpiritAI2;
import com.aionemu.gameserver.ai2.ai.instance.fire_temple.KromedeAI2;
import com.aionemu.gameserver.ai2.ai.instance.fire_temple.KromedeTrapAI2;
import com.aionemu.gameserver.ai2.ai.instance.fire_temple.MercilessFireSpiritAI2;
import com.aionemu.gameserver.ai2.ai.instance.fire_temple.MugolemAI2;
import com.aionemu.gameserver.ai2.ai.instance.fire_temple.RedKalgolemAI2;
import com.aionemu.gameserver.ai2.ai.instance.fire_temple.RedObscuraAI2;


/**
 * This class is the result of the easiest method to skirt placing AI Classes into
 * the static script data of the server. Any AI Classes that are in the
 * {@link com.aionemu.gameserver.ai2.ai} package will be registered to the
 * {@link AI2Engine} from here.
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class DefaultAIRegistrationHandler {
	
	@SuppressWarnings("unchecked")
	public final static void registerAIs() {
		// TODO: Do this shit programmatically
		Class<?>[] classes = {GeneralNpcAI2.class,
		                      AggressiveNpcAI2.class,
		                      TrapNpcAI2.class,
		                      BlueKalgolemAI2.class,
		                      BlueObscuraAI2.class,
		                      BrokenWingKutisenAI2.class,
		                      CaryatidAI2.class,
		                      ChaliceGuardDenlavisAI2.class,
		                      GargoyleAI2.class,
		                      InfernoFireSpiritAI2.class,
		                      KromedeAI2.class,
		                      KromedeTrapAI2.class,
		                      MercilessFireSpiritAI2.class,
		                      MugolemAI2.class,
		                      RedKalgolemAI2.class,
		                      RedObscuraAI2.class};
		
		for (Class<?> c: classes) {
			if (ClassUtils.isSubclass(c, AbstractAI.class) && isValidClass(c)) {
				AI2Engine.getInstance().registerAI((Class<? extends AbstractAI>) c);
			}
		}
	}
	
	public static boolean isValidClass(Class<?> clazz) {
		final int modifiers = clazz.getModifiers();
		
		if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers)) {
			return false;
		}
		
		if (!Modifier.isPublic(modifiers)) {
			return false;
		}
		
		return true;
	}
	
}

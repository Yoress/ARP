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
/**
 * This package, along with all sub-packages (with the exception of
 * {@link com.aionemu.gameserver.ai2.ai.registration registration}) contains all of the basic AI's
 * this server is expected to operate with.
 * <p>
 * The server is designed in a fashion that AI's can be loaded from static script data,
 * compiled at runtime, and held in memory; that system is intended for custom AI implementations
 * from the server owners. The AI's found in this package are intended to be the default
 * (expected) AI's during retail-like operation.
 * <p>
 * It's up to the server owners to decide if these AI's should be overridden, and to manage
 * any static data associated with overriding them for any NPC.
 * <p>
 * In previous iterations of this emulator, the AI's stored in this package were located in
 * the static script data mentioned above; they have been moved here so that they may be
 * unloaded by the JVM during operation.
 */
package com.aionemu.gameserver.ai2.ai;
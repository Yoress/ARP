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
package com.aionemu.gameserver.dao;

import java.util.ArrayList;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.taskmanager.fromdb.trigger.TaskFromDBTrigger;

/**
 * @author Divinity, nrg
 */
public abstract class TaskFromDBDAO implements DAO {
	
	/**
	 * Return all tasks from DB
	 *
	 * @return all tasks
	 */
	public abstract ArrayList<TaskFromDBTrigger> getAllTasks();
	
	/**
	 * Returns class name that will be uses as unique identifier for all DAO classes
	 *
	 * @return class name
	 */
	@Override
	public final String getClassName() {
		return TaskFromDBDAO.class.getName();
	}
}

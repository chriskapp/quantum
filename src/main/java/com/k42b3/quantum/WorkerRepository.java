/**
 * Quantum is an aggregator service which collects messages from different 
 * sources and publish them through an REST API.
 * 
 * Copyright (c) 2014 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of Quantum. Quantum is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * Quantum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Quantum. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.quantum;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * WorkerRepository
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/quantum
 */
public class WorkerRepository extends RepositoryAbstract
{
	public WorkerRepository(Connection connection)
	{
		super(connection);
	}

	public List<Worker> getAll() throws SQLException
	{
		ArrayList<Worker> workers = new ArrayList<Worker>();

		Statement stmt = connection.createStatement();
		ResultSet result = stmt.executeQuery("SELECT id, type, params, last_request FROM worker");
		
		while(result.next())
		{
			Worker worker = new Worker();
			worker.setId(result.getInt("id"));
			worker.setType(result.getString("type"));
			worker.setParams(this.unserialize(result.getBytes("params")));
			worker.setLastRequest(result.getDate("last_request"));
			
			workers.add(worker);
		}

		return workers;
	}

	public void insert(Worker worker) throws SQLException
	{
		// @TODO check whether the worker data is valid

		PreparedStatement stmt = connection.prepareStatement("INSERT INTO worker (type, params, last_request) VALUES (?, ?, NULL)");
		stmt.setString(1, worker.getType());
		stmt.setBytes(2, this.serialize(worker.getParams()));
		stmt.execute();
	}
	
	public void remove(Worker worker) throws SQLException
	{
		PreparedStatement stmt = connection.prepareStatement("DELETE FROM worker WHERE id = ?");
		stmt.setInt(1, worker.getId());
		stmt.execute();
	}

	public void updateRequestDate(Worker worker, Date date) throws SQLException
	{
		PreparedStatement stmt = connection.prepareStatement("UPDATE worker SET last_request = ? WHERE id = ?");
		stmt.setDate(1, new java.sql.Date(date.getTime()));
		stmt.setInt(2, worker.getId());
		stmt.execute();
	}

	protected Map<String, String> unserialize(byte[] params)
	{
		try
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(params);
			ObjectInputStream ois = new ObjectInputStream(bais);

			Object object = ois.readObject();

			ois.close();
			bais.close();
			
			if(object instanceof Map)
			{
				return (Map<String, String>) object;
			}
		}
		catch (IOException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch (ClassNotFoundException e)
		{
			logger.error(e.getMessage(), e);
		}

		return null;
	}
	
	protected byte[] serialize(Map<String, String> params)
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);

			oos.writeObject(params);
			byte[] result = baos.toByteArray();

			oos.close();
			baos.close();

			return result;
		}
		catch (IOException e)
		{
			logger.error(e.getMessage(), e);
		}

		return null;
	}
}

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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * MessageRepository
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/quantum
 */
public class MessageRepository extends RepositoryAbstract
{
	public MessageRepository(Connection connection)
	{
		super(connection);
	}
	
	public List<Message> getAll(Date modifiedSince) throws SQLException
	{
    	List<Message> messages = new ArrayList<Message>();
    	String condition = "";

    	if(modifiedSince != null)
    	{
    		condition = " WHERE date > ? ";
    	}

		PreparedStatement stmt = this.connection.prepareStatement("SELECT id, mid, url, message, producer, date FROM message " + condition + " ORDER BY date DESC LIMIT 64");

    	if(modifiedSince != null)
    	{
    		stmt.setDate(1, new java.sql.Date(modifiedSince.getTime()));
    	}

		ResultSet result = stmt.executeQuery();

		while(result.next())
		{
			Message message = new Message();
			message.setId(result.getInt("id"));
			message.setMid(result.getString("mid"));
			message.setUrl(result.getString("url"));
			message.setMessage(result.getString("message"));
			message.setProducer(result.getString("producer"));
			message.setDate(result.getDate("date"));
			
			messages.add(message);
		}

		return messages;
	}
	
	public List<Message> getAll() throws SQLException
	{
		return getAll(null);
	}

	public Message getLatest()
	{
		return null;
	}

	public void insert(Message message) throws SQLException
	{
		PreparedStatement stmt = connection.prepareStatement("INSERT OR IGNORE INTO message (mid, url, message, producer, date) VALUES (?, ?, ?, ?, ?)");
		stmt.setString(1, message.getMid());
		stmt.setString(2, message.getUrl());
		stmt.setString(3, message.getMessage());
		stmt.setString(4, message.getProducer());
		stmt.setDate(5, new java.sql.Date(message.getDate().getTime()));
		stmt.execute();
	}
}

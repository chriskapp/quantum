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

import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;

import com.google.gson.Gson;
import com.k42b3.quantum.handler.HttpHandler;
import com.k42b3.quantum.listener.DatabaseListener;
import com.k42b3.quantum.listener.LogListener;
import com.k42b3.quantum.listener.RequestDateListener;

/**
 * Quantum
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/quantum
 */
public class Quantum
{
	public static final String VERSION = "0.0.1";

	protected Container container;

	/**
	 * How often the worker should check for new messages in minutes
	 */
	protected int port = 8080;

	/**
	 * How often the worker should check for new messages in minutes
	 */
	protected int pollInterval = 1;

	/**
	 * Where to store the database
	 */
	protected String dbPath = "quantum.db";
	
	protected Logger logger = Logger.getLogger("com.k42b3.quantum");

	public Quantum()
	{
		this.container = new Container();
	}

	public Container getContainer()
	{
		return container;
	}

	public void setPort(int port)
	{
		if(port > 0 && port <= 0xFFFF)
		{
			this.port = port;
		}
		else
		{
			throw new InvalidParameterException("Port must be between 1 and " + 0xFFFF);
		}
	}

	public void setPollInterval(int pollInterval)
	{
		if(pollInterval > 0 && pollInterval <= 60)
		{
			this.pollInterval = pollInterval;
		}
		else
		{
			throw new InvalidParameterException("Port must be between 1 and 60");
		}
	}
	
	public void setDbPath(String dbPath)
	{
		this.dbPath = dbPath;
	}

	public void run() throws Exception
	{
		this.connectToDatabase();

		this.buildContainer();

		this.setupDatabase();

		this.startWorker();

		this.startWebServer();
	}

	protected void connectToDatabase() throws Exception
	{
		Class.forName("org.sqlite.JDBC"); 

		Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

		// close connection on shutdown
		Runtime.getRuntime().addShutdownHook(new Thread(){

			public void run()
			{
				try
				{
					if(container.getConnection() != null && !container.getConnection().isClosed())
					{
						container.getConnection().close();
					}
				}
				catch(SQLException e)
				{
					logger.error(e.getMessage(), e);
				}
			}

		});

		container.setConnection(connection);
	}

	protected void buildContainer()
	{
		EventPublisher eventPublisher = new EventPublisher();

		container.setGson(new Gson());
		container.setWorkerRepository(new WorkerRepository(container.getConnection()));
		container.setMessageRepository(new MessageRepository(container.getConnection()));
		container.setQueue(new Queue(eventPublisher));
		container.setWorkerFactory(new WorkerFactory(container.getWorkerRepository(), container.getQueue()));
		container.setService(new Service(pollInterval));

		// event
		eventPublisher.addListener(new LogListener());
		eventPublisher.addListener(new DatabaseListener(container.getMessageRepository()));
		eventPublisher.addListener(new RequestDateListener(container.getWorkerRepository()));

		container.setEventPublisher(new EventPublisher());

		// http handler
		container.setHttpHandler(new HttpHandler(container));
	}

	protected void setupDatabase() throws SQLException
	{
		// message
		Statement stmt = container.getConnection().createStatement();
		String sql = "CREATE TABLE IF NOT EXISTS message (";
		sql+= "id INTEGER PRIMARY KEY AUTOINCREMENT,";
		sql+= "mid VARCHAR(255) UNIQUE NOT NULL,";
		sql+= "url VARCHAR(255) NOT NULL,";
		sql+= "message TEXT NOT NULL,";
		sql+= "producer VARCHAR(255) NOT NULL,";
		sql+= "date DATETIME NOT NULL";
		sql+= ");";

		stmt.executeUpdate(sql);

		// worker
		sql = "CREATE TABLE IF NOT EXISTS worker (";
		sql+= "id INTEGER PRIMARY KEY AUTOINCREMENT,";
		sql+= "type VARCHAR(64) NOT NULL,";
		sql+= "params BLOB NOT NULL,";
		sql+= "last_request DATETIME DEFAULT NULL";
		sql+= ");";

		stmt.executeUpdate(sql);
	}

	protected void startWorker()
	{
		List<WorkerAbstract> workers = container.getWorkerFactory().getAvailableWorker();

		for(int i = 0; i < workers.size(); i++)
		{
			container.getService().add(workers.get(i));
		}
	}

	protected void startWebServer() throws Exception
	{
        Server server = new Server(8080);
        server.setHandler(container.getHttpHandler());
        server.start();
        server.join();
	}
}

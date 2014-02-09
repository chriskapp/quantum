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

import org.eclipse.jetty.server.handler.AbstractHandler;

import com.google.gson.Gson;

/**
 * Container
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/quantum
 */
public class Container
{
	protected Connection connection;
	protected MessageRepository messageRepository;
	protected WorkerRepository workerRepository;
	protected Queue queue;
	protected WorkerFactory workerFactory;
	protected AbstractHandler httpHandler;
	protected Gson gson;
	protected EventPublisher eventPublisher;
	protected Service service;

	public Connection getConnection()
	{
		return connection;
	}
	
	public void setConnection(Connection connection)
	{
		this.connection = connection;
	}
	
	public MessageRepository getMessageRepository()
	{
		return messageRepository;
	}
	
	public void setMessageRepository(MessageRepository messageRepository)
	{
		this.messageRepository = messageRepository;
	}
	
	public WorkerRepository getWorkerRepository()
	{
		return workerRepository;
	}
	
	public void setWorkerRepository(WorkerRepository workerRepository)
	{
		this.workerRepository = workerRepository;
	}
	
	public Queue getQueue()
	{
		return queue;
	}
	
	public void setQueue(Queue queue)
	{
		this.queue = queue;
	}
	
	public WorkerFactory getWorkerFactory()
	{
		return workerFactory;
	}
	
	public void setWorkerFactory(WorkerFactory workerFactory)
	{
		this.workerFactory = workerFactory;
	}

	public AbstractHandler getHttpHandler()
	{
		return httpHandler;
	}

	public void setHttpHandler(AbstractHandler httpHandler)
	{
		this.httpHandler = httpHandler;
	}

	public Gson getGson()
	{
		return gson;
	}

	public void setGson(Gson gson)
	{
		this.gson = gson;
	}

	public EventPublisher getEventPublisher()
	{
		return eventPublisher;
	}

	public void setEventPublisher(EventPublisher eventPublisher)
	{
		this.eventPublisher = eventPublisher;
	}

	public Service getService()
	{
		return service;
	}

	public void setService(Service service)
	{
		this.service = service;
	}
}

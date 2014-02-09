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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.k42b3.quantum.worker.FeedWorker;
import com.k42b3.quantum.worker.GitHubCommitWorker;
import com.k42b3.quantum.worker.IrcWorker;
import com.k42b3.quantum.worker.MailImapWorker;
import com.k42b3.quantum.worker.TwitterWorker;
import com.k42b3.quantum.worker.XmppWorker;

/**
 * WorkerFactory
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/quantum
 */
public class WorkerFactory
{
	protected WorkerRepository workerRepository;
	protected Queue queue;
	
	protected Logger logger = Logger.getLogger("com.k42b3.quantum");
	
	public WorkerFactory(WorkerRepository workerRepository, Queue queue)
	{
		this.workerRepository = workerRepository;
		this.queue = queue;
	}
	
	public List<WorkerAbstract> getAvailableWorker()
	{
		List<WorkerAbstract> availableWorkers = new ArrayList<WorkerAbstract>();
		
		try
		{
			List<Worker> workers = this.workerRepository.getAll();

			for(int i = 0; i < workers.size(); i++)
			{
				WorkerAbstract worker = this.factory(workers.get(i));
				
				if(worker != null)
				{
					availableWorkers.add(worker);
				}
			}
		}
		catch(SQLException e)
		{
			logger.error(e.getMessage(), e);
		}
		
		return availableWorkers;
	}

	public WorkerAbstract factory(Worker worker)
	{
		try
		{
			Class workerClass = Class.forName(worker.getType());
			Constructor constructor = workerClass.getConstructor(Queue.class, Worker.class);
			Object workerInstance = constructor.newInstance(queue, worker);

			if(workerInstance instanceof WorkerAbstract)
			{
				return (WorkerAbstract) workerInstance;
			}
		}
		catch (ClassNotFoundException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch (InstantiationException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch (IllegalAccessException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch (NoSuchMethodException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch (SecurityException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch (IllegalArgumentException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch (InvocationTargetException e)
		{
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	public List<WorkerType> getWorkerTypes()
	{
		List<WorkerType> workers = new ArrayList<WorkerType>();
		workers.add(typeFactory(new FeedWorker(null, null)));
		workers.add(typeFactory(new GitHubCommitWorker(null, null)));
		workers.add(typeFactory(new IrcWorker(null, null)));
		workers.add(typeFactory(new MailImapWorker(null, null)));
		workers.add(typeFactory(new TwitterWorker(null, null)));
		workers.add(typeFactory(new XmppWorker(null, null)));

		return workers;
	}
	
	public WorkerType typeFactory(WorkerAbstract worker)
	{
		WorkerType type = new WorkerType();
		type.setType(worker.getClass().getName());
		type.setParams(worker.getParameters());

		return type;
	}
}

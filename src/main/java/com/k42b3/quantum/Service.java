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

import static java.util.concurrent.TimeUnit.MINUTES;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Service
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/quantum
 */
public class Service
{
	protected ScheduledExecutorService scheduledPool;
	protected int pollInterval;
	protected ArrayList<Thread> threads;

	public Service(int pollInterval)
	{
		this.pollInterval = pollInterval;
		this.scheduledPool = Executors.newScheduledThreadPool(0);
	}
	
	public void add(WorkerAbstract worker)
	{
		if(!worker.needsOwnThread())
		{
			scheduledPool.scheduleAtFixedRate(worker, 0, pollInterval, MINUTES);
		}
		else
		{
			Thread thread = new Thread(worker);
			thread.run();
		}
	}

	public void stop()
	{
		scheduledPool.shutdown();
	}
}

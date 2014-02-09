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

package com.k42b3.quantum.worker;

import java.util.LinkedHashMap;
import java.util.Map;

import com.k42b3.quantum.Queue;
import com.k42b3.quantum.Worker;
import com.k42b3.quantum.WorkerAbstract;

/**
 * Worker wich connects to an xmpp account and optional joins an group chat and 
 * redirects all incomming messages into the queue
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/quantum
 */
public class XmppWorker extends WorkerAbstract
{
	public XmppWorker(Queue queue, Worker worker)
	{
		super(queue, worker);
	}

	public void run()
	{
		// @TODO implement using smack
	}

	@Override
	public Map<String, String> getParameters()
	{
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("host", "Host");
		parameters.put("port", "Port");
		parameters.put("username", "Username");
		parameters.put("password", "Password");
		parameters.put("group", "Group");

		return parameters;
	}

	public boolean needsOwnThread()
	{
		return true;
	}
}

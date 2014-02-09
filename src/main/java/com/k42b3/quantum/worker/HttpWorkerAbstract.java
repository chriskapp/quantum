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

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.k42b3.quantum.Queue;
import com.k42b3.quantum.Worker;
import com.k42b3.quantum.WorkerAbstract;

/**
 * HttpWorkerAbstract
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/quantum
 */
abstract public class HttpWorkerAbstract extends WorkerAbstract
{
	public HttpWorkerAbstract(Queue queue, Worker worker)
	{
		super(queue, worker);
	}

	public void run()
	{
		CloseableHttpClient httpClient = HttpClients.createDefault();

		try
		{
			this.fetch(httpClient);

			httpClient.close();
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
		}
		finally
		{
			try
			{
				httpClient.close();
			}
			catch(IOException e)
			{
				logger.error(e.getMessage(), e);
			}
		}
	}

	public Map<String, String> getParameters()
	{
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("url", "Url");

		return parameters;
	}

	/**
	 * Method which makes an http requests an returns an list of new messages
	 * 
	 * @param httpClient
	 */
	abstract protected void fetch(HttpClient httpClient);
}

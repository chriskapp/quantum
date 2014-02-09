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
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.DateUtils;

import com.k42b3.quantum.Message;
import com.k42b3.quantum.Queue;
import com.k42b3.quantum.Worker;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;

/**
 * Worker which requests an atom feed
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/quantum
 */
public class FeedWorker extends HttpWorkerAbstract
{
	public FeedWorker(Queue queue, Worker worker)
	{
		super(queue, worker);
	}

	@Override
	protected void fetch(HttpClient httpClient)
	{
		try
		{
			String url = worker.getParams().get("url");
			HttpGet httpGet = new HttpGet(url);
			if(worker.getLastRequest() != null)
			{
				httpGet.addHeader("If-Modified-Since", DateUtils.formatDate(worker.getLastRequest()));
			}

			logger.info("Request " + url);

			HttpResponse response = httpClient.execute(httpGet);

			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new InputStreamReader(response.getEntity().getContent()));
			List<SyndEntry> entries = feed.getEntries();

			for(int i = 0; i < entries.size(); i++)
			{
				SyndEntry entry = entries.get(i);

				Message message = new Message();
				message.setMid(entry.getUri());
				message.setUrl(entry.getLink());
				message.setMessage(entry.getTitle());
				message.setProducer(worker.getParams().get("url"));
				message.setDate(entry.getPublishedDate());

				queue.push(this, message);
			}
		}
		catch (IllegalArgumentException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch (FeedException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch (ClientProtocolException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch (IOException e)
		{
			logger.error(e.getMessage(), e);
		}
	}
}

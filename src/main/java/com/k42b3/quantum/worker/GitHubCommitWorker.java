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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.k42b3.quantum.Message;
import com.k42b3.quantum.Queue;
import com.k42b3.quantum.Worker;

/**
 * Worker which gets the latest commits of an specific github project
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/quantum
 */
public class GitHubCommitWorker extends HttpWorkerAbstract
{
	public GitHubCommitWorker(Queue queue, Worker worker)
	{
		super(queue, worker);
	}

	@Override
	protected void fetch(HttpClient httpClient)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");

			String url = "https://api.github.com/repos/" + worker.getParams().get("owner") + "/" + worker.getParams().get("repo") + "/commits";
			if(worker.getLastRequest() != null)
			{
				url = url + "?since=" + sdf.format(worker.getLastRequest());
			}

			HttpGet httpGet = new HttpGet(url);
			if(worker.getLastRequest() != null)
			{
				httpGet.addHeader("If-Modified-Since", DateUtils.formatDate(worker.getLastRequest()));
			}

			logger.info("Request " + url);
			
			HttpResponse response = httpClient.execute(httpGet);

			if(response.getStatusLine().getStatusCode() == 304)
			{
				return;
			}

			String json = EntityUtils.toString(response.getEntity());
			Gson gson = new Gson();
			
			Commits[] commits = gson.fromJson(json, Commits[].class);

			for(int i = 0; i < commits.length; i++)
			{
				try
				{
					Date date = sdf.parse(commits[i].getCommit().getAuthor().getDate());
					
					Message message = new Message();
					message.setMid(commits[i].getSha());
					message.setUrl(commits[i].getHtmlUrl());
					message.setMessage(commits[i].getCommit().getMessage());
					message.setProducer(worker.getParams().get("repo"));
					message.setDate(date);

					queue.push(this, message);
				}
				catch (ParseException e)
				{
					logger.error(e.getMessage(), e);
				}
			}
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

	@Override
	public Map<String, String> getParameters()
	{
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("owner", "Owner");
		parameters.put("repo", "Repository");

		return parameters;
	}
	
	class Commits
	{
		protected String sha;
		protected Commit commit;
		protected String html_url;
		
		public String getSha()
		{
			return sha;
		}
		
		public void setSha(String sha)
		{
			this.sha = sha;
		}
		
		public Commit getCommit()
		{
			return commit;
		}
		
		public void setCommit(Commit commit)
		{
			this.commit = commit;
		}
		
		public String getHtmlUrl()
		{
			return html_url;
		}
		
		public void setHtmlUrl(String htmlUrl)
		{
			this.html_url = htmlUrl;
		}
	}
	
	class Commit
	{
		protected Author author;
		protected String message;
		
		public Author getAuthor()
		{
			return author;
		}
		
		public void setAuthor(Author author)
		{
			this.author = author;
		}
		
		public String getMessage()
		{
			return message;
		}
		
		public void setMessage(String message)
		{
			this.message = message;
		}
	}
	
	class Author
	{
		protected String name;
		protected String email;
		protected String date;
		
		public String getName()
		{
			return name;
		}
		
		public void setName(String name)
		{
			this.name = name;
		}
		
		public String getEmail()
		{
			return email;
		}
		
		public void setEmail(String email)
		{
			this.email = email;
		}
		
		public String getDate()
		{
			return date;
		}
		
		public void setDate(String date)
		{
			this.date = date;
		}
	}
}

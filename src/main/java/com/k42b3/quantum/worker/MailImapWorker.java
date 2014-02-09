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
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.ReceivedDateTerm;

import com.k42b3.quantum.Message;
import com.k42b3.quantum.Queue;
import com.k42b3.quantum.Worker;
import com.k42b3.quantum.WorkerAbstract;

/**
 * Worker which gets the latest mail subjects from an imap server
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/quantum
 */
public class MailImapWorker extends WorkerAbstract
{
	public MailImapWorker(Queue queue, Worker worker)
	{
		super(queue, worker);
	}

	public void run()
	{
		try
		{
			Properties props = new Properties();
			//props.put("mail.debug", "true");

			Session session = Session.getInstance(props);
			int port = Integer.parseInt(worker.getParams().get("port"));
			String storeType = port == 993 ? "imaps" : "imap";
			Store store = session.getStore(storeType);
			
			logger.info("Request " + worker.getParams().get("host"));

			store.connect(worker.getParams().get("host"), port, worker.getParams().get("username"), worker.getParams().get("password"));

			Folder inbox;
			if(worker.getParams().get("folder") == null || worker.getParams().get("folder").isEmpty())
			{
				inbox = store.getFolder("Inbox");
			}
			else
			{
				inbox = store.getFolder(worker.getParams().get("folder"));
			}

			inbox.open(Folder.READ_ONLY);

			javax.mail.Message[] messages;
			if(worker.getLastRequest() != null)
			{
				messages = inbox.search(new ReceivedDateTerm(ComparisonTerm.GT, worker.getLastRequest()));
			}
			else
			{
				messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
			}

			for(int i = 0; i < messages.length; i++)
			{
				String mid = messages[i].getHeader("Message-ID").toString();
				if(mid == null || mid.isEmpty())
				{
					mid = "urn:" + worker.getParams().get("host") + ":" + messages[i].getMessageNumber();
				}

				Address[] addresses = messages[i].getFrom();
				StringBuilder from = new StringBuilder();
				StringBuilder producer = new StringBuilder();
				if(addresses != null && addresses.length >= 1)
				{
					from.append(addresses[0].toString());

					for(int j = 0; j < addresses.length; j++)
					{
						producer.append(addresses[j].toString());
					}
				}

				Message message = new Message();
				message.setMid(mid);
				message.setUrl("mailto://" + from.toString());
				message.setMessage(messages[i].getSubject());
				message.setDate(messages[i].getReceivedDate());
				message.setProducer(producer.toString());

				queue.push(this, message);
			}

			inbox.close(false);
			store.close();
		}
		catch (MessagingException e)
		{
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public Map<String, String> getParameters()
	{
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("host", "Host");
		parameters.put("port", "Port");
		parameters.put("username", "Username");
		parameters.put("password", "Password");
		parameters.put("folder", "Folder");

		return parameters;
	}
}

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

package com.k42b3.quantum.handler;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.k42b3.quantum.Container;

/**
 * HandlerAbstract
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/quantum
 */
abstract public class HandlerAbstract extends AbstractHandler
{
	protected Container container;
	
	protected Logger logger = Logger.getLogger("com.k42b3.quantum");
	
	public HandlerAbstract(Container container)
	{
		this.container = container;
	}
	
    protected void handleException(HttpServletResponse response, Exception e)
    {
		logger.error(e.getMessage(), e);

		try
		{
			StatusResponse status = new StatusResponse(e.getMessage(), false);

	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print(container.getGson().toJson(status));
		}
		catch (IOException ex)
		{
			logger.error(e.getMessage(), e);
		}
    }

    protected String readRequestBody(HttpServletRequest request) throws IOException
    {
		StringBuilder json = new StringBuilder();
		BufferedReader reader = request.getReader();
		String line = null;

		while((line = reader.readLine()) != null)
		{
			json.append(line);
		}

		logger.info("Received: " + json.toString());

		return json.toString();
    }

    class StatusResponse
    {
    	protected String message;
    	protected boolean success;
    	
    	public StatusResponse(String message, boolean success)
    	{
    		this.message = message;
    		this.success = success;
    	}

		public String getMessage()
		{
			return message;
		}

		public void setMessage(String message)
		{
			this.message = message;
		}

		public boolean isSuccess()
		{
			return success;
		}

		public void setSuccess(boolean success)
		{
			this.success = success;
		}
    }
}

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

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.utils.DateUtils;
import org.eclipse.jetty.server.Request;

import com.k42b3.quantum.Container;
import com.k42b3.quantum.Message;

/**
 * MessageHandler
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/quantum
 */
public class MessageHandler extends HandlerAbstract
{
	public MessageHandler(Container container)
	{
		super(container);
	}

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
    	if(request.getMethod().equals("GET"))
    	{
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json;charset=utf-8");
			baseRequest.setHandled(true);

    		try
			{
    			String modifiedSince = request.getHeader("If-Modified-Since");
    			Date date = null;

    			if(modifiedSince != null && !modifiedSince.isEmpty())
    			{
    				date = DateUtils.parseDate(modifiedSince);
    			}

    			response.getWriter().print(container.getGson().toJson(container.getMessageRepository().getAll(date)));
			}
			catch(SQLException e)
			{
				this.handleException(response, e);
			}
    	}
		else if(request.getMethod().equals("POST")) 
    	{
    		Message message = container.getGson().fromJson(readRequestBody(request), Message.class);

    		container.getEventPublisher().publish(null, message);

            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().print(container.getGson().toJson(message));
    	}
    }
}


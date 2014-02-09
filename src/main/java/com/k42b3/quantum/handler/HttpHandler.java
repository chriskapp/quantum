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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.k42b3.quantum.Container;

/**
 * HttpHandler
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/quantum
 */
public class HttpHandler extends HandlerAbstract
{
	protected Container container;
	protected WorkerHandler workerHandler;
	protected MessageHandler messageHandler;
	protected AppHandler appHandler;

	public HttpHandler(Container container)
	{
		super(container);

		this.workerHandler = new WorkerHandler(container);
		this.messageHandler = new MessageHandler(container);
		this.appHandler = new AppHandler(container);
	}

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
    	if(request.getPathInfo().startsWith("/api/worker"))
    	{
    		workerHandler.handle(target, baseRequest, request, response);
    	}
    	else if(request.getPathInfo().startsWith("/api/message"))
    	{
    		messageHandler.handle(target, baseRequest, request, response);
    	}
    	else
    	{
    		appHandler.handle(target, baseRequest, request, response);
    	}
    }
}

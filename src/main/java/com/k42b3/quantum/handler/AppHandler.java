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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.k42b3.quantum.Container;

/**
 * AppHandler
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/quantum
 */
public class AppHandler extends HandlerAbstract
{
	public AppHandler(Container container)
	{
		super(container);
	}

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
		LinkedHashMap<String, String> routes = new LinkedHashMap<String, String>();
		routes.put("/common.css", "/common.css");
		routes.put("/common.js", "/common.js");
		routes.put("/worker", "/worker.htm");
		routes.put("/", "/index.htm");

		Iterator<Entry<String, String>> it = routes.entrySet().iterator();
		String templateFile = null;

		while(it.hasNext())
		{
			Entry<String, String> route = it.next();
			
			if(request.getPathInfo().startsWith(route.getKey()))
			{
				templateFile = route.getValue();
				break;
			}
		}

		if(templateFile != null)
		{
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType(this.getContentType(templateFile));
			baseRequest.setHandled(true);

			InputStream is = this.getClass().getResourceAsStream(templateFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			String line = null;
			while((line = reader.readLine()) != null)
			{
				response.getWriter().println(line);
			}
		}
    }
    
    protected String getContentType(String fileName)
    {
		if(fileName.endsWith(".css"))
		{
			return "text/css;charset=utf-8";
		}
		else if(fileName.endsWith(".js"))
		{
			return "text/javascript;charset=utf-8";
		}
		else
		{
			return "text/html;charset=utf-8";
		}
    }
}

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

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;

/**
 * Entry
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/quantum
 */
public class Entry
{
	public static void main(String[] args) throws Exception
	{
		// logging
		Layout layout = new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN);

		Logger.getLogger("com.k42b3.quantum").setLevel(Level.ALL);
		Logger.getLogger("com.k42b3.quantum").addAppender(new WriterAppender(layout, System.out));

		// options
		Options options = new Options();
		options.addOption("p", false, "Port for the web server default is 8080");
		options.addOption("i", false, "The interval how often each worker gets triggered in minutes default is 2");

		CommandLineParser parser = new BasicParser();
		CommandLine cmd = parser.parse(options, args);

		// start app
		Quantum app = new Quantum();
		
		if(cmd.hasOption("p"))
		{
			try
			{
				int port = Integer.parseInt(cmd.getOptionValue("p"));

				app.setPort(port);
			}
			catch(NumberFormatException e)
			{
				Logger.getLogger("com.k42b3.quantum").info("Port must be an integer");
			}
		}
		
		if(cmd.hasOption("i"))
		{
			try
			{
				int pollInterval = Integer.parseInt(cmd.getOptionValue("i"));

				app.setPollInterval(pollInterval);
			}
			catch(NumberFormatException e)
			{
				Logger.getLogger("com.k42b3.quantum").info("Interval must be an integer");
			}
		}

		app.run();
	}
}

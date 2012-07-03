/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.protocol.ws.glassfish3;

import java.io.IOException;

import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.util.lang.Args;
import org.glassfish.grizzly.websockets.WebSocket;

/**
 * @since 6.0
 */
public class GlassfishWebSocketConnection implements IWebSocketConnection
{
	private final WebSocket connection;

	/**
	 * Constructor
	 *
	 * @param connection
	 *      the native web socket connection
	 */
	public GlassfishWebSocketConnection(final WebSocket connection)
	{
		this.connection = Args.notNull(connection, "connection");
	}

	@Override
	public boolean isOpen()
	{
		return connection.isConnected();
	}

	@Override
	public void close(int code, String message)
	{
		if (isOpen())
		{
			connection.close(code, message);
		}
	}

	@Override
	public IWebSocketConnection sendMessage(String message) throws IOException
	{
		checkClosed();

		connection.send(message);
		return this;
	}

	@Override
	public IWebSocketConnection sendMessage(byte[] message, int offset, int length) throws IOException
	{
		checkClosed();

		connection.send(message);
		return this;
	}

	private void checkClosed()
	{
		if (!isOpen())
		{
			throw new IllegalStateException("The connection is closed.");
		}
	}
}

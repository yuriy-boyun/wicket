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
package org.apache.wicket.protocol.http;

import org.apache.wicket.Application;
import org.apache.wicket.protocol.ws.glassfish3.GlassfishWebSocketProcessor;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.ProtocolHandler;
import org.glassfish.grizzly.websockets.Version;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketApplication;
import org.glassfish.grizzly.websockets.WebSocketEngine;
import org.glassfish.grizzly.websockets.WebSocketListener;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @since
 */
public class Glassfish3WebSocketFilter extends AbstractUpgradeFilter
{
	private final GlassfishWebSocketApplication webSocketApplication = new GlassfishWebSocketApplication();

	@Override
	public void init(boolean isServlet, FilterConfig filterConfig) throws ServletException
	{
		super.init(isServlet, filterConfig);

		WebSocketEngine.getEngine().register(webSocketApplication);
	}

	@Override
	public void destroy()
	{
		WebSocketEngine.getEngine().unregisterAll();

		super.destroy();
	}

	protected boolean acceptWebSocket(HttpServletRequest req, HttpServletResponse resp, Application application)
			throws ServletException, IOException
	{
		if (!super.acceptWebSocket(req, resp, application))
		{
			return false;
		}

		GlassfishWebSocketProcessor webSocketHandle = new GlassfishWebSocketProcessor(req, application);
		ProtocolHandler handler = Version.DRAFT17.createHandler(false);
		GlassfishWebSocketProcessor.GlassfishWebSocket webSocket = webSocketHandle.new GlassfishWebSocket(handler);

		webSocketApplication.add(webSocket);
		webSocket.onConnect();
		return true;
	}

	private static class GlassfishWebSocketApplication extends WebSocketApplication
	{
		@Override
		public WebSocket createSocket(ProtocolHandler handler, WebSocketListener... listeners) {
			return null;
		}

		@Override
		public boolean add(WebSocket socket)
		{
			return super.add(socket);
		}

		@Override
		public boolean isApplicationRequest(HttpRequestPacket request) {
			return true;
		}

		@Override
		public void onConnect(WebSocket socket) {
			super.onConnect(socket);
		}
	}
}

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

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Application;
import org.apache.wicket.protocol.ws.api.AbstractWebSocketProcessor;
import org.glassfish.grizzly.websockets.DataFrame;
import org.glassfish.grizzly.websockets.DefaultWebSocket;
import org.glassfish.grizzly.websockets.ProtocolHandler;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketListener;
import org.glassfish.grizzly.websockets.draft06.ClosingFrame;

/**
 * @since 0.1
 */
public class GlassfishWebSocketProcessor extends AbstractWebSocketProcessor
{
	public class GlassfishWebSocket extends DefaultWebSocket
	{
        public GlassfishWebSocket(ProtocolHandler protocolHandler, WebSocketListener... listeners) {
            super(protocolHandler, listeners);
        }

        @Override
		public void onClose(DataFrame frame)
		{
			int code;
			String reason;
			if (frame instanceof ClosingFrame)
			{
				ClosingFrame closingFrame = (ClosingFrame) frame;
				code = closingFrame.getCode();
				reason = closingFrame.getReason();
			}
			else
			{
				code = 0;
				reason = frame.getTextPayload();
			}
			
			GlassfishWebSocketProcessor.this.onClose(code, reason);
		}

		@Override
		public void onConnect()
		{
			super.onConnect();
			GlassfishWebSocketProcessor.this.onOpen(this);
		}

		@Override
		public void onMessage(String message)
		{
			GlassfishWebSocketProcessor.this.onMessage(message);
		}

		@Override
		public void onMessage(byte[] message)
		{
			GlassfishWebSocketProcessor.this.onMessage(message, 0, message.length);
		}
	}

	public GlassfishWebSocketProcessor(final HttpServletRequest request, Application application)
	{
		super(request, application);
	}

	@Override
	public void onOpen(Object connection)
	{
		if (!(connection instanceof WebSocket))
		{
			throw new IllegalArgumentException(GlassfishWebSocketProcessor.class.getName() + " can work only with " + WebSocket.class.getName());
		}
		onConnect(new GlassfishWebSocketConnection((WebSocket) connection));
	}
}

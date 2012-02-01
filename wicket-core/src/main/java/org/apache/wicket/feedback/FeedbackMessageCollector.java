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
package org.apache.wicket.feedback;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

/**
 * Collects feedback messages from all the places where they can be stored.
 * 
 * @author igor
 */
public final class FeedbackMessageCollector
{
	private final Component root;
	private boolean includeSession = true;
	private boolean recursive = true;

	public FeedbackMessageCollector(Component root)
	{
		this.root = root;
	}

	public FeedbackMessageCollector setIncludeSession(boolean value)
	{
		includeSession = value;
		return this;
	}

	public FeedbackMessageCollector setRecursive(boolean value)
	{
		recursive = value;
		return this;
	}

	public List<FeedbackMessage> collect()
	{
		return collect(IFeedbackMessageFilter.ALL);
	}

	public List<FeedbackMessage> collect(final IFeedbackMessageFilter filter)
	{
		final List<FeedbackMessage> messages = new ArrayList<FeedbackMessage>();

		if (includeSession)
		{
			messages.addAll(root.getSession().getFeedbackMessages().messages(filter));
		}

		if (root.hasFeedbackMessage())
		{
			messages.addAll(root.getFeedbackMessages().messages(filter));
		}

		if (recursive && root instanceof MarkupContainer)
		{
			((MarkupContainer)root).visitChildren(new IVisitor<Component, Void>()
			{

				@Override
				public void component(Component object, IVisit<Void> visit)
				{
					if (object.hasFeedbackMessage())
					{
						messages.addAll(object.getFeedbackMessages().messages(filter));
					}
				}
			});
		}

		return messages;
	}


}

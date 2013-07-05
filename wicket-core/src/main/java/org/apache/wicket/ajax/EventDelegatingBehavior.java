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
package org.apache.wicket.ajax;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.ajax.json.JsonFunction;
import org.apache.wicket.util.template.PackageTextTemplate;

/**
 * A behavior that collects the Ajax Request attributes for all AjaxEventBehaviors
 * on the same event in the children components of the host component.
 * This way there is only one JavaScript event binding and all the Ajax call listeners are preserved.
 */
public class EventDelegatingBehavior extends AjaxEventBehavior
{
	private Map<String, CharSequence> attrsMap;

	private boolean initialized = false;

	/**
	 * Construct.
	 *
	 * @param event the event this behavior will be attached to
	 */
	public EventDelegatingBehavior(String event)
	{
		super(event);
	}

	@Override
	public void onConfigure(Component component)
	{
		super.onConfigure(component);

		if (initialized == false)
		{
			Page page = component.getPage();
			HashSet<String> enabledEvents = page.getMetaData(EVENT_NAME_PAGE_KEY);
			if (enabledEvents == null)
			{
				enabledEvents = new HashSet<>();
				page.setMetaData(EVENT_NAME_PAGE_KEY, enabledEvents);
			}
			enabledEvents.add(getEvent());
			initialized = true;
		}
	}

	/**
	 * Collects the Ajax Request Attributes for a AjaxEventBehavior in a child component
	 * @param componentId
	 * @param attributes
	 */
	protected final void contributeComponentAttributes(String componentId, CharSequence attributes)
	{
		if (attrsMap == null)
		{
			attrsMap = new HashMap<>();
		}
		attrsMap.put(componentId, new JsonFunction(attributes));
	}

	@Override
	public void detach(Component component)
	{
		super.detach(component);
		attrsMap = null;
	}

	@Override
	protected CharSequence getCallbackScript(Component component)
	{
		PackageTextTemplate template = new PackageTextTemplate(EventDelegatingBehavior.class, "res/js/EventDelegatingBehavior.js.tmpl");

		Map<String, Object> variables = new HashMap<>();
		variables.put("componentMarkupId", component.getMarkupId());
		variables.put("event", getEvent());

		JSONObject attributesMap = new JSONObject(attrsMap);
		try
		{
			variables.put("attributesMap", attributesMap.toString(2));
		} catch (JSONException e)
		{
			e.printStackTrace();
		}

		return template.asString(variables);
	}

	@Override
	protected final void onEvent(AjaxRequestTarget target)
	{
		throw new UnsupportedOperationException("Executed the event delegating behavior should never happen");
	}
}

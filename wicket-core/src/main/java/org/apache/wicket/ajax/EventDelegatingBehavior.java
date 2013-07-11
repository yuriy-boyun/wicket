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
import java.util.List;
import java.util.Map;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.ajax.json.JsonFunction;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * A behavior that collects the Ajax Request attributes for all AjaxEventBehaviors
 * on the same event in the children components of the host component.
 * This way there is only one JavaScript event binding and all the Ajax call listeners
 * for the delegated behaviors are preserved.
 *
 * <strong>Note</strong>: this behavior doesn't support custom Ajax request attributes.
 * It executes the ones (if any) of the delegated behavior. The only attributes that
 * are taken into account are {@link org.apache.wicket.ajax.attributes.AjaxRequestAttributes#preventDefault}
 * and {@link org.apache.wicket.ajax.attributes.AjaxRequestAttributes#eventPropagation}
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
	protected void onBind()
	{
		super.onBind();

		getComponent().getApplication().getAjaxSettings().setUseEventDelegation(true);
	}

	@Override
	public void onConfigure(Component component)
	{
		super.onConfigure(component);

		if (initialized == false)
		{
			Page page = component.getPage();
			HashMap<String, Integer> enabledEvents = page.getMetaData(EVENT_NAME_PAGE_KEY);
			if (enabledEvents == null)
			{
				enabledEvents = new HashMap<>();
				page.setMetaData(EVENT_NAME_PAGE_KEY, enabledEvents);
				enabledEvents.put(getEvent(), 1);
			}
			else
			{
				Integer count = enabledEvents.get(getEvent());
				enabledEvents.put(getEvent(), count + 1);
			}

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
	public void onRemove(Component component)
	{
		super.onRemove(component);

		Page page = component.getPage();
		HashMap<String, Integer> enabledEvents = page.getMetaData(EVENT_NAME_PAGE_KEY);
		String event = getEvent();
		Integer count = enabledEvents.remove(event);
		if (count > 1)
		{
			enabledEvents.put(event, count - 1);
		}
		else if (enabledEvents.isEmpty())
		{
			page.setMetaData(EVENT_NAME_PAGE_KEY, null);
		}
	}

	@Override
	protected CharSequence getCallbackScript(Component component)
	{
		return String.format("Wicket.Event.delegate('%s', '%s', %s, %s)",
				component.getMarkupId(), getEvent(), renderAjaxAttributes(component), formatAttributesMap(component));
	}

	private String formatAttributesMap(Component component)
	{
		JSONObject attributesMap = new JSONObject(attrsMap);

		String attributesMapAsString;

		if (component.getApplication().usesDevelopmentConfig())
		{
			try
			{
				attributesMapAsString = attributesMap.toString(2);
			}
			catch (JSONException ignored)
			{
				attributesMapAsString = attributesMap.toString();
			}
		}
		else
		{
			attributesMapAsString = attributesMap.toString();
		}

		return attributesMapAsString;
	}

	@Override
	public void renderHead(Component component, IHeaderResponse response)
	{
		super.renderHead(component, response);

		response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(EventDelegatingBehavior.class, "res/js/EventDelegatingBehavior.js")
		{
			@Override
			public List<HeaderItem> getDependencies()
			{
				List<HeaderItem> dependencies = super.getDependencies();
				dependencies.add(JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getWicketEventReference()));
				return dependencies;
			}
		}));
	}

	@Override
	protected final void onEvent(AjaxRequestTarget target)
	{
		throw new UnsupportedOperationException("Executed the event delegating behavior should never happen");
	}

}

package org.apache.wicket.settings.def;

import org.apache.wicket.settings.IAjaxSettings;

/**
 *
 * @since 7.0
 */
public class AjaxSettings implements IAjaxSettings
{
	private boolean useEventDelegation = false;

	@Override
	public void setUseEventDelegation(boolean useEventDelegation)
	{
		this.useEventDelegation = useEventDelegation;
	}

	@Override
	public boolean isUseEventDelegation()
	{
		return useEventDelegation;
	}
}

package bp.event;

import java.util.List;
import java.util.function.Consumer;

import bp.ui.util.UIUtil;

public class BPEventChannelUI extends BPEventChannelBase
{
	protected boolean triggerEventListeners(List<Consumer<? extends BPEvent>> ls, BPEvent event)
	{
		boolean rc = false;
		if (event.triggerLater)
		{
			UIUtil.laterUI(() ->
			{
				super.triggerEventListenersInner(ls, event);
			});
		}
		else
		{
			UIUtil.inUI(() ->
			{
				super.triggerEventListenersInner(ls, event);
			});
			rc = event.stopDefault;
		}
		return rc;
	}
}

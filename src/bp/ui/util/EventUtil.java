package bp.ui.util;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import bp.BPGUICore;
import bp.res.BPResource;
import bp.ui.event.BPEventUIResourceOperation;
import bp.ui.tree.BPPathTreePanel.BPEventUIPathTree;

public class EventUtil
{
	public static abstract class EventConsumer implements Consumer<ActionEvent>
	{
		protected Object m_obj;
		protected int m_channelid;

		public EventConsumer(Object params, int channelid)
		{
			m_obj = params;
			m_channelid = channelid;
		}
	}

	public static class EventConsumerNodeAction extends EventConsumer
	{
		protected String m_action;

		public EventConsumerNodeAction(Object params, int channelid, String action)
		{
			super(params, channelid);
			m_action = action;
		}

		public void accept(ActionEvent t)
		{
			BPGUICore.EVENTS_UI.trigger(m_channelid, new BPEventUIPathTree(BPEventUIPathTree.NODE_ACTION, new Object[] { m_obj, m_action }));
		}
	}

	public static class EventConsumerMakePathTreeAction extends EventConsumer
	{
		protected String m_action;

		public EventConsumerMakePathTreeAction(Object params, int channelid, String action)
		{
			super(params, channelid);
			m_action = action;
		}

		public void accept(ActionEvent t)
		{
			BPGUICore.EVENTS_UI.trigger(m_channelid, BPEventUIPathTree.makeActionEvent(m_action, (BPResource) m_obj));
		}
	}

	public static class EventConsumerResourceOPAction extends EventConsumer
	{
		protected String m_action;

		public EventConsumerResourceOPAction(Object params, int channelid, String action)
		{
			super(params, channelid);
			m_action = action;
		}

		public void accept(ActionEvent t)
		{
			BPGUICore.EVENTS_UI.trigger(m_channelid, new BPEventUIResourceOperation(BPEventUIResourceOperation.RES_ACTION, new Object[] { m_obj, m_action }, UIUtil.getRouteContext(t.getSource())));
		}
	}
}

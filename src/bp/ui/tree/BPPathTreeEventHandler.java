package bp.ui.tree;

import java.lang.ref.WeakReference;
import java.util.function.Consumer;

import bp.BPGUICore;
import bp.event.BPEventChannelUI;
import bp.ui.tree.BPPathTreePanel.BPEventUIPathTree;

public class BPPathTreeEventHandler implements Consumer<BPEventUIPathTree>
{
	protected int m_chid;
	protected WeakReference<BPTreeComponent<?>> m_pref;

	public BPPathTreeEventHandler(BPTreeComponent<?> p)
	{
		BPEventChannelUI channelui = new BPEventChannelUI();
		m_chid = BPGUICore.EVENTS_UI.addChannel(channelui);
	}

	public void accept(BPEventUIPathTree t)
	{

	}

	public int getChannelID()
	{
		return m_chid;
	}

	public void clearResource()
	{
		BPGUICore.EVENTS_UI.removeChannel(m_chid);
	}
}
package bp.ui.parallel;

import java.util.function.Consumer;

import bp.BPGUICore;
import bp.event.BPEventUI;
import bp.util.LogicUtil.WeakRefGo;

public class BPSyncGUIControllerBase implements BPSyncGUIController
{
	protected Consumer<BPEventUISyncEditor> m_synccb;
	protected volatile boolean m_onsync;
	protected volatile boolean m_blocksync;
	protected int m_channelid;
	protected WeakRefGo<Consumer<BPEventUISyncEditor>> m_cbref;

	public BPSyncGUIControllerBase(Consumer<BPEventUISyncEditor> e)
	{
		m_cbref = new WeakRefGo<Consumer<BPEventUISyncEditor>>(e);
	}

	public void setCallback(Consumer<BPEventUISyncEditor> e)
	{
		m_cbref.setTarget(e);
	}

	public void setChannelID(int channelid)
	{
		m_channelid = channelid;
	}

	public void startSync()
	{
		if (m_synccb != null)
			stopSync();
		m_synccb = this::onSync;
		BPGUICore.EVENTS_UI.on(m_channelid, BPEventUISyncEditor.EVENTKEY_SYNC_EDITOR, m_synccb);
		m_onsync = true;
	}

	public void stopSync()
	{
		m_onsync = false;
		if (m_synccb != null)
			BPGUICore.EVENTS_UI.off(m_channelid, BPEventUISyncEditor.EVENTKEY_SYNC_EDITOR, m_synccb);
		m_synccb = null;
	}

	protected void onSync(BPEventUISyncEditor e)
	{
		m_cbref.run(c -> c.accept(e));
	}

	public boolean checkSync()
	{
		return m_onsync;
	}

	public boolean checkSyncAndNoBlock()
	{
		return m_onsync && !m_blocksync;
	}

	public void blockSync(Runnable seg)
	{
		m_blocksync = true;
		try
		{
			seg.run();
		}
		finally
		{
			m_blocksync = false;
		}
	}

	public void trigger(BPEventUI e)
	{
		BPGUICore.EVENTS_UI.trigger(m_channelid, e);
	}

	public void clearResource()
	{
		m_cbref.setTarget(null);
		stopSync();
	}
}

package bp.ui.editor.controller;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import bp.ui.editor.BPEditor;
import bp.ui.parallel.BPEventUISyncEditor;
import bp.ui.parallel.BPSyncGUIController;
import bp.ui.parallel.BPSyncGUIControllerBase;

public class BPEditorController
{
	public BPEditorEventController eventcontroller;

	public BPSyncGUIController syncstatus;
	public BPSyncGUIController syncaction;

	public Consumer<BPEventUISyncEditor> syncstatuscb;
	public Consumer<BPEventUISyncEditor> syncactioncb;

	protected BiConsumer<BPEventUISyncEditor, ? extends BPEditor<?>> m_syncstatuscbinner;
	protected BiConsumer<BPEventUISyncEditor, ? extends BPEditor<?>> m_syncactioncbinner;

	public Consumer<? extends BPEditor<?>> startsynccb;
	public Consumer<? extends BPEditor<?>> stopsynccb;

	protected BPEditor<?> m_editor;

	protected boolean m_needsaveeditable;
	protected boolean m_needsave;

	public BPEditorController(BPEditor<?> editor)
	{
		m_editor = editor;
		eventcontroller = new BPEditorEventController.BPEditorEventControllerBase(editor);
	}

	public boolean canSync()
	{
		return syncstatus != null;
	}

	public void initStatusSync(BiConsumer<BPEventUISyncEditor, ? extends BPEditor<?>> cb)
	{
		m_syncstatuscbinner = cb;
		syncstatuscb = this::onSyncStatusL2;
		syncstatus = new BPSyncGUIControllerBase(syncstatuscb);
	}

	public void initActionSync(BiConsumer<BPEventUISyncEditor, ? extends BPEditor<?>> cb)
	{
		m_syncactioncbinner = cb;
		syncactioncb = this::onSyncActionL2;
		syncaction = new BPSyncGUIControllerBase(syncactioncb);
	}

	@SuppressWarnings("unchecked")
	protected void onSyncStatusL2(BPEventUISyncEditor e)
	{
		((BiConsumer<BPEventUISyncEditor, BPEditor<?>>) m_syncstatuscbinner).accept(e, m_editor);
	}

	@SuppressWarnings("unchecked")
	protected void onSyncActionL2(BPEventUISyncEditor e)
	{
		((BiConsumer<BPEventUISyncEditor, BPEditor<?>>) m_syncactioncbinner).accept(e, m_editor);
	}

	public void initStatusSync(Consumer<BPEventUISyncEditor> cb)
	{
		syncstatuscb = cb;
		syncstatus = new BPSyncGUIControllerBase(syncstatuscb);
	}

	public void initActionSync(Consumer<BPEventUISyncEditor> cb)
	{
		syncactioncb = cb;
		syncaction = new BPSyncGUIControllerBase(syncactioncb);
	}

	public void setChannelID(int channelid)
	{
		if (syncstatus != null)
			syncstatus.setChannelID(channelid);
		if (syncaction != null)
			syncaction.setChannelID(channelid);
	}

	public void clearResource()
	{
		stopSync();
		if (syncaction != null)
			syncaction.clearResource();
		if (syncstatus != null)
			syncstatus.clearResource();
		syncstatuscb = null;
		syncactioncb = null;
		m_syncstatuscbinner = null;
		m_syncactioncbinner = null;
	}

	@SuppressWarnings("unchecked")
	public void startSync()
	{
		if (syncstatus != null)
		{
			syncstatus.startSync();
			if (syncaction != null)
				syncaction.startSync();
			if (startsynccb != null)
				((Consumer<BPEditor<?>>) startsynccb).accept(m_editor);
		}
	}

	@SuppressWarnings("unchecked")
	public void startSyncStatus()
	{
		if (syncstatus != null)
		{
			syncstatus.startSync();
			if (startsynccb != null)
				((Consumer<BPEditor<?>>) startsynccb).accept(m_editor);
		}
	}

	@SuppressWarnings("unchecked")
	public void stopSync()
	{
		if (syncaction != null)
		{
			syncaction.stopSync();
			if (syncstatus != null)
				syncstatus.stopSync();
			if (stopsynccb != null)
				((Consumer<BPEditor<?>>) stopsynccb).accept(m_editor);
		}
	}

	@SuppressWarnings("unchecked")
	public void stopSyncStatus()
	{
		if (syncstatus != null)
		{
			syncstatus.stopSync();
			if (stopsynccb != null)
				((Consumer<BPEditor<?>>) stopsynccb).accept(m_editor);
		}
	}
	
	public void setNeedSaveEditable(boolean flag)
	{
		m_needsaveeditable=flag;
	}

	public void setNeedSave(boolean flag)
	{
		if (m_needsaveeditable)
			m_needsave = flag;
	}

	public boolean isNeedSave()
	{
		return m_needsave;
	}
}

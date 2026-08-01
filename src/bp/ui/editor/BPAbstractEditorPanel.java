package bp.ui.editor;

import java.lang.ref.WeakReference;
import java.util.function.BiConsumer;

import javax.swing.JPanel;

import bp.ui.editor.controller.BPEditorController;

@SuppressWarnings("serial")
public abstract class BPAbstractEditorPanel extends JPanel implements BPEditor<JPanel>
{
	protected String m_id;
	protected int m_channelid;
	protected BPEditorController m_ec;

	protected WeakReference<BiConsumer<String, Boolean>> m_statehandler;

	public BPAbstractEditorPanel()
	{
		m_ec = new BPEditorController(this);
		m_ec.setNeedSaveEditable(!isNoSave());
	}

	public JPanel getComponent()
	{
		return this;
	}

	public void focusEditor()
	{
		requestFocus();
	}

	public void setID(String id)
	{
		m_id = id;
	}

	public String getID()
	{
		return m_id;
	}

	public void setChannelID(int channelid)
	{
		m_channelid = channelid;
		m_ec.setChannelID(channelid);
	}

	public int getChannelID()
	{
		return m_channelid;
	}

	public BPEditorController getEditorController()
	{
		return m_ec;
	}

	protected void changeNeedSave(boolean flag)
	{
		m_ec.setNeedSave(flag);
		dispatchStateChanged();
	}

	public void setOnStateChanged(BiConsumer<String, Boolean> handler)
	{
		m_statehandler = new WeakReference<BiConsumer<String, Boolean>>(handler);
	}

	public void dispatchStateChanged()
	{
		WeakReference<BiConsumer<String, Boolean>> ref = m_statehandler;
		if (ref != null)
		{
			BiConsumer<String, Boolean> handler = ref.get();
			if (handler != null)
			{
				handler.accept(m_id, m_ec.isNeedSave());
			}
		}
	}
}

package bp.ui.editor;

import javax.swing.JPanel;

import bp.ui.editor.controller.BPEditorController;

@SuppressWarnings("serial")
public abstract class BPAbstractEditorPanel extends JPanel implements BPEditor<JPanel>
{
	protected boolean m_needsave;
	protected String m_id;
	protected int m_channelid;
	protected BPEditorController m_ec;

	public BPAbstractEditorPanel()
	{
		m_ec = new BPEditorController(this);
	}

	public JPanel getComponent()
	{
		return this;
	}

	public void focusEditor()
	{
		requestFocus();
	}

	public boolean needSave()
	{
		return m_needsave;
	}

	public void setNeedSave(boolean needsave)
	{
		m_needsave = needsave;
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
	}

	public int getChannelID()
	{
		return m_channelid;
	}

	public BPEditorController getEditorController()
	{
		return m_ec;
	}
}

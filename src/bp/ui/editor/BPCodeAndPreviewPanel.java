package bp.ui.editor;

public abstract class BPCodeAndPreviewPanel<PREVIEWCOMP> extends BPCodePanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7234821768148211733L;

	protected boolean m_ispmode;

	protected void setTextContainerValue(String text)
	{
		if (m_ispmode)
			setPreviewValue(text);
		else
			super.setTextContainerValue(text);
	}

	protected void setPreviewValue(String text)
	{
		setPreviewData(transPreviewData(text, false));
	}

	protected void initPreview()
	{
		setPreviewData(getInitPreviewData());
	}

	public void setPreviewOnlyMode()
	{
		m_txt.setVisible(false);
		m_ispmode = true;
	}

	public abstract PREVIEWCOMP getPreviewComponent();

	protected abstract Object getInitPreviewData();

	protected abstract Object transPreviewData(String text, boolean errout);

	public abstract void setPreviewData(Object data);
}

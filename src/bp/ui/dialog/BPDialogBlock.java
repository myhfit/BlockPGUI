package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import bp.ui.editor.BPTextPanel;
import bp.ui.scomp.BPLabel;
import bp.ui.util.UIUtil;

public class BPDialogBlock<T> extends BPDialogCommon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3046472082428859928L;

	protected BPLabel m_lbltext;
	protected volatile T m_result;
	protected volatile RuntimeException m_err;
	protected volatile boolean m_notcloseonerr;
	protected volatile boolean m_notcloseoncomplete;

	public BPDialogBlock(Supplier<CompletionStage<T>> callback)
	{
		this(callback, true, true);
	}

	public BPDialogBlock(Supplier<CompletionStage<T>> callback, boolean closeoncomplete, boolean closeonerr)
	{
		super();
		callback.get().whenComplete(this::onComplete);
		m_notcloseoncomplete = !closeoncomplete;
		m_notcloseonerr = !closeonerr;
	}

	public void setText(String text)
	{
		m_lbltext.setText(text);
		pack();
		setLocationRelativeTo(getParent());
	}

	public void refreshText(String text)
	{
		m_lbltext.setText(text);
	}

	public void setResult(String result, boolean iserr)
	{
		BPTextPanel resultp = new BPTextPanel();
		resultp.getTextPanel().setEditable(false);
		resultp.getTextPanel().setMonoFont();
		resultp.getTextPanel().setText(result);
		getContentPane().removeAll();
		getContentPane().add(resultp);
		resultp.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(400, 300)));
		setTitle(iserr ? "Error" : "Completed");
		setCommandBarMode(COMMANDBAR_OKENTER);
		pack();
		setLocationRelativeTo(getParent());
	}

	protected void onComplete(T data, Throwable err)
	{
		m_result = data;
		if (err != null)
		{
			m_err = err instanceof RuntimeException ? (RuntimeException) err : new RuntimeException(err);
			if (m_notcloseonerr)
			{
				setResult(err.getStackTrace().toString(), true);
				return;
			}
		}
		else if (m_notcloseoncomplete)
		{
			setResult(data == null ? "Completed" : data.toString(), false);
			return;
		}
		SwingUtilities.invokeLater(() -> dispose());
	}

	public T getResult()
	{
		return m_result;
	}

	public RuntimeException getError()
	{
		return m_err;
	}

	public boolean doCallCommonAction(int command)
	{
		return false;
	}

	protected void initUIComponents()
	{
		m_lbltext = new BPLabel("Waiting");
		m_lbltext.setBorder(new EmptyBorder(10, 10, 10, 10));
		m_lbltext.setLabelFont();
		m_lbltext.setHorizontalAlignment(BPLabel.CENTER);
		setLayout(new BorderLayout());
		getContentPane().add(m_lbltext, BorderLayout.CENTER);
		setMinimumSize(UIUtil.scaleUIDimension(new Dimension(300, 40)));
		setModal(true);
	}

	protected void initDatas()
	{
	}
}

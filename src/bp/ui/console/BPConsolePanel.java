package bp.ui.console;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import bp.console.BPConsole;
import bp.ui.editor.BPCodePanel;
import bp.ui.scomp.BPConsolePane;

public class BPConsolePanel extends BPCodePanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2242830294327033326L;

	protected void init()
	{
		m_scroll = new JScrollPane();
		m_scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		initActions();
		setLayout(new BorderLayout());
		add(m_scroll, BorderLayout.CENTER);
		initListeners();
	}

	public void setTextPane(BPConsolePane cp)
	{
		m_txt = cp;
		m_txt.setOnPosChanged(this::onPosChanged);
		m_scroll.setViewportView(cp);
		m_scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		initListeners();
	}

	public String getEditorName()
	{
		return ((BPConsolePane) m_txt).getConsole().getName();
	}

	public BPConsole getConsole()
	{
		return ((BPConsolePane) m_txt).getConsole();
	}
}

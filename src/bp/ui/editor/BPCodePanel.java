package bp.ui.editor;

import javax.swing.JPanel;

import bp.data.BPTextContainer;
import bp.ui.scomp.BPCodePane;
import bp.ui.scomp.BPTextPane;
import bp.ui.util.UIUtil;

public class BPCodePanel extends BPTextPanel implements BPCodeEditor<JPanel, BPTextContainer>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 789055056771931789L;

	protected BPTextPane createTextPane()
	{
		BPTextPane rc= new BPCodePane();
		UIUtil.createLinePanel(rc, m_scroll);
		return rc;
	}
}

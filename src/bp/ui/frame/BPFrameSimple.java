package bp.ui.frame;

import java.awt.BorderLayout;
import java.awt.Component;

public class BPFrameSimple extends BPFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2676043594451260643L;

	protected void initUIComponents()
	{
	}

	protected void initDatas()
	{
	}

	public final static BPFrameSimple createWithComponent(Component c)
	{
		BPFrameSimple rc = new BPFrameSimple();
		rc.setLayout(new BorderLayout());
		rc.getContentPane().add(c, BorderLayout.CENTER);
		return rc;
	}
}

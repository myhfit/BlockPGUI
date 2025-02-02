package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.lang.ref.WeakReference;
import java.util.function.Function;

public class BPDialogSimple extends BPDialogCommon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6248519898715122294L;

	protected WeakReference<Function<Integer, Boolean>> m_cb;

	public void setCommandCallback(Function<Integer, Boolean> callback)
	{
		m_cb = new WeakReference<Function<Integer, Boolean>>(callback);
	}

	public boolean doCallCommonAction(int command)
	{
		if (m_cb != null && m_cb.get() != null)
			return m_cb.get().apply(command);
		return false;
	}

	protected void initUIComponents()
	{
	}

	protected void initDatas()
	{
	}

	public final static int showComponent(Component c, int cmdbarmode, Function<Integer, Boolean> cmdcallback, String title, Component parent)
	{
		BPDialogSimple dlg = createWithComponent(c, cmdbarmode, cmdcallback);
		dlg.setTitle(title);
		dlg.pack();
		dlg.setLocationRelativeTo(parent);
		dlg.setModal(true);
		dlg.setVisible(true);
		return dlg.getActionResult();
	}

	public final static BPDialogSimple createWithComponent(Component c, int cmdbarmode, Function<Integer, Boolean> cmdcallback)
	{
		BPDialogSimple dlg = new BPDialogSimple();
		dlg.setLayout(new BorderLayout());
		dlg.setCommandCallback(cmdcallback);
		dlg.setCommandBarMode(cmdbarmode);
		dlg.getContentPane().add(c, BorderLayout.CENTER);
		return dlg;
	}
}

package bp.ui.console;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import bp.console.BPConsole;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.editor.BPCodePanel;
import bp.ui.parallel.BPEventUISyncEditor;
import bp.ui.parallel.BPSyncGUIController;
import bp.ui.parallel.BPSyncGUIControllerBase;
import bp.ui.scomp.BPConsolePane;
import bp.ui.util.UIUtil;

public class BPConsolePanel extends BPCodePanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2242830294327033326L;
	protected Consumer<String> m_onruncmd;
	protected BPSyncGUIController m_syncactobj;

	public final static String SYNCACTIONNAME_CONSOLE_RUNCMD = "CONSOLE_RUNCMD";
	public final static String SYNCACTIONNAME_CONSOLE_CLEAR = "CONSOLE_CLEAR";

	public BPConsolePanel()
	{
	}

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

	protected void initListeners()
	{
		super.initListeners();
		m_onruncmd = this::onRunCommand;
		if (m_txt != null && m_txt instanceof BPConsolePane)
		{
			((BPConsolePane) m_txt).addOnEnterSync(m_onruncmd);
		}
		if (m_syncactobj == null)
			m_syncactobj = new BPSyncGUIControllerBase(m_synccb);
	}

	public String getEditorName()
	{
		return ((BPConsolePane) m_txt).getConsole().getName();
	}

	public BPConsole getConsole()
	{
		return ((BPConsolePane) m_txt).getConsole();
	}

	public void setChannelID(int channelid)
	{
		super.setChannelID(channelid);
		if (m_syncactobj != null)
			m_syncactobj.setChannelID(channelid);
	}

	protected List<Action> makeActionsAtPos(int pos)
	{
		if (pos == 0)
		{
			List<Action> rc = new ArrayList<Action>();
			Action actclear = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUCLEAR, this::onClear);
			rc.add(actclear);
			return rc;
		}
		return null;
	}

	protected void onRunCommand(String cmd)
	{
		if (m_syncactobj.checkSyncAndNoBlock())
			m_syncactobj.trigger(BPEventUISyncEditor.syncAction(getID(), SYNCACTIONNAME_CONSOLE_RUNCMD, cmd));
	}

	protected void onClear(ActionEvent e)
	{
		((BPConsolePane) m_txt).runClear();
		if (m_syncactobj.checkSyncAndNoBlock())
			m_syncactobj.trigger(BPEventUISyncEditor.syncAction(getID(), SYNCACTIONNAME_CONSOLE_CLEAR));
	}

	public BPSyncGUIController getSyncActionController()
	{
		return m_syncactobj;
	}
	
	protected int getScrollYPos()
	{
		return UIUtil.getScrollBarPosCheckMax(m_scroll.getVerticalScrollBar());
	}

	protected void onSyncEditor(BPEventUISyncEditor e)
	{
		boolean dealed = false;
		if (BPEventUISyncEditor.SYNC_ACTION.equals(e.subkey) && !m_txt.getID().equals(e.datas[0]))
		{
			String actionname = (String) e.datas[1];
			if (SYNCACTIONNAME_CONSOLE_RUNCMD.equals(actionname))
			{
				String cmd = (String) ((Object[]) e.datas[2])[0];
				if (cmd != null && cmd.length() > 0)
				{
					m_syncobj.blockSync(() -> m_syncactobj.blockSync(() -> ((BPConsolePane) m_txt).runCommandFromOutside(cmd)));
				}
				dealed = true;
			}
			else if (SYNCACTIONNAME_CONSOLE_CLEAR.equals(actionname))
			{
				m_syncobj.blockSync(() -> m_syncactobj.blockSync(() -> ((BPConsolePane) m_txt).runClear()));
				dealed = true;
			}
		}
		if (!dealed)
		{
			super.onSyncEditor(e);
		}
	}
}
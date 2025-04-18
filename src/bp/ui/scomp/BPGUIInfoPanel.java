package bp.ui.scomp;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIUtil;

public class BPGUIInfoPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8042041062193934411L;

	protected BPLabel m_lbleditor;
	protected BPLabel m_lbleditord;
	protected BPLabel m_pgvm;
	protected Timer m_timer;

	public BPGUIInfoPanel(boolean needvminfo)
	{
		m_lbleditor = new BPLabel();
		m_lbleditord = new BPLabel();
		m_lbleditor.setLabelFont();
		m_lbleditord.setLabelFont();
		m_lbleditord.setBorder(new EmptyBorder(0, 2, 0, 2));
		m_lbleditor.setBorder(new EmptyBorder(0, 2, 0, 2));
		setLayout(new BorderLayout());

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(m_lbleditord, BorderLayout.WEST);
		p.add(m_lbleditor, BorderLayout.EAST);
		add(p, BorderLayout.CENTER);

		if (needvminfo && UIConfigs.SHOW_VMINFO() && checkModule())
		{
			m_pgvm = new BPLabel();
			m_pgvm.setBorder(new MatteBorder(0, 1, 0, 0, UIConfigs.COLOR_WEAKBORDER()));
			m_pgvm.setLabelFont();
			m_pgvm.addMouseListener(new UIUtil.BPMouseListener(null, this::onGC, null, null, null));
			add(m_pgvm, BorderLayout.EAST);

			onTimer(null);
			m_timer = new Timer(1000, this::onTimer);
			m_timer.start();
		}
	}

	protected boolean checkModule()
	{
		try
		{
			ManagementFactory.getMemoryMXBean();
			return true;
		}
		catch (Error e2)
		{
		}
		return false;
	}

	protected void onGC(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
			ManagementFactory.getMemoryMXBean().gc();
		else if (e.getButton() == MouseEvent.BUTTON3)
		{
			CommonUIOperations.showSystemInfo();
		}
	}

	protected void onTimer(ActionEvent e)
	{
		if (m_pgvm != null)
		{
			MemoryUsage usage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
			int total = (int) (usage.getUsed() / 1048576f);
			int max = (int) (usage.getMax() / 1048576f);
			m_pgvm.setText(" " + total + "M/" + max + "M ");
		}
	}

	public void setEditorInfo(String info)
	{
		m_lbleditor.setText(info);
	}

	public void setEditorDynamicInfo(String info)
	{
		m_lbleditord.setText(info);
	}

	public void clearResources()
	{
		if (m_timer != null)
		{
			m_timer.stop();
			m_timer = null;
		}
	}
}

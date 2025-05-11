package bp.ui.scomp;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.ref.WeakReference;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import bp.config.UIConfigs;

public class BPSplitPane extends JSplitPane implements ComponentListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5909545530118758296L;

	protected int m_lastpos = 0;
	protected int m_mode = 0;
	protected int m_lastdsize = 0;
	protected int m_reservedsize = 0;
	protected WeakReference<ResizeFunction> m_resizefunc = null;

	public BPSplitPane(int newOrientation)
	{
		super(newOrientation);
		setDividerSize(UIConfigs.DIVIDER_SIZE());
		setContinuousLayout(true);
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.CTRL_DOWN_MASK), "none");
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), "none");
		addComponentListener(this);
	}

	public void setWeakDividerBorder()
	{
		SplitPaneUI spui = getUI();
		int ori = getOrientation();
		if (spui != null && spui instanceof BasicSplitPaneUI)
		{
			BasicSplitPaneDivider d = ((BasicSplitPaneUI) ui).getDivider();
			if (d != null)
				d.setBorder(new MatteBorder(0, ori == HORIZONTAL_SPLIT ? 1 : 0, ori == HORIZONTAL_SPLIT ? 0 : 1, 0, UIConfigs.COLOR_WEAKBORDER()));
		}
	}

	public void setDividerBorderColor(Color color, boolean first)
	{
		SplitPaneUI spui = getUI();
		int ori = getOrientation();
		if (spui != null && spui instanceof BasicSplitPaneUI)
		{
			BasicSplitPaneDivider d = ((BasicSplitPaneUI) ui).getDivider();
			if (d != null)
				d.setBorder(new MatteBorder(first ? (ori == HORIZONTAL_SPLIT ? 0 : 1) : 0, first ? ori == HORIZONTAL_SPLIT ? 1 : 0 : 0, first ? 0 : (ori == HORIZONTAL_SPLIT ? 0 : 1), first ? 0 : ori == HORIZONTAL_SPLIT ? 1 : 0, color));
		}
	}

	public void setDividerBorderColor(Color c1, Color c2)
	{
		SplitPaneUI spui = getUI();
		int ori = getOrientation();
		if (spui != null && spui instanceof BasicSplitPaneUI)
		{
			BasicSplitPaneDivider d = ((BasicSplitPaneUI) ui).getDivider();
			if (d != null)
				d.setBorder(new CompoundBorder(new MatteBorder(ori == HORIZONTAL_SPLIT ? 0 : 1, ori == HORIZONTAL_SPLIT ? 1 : 0, 0, 0, c1), new MatteBorder(0, 0, ori == HORIZONTAL_SPLIT ? 0 : 1, ori == HORIZONTAL_SPLIT ? 1 : 0, c2)));
		}
	}

	public void setReservedSize(int v)
	{
		m_reservedsize = v;
	}

	public int getToggleState()
	{
		return m_mode;
	}

	public void togglePanel(boolean left)
	{
		if (left)
		{
			if (m_mode == 0)
			{
				m_lastpos = getDividerLocation();
				setDividerLocation(0);
				m_mode = 1;
				m_lastdsize = getDividerSize();
				setDividerSize(0);
			}
			else
			{
				setDividerLocation(m_lastpos);
				m_lastpos = 0;
				m_mode = 0;
				setDividerSize(m_lastdsize);
				m_lastdsize = 0;
			}
		}
		else
		{
			if (m_mode == 0)
			{
				m_lastpos = getDividerLocation();
				setDividerLocation((getOrientation() == VERTICAL_SPLIT ? getHeight() : getWidth()) - getDividerSize() - m_reservedsize);
				m_mode = 2;
				m_lastdsize = getDividerSize();
				if (m_reservedsize == 0)
					setDividerSize(0);
			}
			else
			{
				setDividerLocation(m_lastpos);
				m_lastpos = 0;
				m_mode = 0;
				setDividerSize(m_lastdsize);
				m_lastdsize = 0;
			}
		}
	}

	public static interface ResizeFunction
	{
		int resize(BPSplitPane parent, int resizemode, Component left, Component right);
	}

	public void setResizeFunc(ResizeFunction resizefunc)
	{
		m_resizefunc = new WeakReference<ResizeFunction>(resizefunc);
	}

	public void componentResized(ComponentEvent e)
	{
		WeakReference<ResizeFunction> resizefuncref = m_resizefunc;
		if (resizefuncref != null)
		{
			ResizeFunction resizefunc = resizefuncref.get();
			if (resizefunc != null)
			{
				int pos = resizefunc.resize(this, m_mode, leftComponent, rightComponent);
				if (pos > -1)
				{
					setDividerLocation(pos);
					validate();
					invalidate();
					return;
				}
			}
		}
		if (m_mode == 2)
		{
			setDividerLocation(getHeight() - getDividerSize() - m_reservedsize);
			validate();
			invalidate();
		}
	}

	public void componentMoved(ComponentEvent e)
	{
	}

	public void componentShown(ComponentEvent e)
	{
	}

	public void componentHidden(ComponentEvent e)
	{
	}
}

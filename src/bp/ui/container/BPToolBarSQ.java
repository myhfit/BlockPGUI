package bp.ui.container;

import java.awt.Container;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.ui.BPComponent;
import bp.ui.actions.BPAction;
import bp.ui.scomp.BPToolSQButton;
import bp.ui.scomp.BPToolVIconButton;

public class BPToolBarSQ extends JPanel implements BPToolBar<JPanel>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8081373457393970685L;

	protected int m_barheight;

	protected static boolean m_isvertical;

	protected boolean m_btnsetsize;
	protected int m_btnsetsizedelta;

	protected Map<String, BPComponent<?>> m_compmap = new HashMap<String, BPComponent<?>>();

	protected boolean m_btnborder = false;

	public BPToolBarSQ()
	{
		this(false);
	}

	public BPToolBarSQ(boolean isvertical)
	{
		m_barheight = isvertical ? UIConfigs.BAR_HEIGHT_VICON() : UIConfigs.BAR_HEIGHT_VERTICAL();
		m_btnsetsize = true;
		m_btnsetsizedelta = 0;
		setDirection(isvertical);
	}

	public void setDirection(boolean isvertical)
	{
		m_isvertical = isvertical;
		if (isvertical)
		{
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		}
		else
		{
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		}
		setBarHeight(m_barheight);
	}

	public void setHasButtonBorder(boolean flag)
	{
		m_btnborder = flag;
	}

	public void setButtonSizePolicy(boolean setbtnsize, int delta)
	{
		m_btnsetsize = setbtnsize;
		m_btnsetsizedelta = delta;
	}

	public void setBarHeight(int barheight)
	{
		m_barheight = barheight;
		if (m_isvertical)
		{
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setMinimumSize(new Dimension((int) (barheight * UIConfigs.UI_SCALE()), 0));
			setPreferredSize(new Dimension((int) (barheight * UIConfigs.UI_SCALE()), 2000));
		}
		else
		{
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			setMinimumSize(new Dimension(0, (int) (barheight * UIConfigs.UI_SCALE())));
			setPreferredSize(new Dimension(4000, (int) (barheight * UIConfigs.UI_SCALE())));
		}
	}

	public int getBarHeight()
	{
		return m_barheight;
	}

	public JPanel getComponent()
	{
		return this;
	}

	public Map<String, BPComponent<?>> getComponentMap()
	{
		return m_compmap;
	}

	public Container getRealContainer()
	{
		return this;
	}

	public void setActions(Action[] actions)
	{
		setActions(actions, null);
	}

	public void setActions(Action[] actions, JComponent accparent)
	{
		clearResource();
		for (Action act : actions)
		{
			if (act != null)
			{
				if (act.getValue("VICON") != null)
				{
					BPToolVIconButton btn = (accparent == null ? new BPToolVIconButton(act) : new BPToolVIconButton(act, accparent));
					if (m_btnsetsize)
						btn.setButtonSize(m_barheight + m_btnsetsizedelta);
					setButtonBorder(btn, m_btnborder);
					add(btn);
				}
				else if (Boolean.TRUE.equals(act.getValue(BPAction.IS_SEPARATOR)))
				{
					addSeparator();
				}
				else if (act.getValue(Action.SMALL_ICON) != null)
				{
					BPToolSQButton btn = new BPToolSQButton(act);
					setButtonBorder(btn, m_btnborder);
					add(btn);
				}
				else
				{
					BPToolSQButton btn = new BPToolSQButton((String) act.getValue(Action.NAME), act);
					setButtonBorder(btn, m_btnborder);
					add(btn);
				}
			}
			else
			{
				addGlue();
			}
		}
	}

	public final static void setButtonBorder(JComponent btn, boolean flag)
	{
		if (flag)
		{
			btn.setBorder(new CompoundBorder(new MatteBorder(1, 1, 1, 1, UIConfigs.COLOR_STRONGBORDER()), new EmptyBorder(4, 4, 4, 4)));
		}
		else
		{
			btn.setBorder(null);
		}
	}

	public void addSeparator()
	{
		add(Box.createRigidArea(new Dimension(4, 4)));
	}

	public void addGlue()
	{
		add(Box.createGlue());
	}
}
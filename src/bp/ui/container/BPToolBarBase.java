package bp.ui.container;

import java.awt.Container;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JToolBar;

import bp.ui.BPComponent;

public class BPToolBarBase extends JToolBar implements BPToolBar<JToolBar>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8081373457393970685L;
	
	protected Map<String, BPComponent<?>> m_compmap = new HashMap<String, BPComponent<?>>();
	
	public BPToolBarBase()
	{
		setFloatable(false);
	}

	public JToolBar getComponent()
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
		clearResource();
		for(Action act:actions)
		{
			add(act);
		}
	}
}

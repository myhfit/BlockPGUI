package bp.ui.container;

import java.awt.Container;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import bp.ui.BPComponent;

public class BPPanelContainerBase extends JPanel implements BPContainer<JPanel>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 371085295739588074L;

	protected Map<String, BPComponent<?>> m_compmap = new HashMap<String, BPComponent<?>>();

	public BPComponentType getComponentType()
	{
		return BPComponentType.PANEL;
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
}

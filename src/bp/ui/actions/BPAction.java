package bp.ui.actions;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import bp.ui.res.icon.BPVIcon;

public class BPAction extends AbstractAction
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8358538446474229611L;

	public final static String SUB_ACTIONS = "SubActions";
	public final static String IS_SEPARATOR = "IsSeparator";

	protected Consumer<ActionEvent> m_cb;

	public BPAction(String name)
	{
		super(name);
	}

	public void setConsumer(Consumer<ActionEvent> callback)
	{
		m_cb = callback;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (m_cb != null)
		{
			m_cb.accept(e);
		}
	}

	public final static BPActionBuilder build(String name)
	{
		BPActionBuilder builder = new BPActionBuilder(new BPAction(name));
		return builder;
	}

	public final static BPAction separator()
	{
		return new BPActionBuilder(new BPAction("")).separator().getAction();
	}

	public final static class BPActionBuilder
	{
		private BPAction action;

		public BPActionBuilder(BPAction action)
		{
			this.action = action;
		}

		public BPActionBuilder name(String _name)
		{
			action.putValue(Action.NAME, _name);
			return this;
		}

		public BPActionBuilder smallIcon(Icon _icon)
		{
			action.putValue(Action.SMALL_ICON, _icon);
			return this;
		}

		public BPActionBuilder vIcon(BPVIcon _icon)
		{
			action.putValue("VICON", _icon);
			return this;
		}

		public BPActionBuilder largeIcon(Icon _icon)
		{
			action.putValue(Action.LARGE_ICON_KEY, _icon);
			return this;
		}

		public BPActionBuilder callback(Consumer<ActionEvent> cb)
		{
			action.setConsumer(cb);
			return this;
		}

		public BPActionBuilder mnemonicKey(int key)
		{
			action.putValue(Action.MNEMONIC_KEY, key);
			return this;
		}

		public BPActionBuilder acceleratorKey(KeyStroke ks)
		{
			action.putValue(Action.ACCELERATOR_KEY, ks);
			return this;
		}

		public BPActionBuilder tooltip(String tooltip)
		{
			action.putValue(Action.SHORT_DESCRIPTION, tooltip);
			return this;
		}

		public BPActionBuilder separator()
		{
			action.putValue(IS_SEPARATOR, true);
			return this;
		}

		public BPAction getAction()
		{
			return action;
		}
	}

}

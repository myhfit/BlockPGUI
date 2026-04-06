package bp.ui.actions;

import static bp.ui.actions.BPActionConst.BPActionVerb.ACCKEY;
import static bp.ui.actions.BPActionConst.BPActionVerb.MNEKEY;
import static bp.ui.actions.BPActionConst.BPActionVerb.NAME;
import static bp.ui.actions.BPActionConst.BPActionVerb.TOOLTIP;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.KeyStroke;

import bp.env.BPEnvActions;
import bp.env.BPEnvManager;
import bp.locale.BPLocaleHelperBase;
import bp.ui.actions.BPAction.BPActionBuilder;
import bp.ui.actions.BPActionConst.BPActionVerb;
import bp.ui.res.icon.BPVIcon;

public abstract class BPActionHelperBase<C extends BPActionConst> extends BPLocaleHelperBase<C, BPActionVerb> implements BPActionHelper<C>
{
	public BPActionHelperBase()
	{
	}

	@SuppressWarnings("unchecked")
	public BPVIcon vIcon(BPActionConst act)
	{
		ensureInit();
		Supplier<BPVIcon> cb = (Supplier<BPVIcon>) m_actps.get(act.viconK());
		return cb != null ? cb.get() : null;
	}

	protected String getOverwriteValue(C act, BPActionVerb verb)
	{
		return BPEnvManager.getEnvValue(BPEnvActions.ENV_NAME_ACTIONS, m_packname + "." + act.name() + "." + (verb == null ? "NAME" : verb.name()));
	}
	
	public String getCurrentLocale()
	{
		String l = BPEnvManager.getEnvValue(BPEnvActions.ENV_NAME_ACTIONS, "locale");
		return (l == null || l.trim().length() == 0) ? super.getCurrentLocale() : l;
	}

	public void putAction(Map<Integer, Object> actmap, C act, String name, String tooltip, Supplier<BPVIcon> vicon, String acckey, String mnekey)
	{
		actmap.put(act.nameK(), name);
		actmap.put(act.tooltipK(), tooltip);
		actmap.put(act.viconK(), vicon);
		actmap.put(act.acckeyK(), acckey);
		actmap.put(act.mnukeyK(), mnekey);
	}

	public BPAction makeAction(C act, Consumer<ActionEvent> cb, Consumer<BPActionBuilder> postfix)
	{
		ensureInit();
		String name = v(act, NAME);
		String tooltip = v(act, TOOLTIP);
		BPVIcon vicon = vIcon(act);
		String acckey = v(act, ACCKEY);
		String mnekey = v(act, MNEKEY);
		BPAction rc = makeAction(name, tooltip, vicon, acckey, mnekey, cb, postfix);
		fixName(act, rc, name, acckey, mnekey);
		return rc;
	}

	public BPAction makeActionWithAlias(C act, C alias, Consumer<ActionEvent> cb, Consumer<BPActionBuilder> postfix)
	{
		ensureInit();
		String name = v(act, alias, NAME);
		String tooltip = v(act, alias, TOOLTIP);
		BPVIcon vicon = vIcon(act);
		String acckey = v(act, alias, ACCKEY);
		String mnekey = v(act, alias, MNEKEY);
		BPAction rc = makeAction(name, tooltip, vicon, acckey, mnekey, cb, postfix);
		fixName(act, rc, name, acckey, mnekey);
		return rc;
	}

	protected void fixName(BPActionConst key, BPAction act, String name, String acckey, String nmekey)
	{
		if (nmekey != null && key.name().contains("_MNU"))
		{
			if (!name.toLowerCase().contains(nmekey.toLowerCase()))
			{
				if (name.endsWith("..."))
					act.putValue(BPAction.NAME, name.substring(0, name.length() - 3) + "(" + nmekey + ")...");
				else
					act.putValue(BPAction.NAME, name + "(" + nmekey + ")");
			}
		}
	}

	protected BPAction makeAction(String name, String tooltip, BPVIcon vicon, String acckey, String mnekey, Consumer<ActionEvent> cb, Consumer<BPActionBuilder> postfix)
	{
		BPActionBuilder b = BPAction.build(name);
		if (tooltip != null)
			b.tooltip(tooltip);
		if (vicon != null)
			b.vIcon(vicon);
		if (acckey != null)
			b.acceleratorKey(getKeyStroke(acckey));
		if (mnekey != null)
			b.mnemonicKey(getMnemonicKey(mnekey));
		if (cb != null)
			b.callback(cb);
		if (postfix != null)
			postfix.accept(b);
		return b.getAction();
	}

	protected KeyStroke getKeyStroke(String acckey)
	{
		return KeyStroke.getKeyStroke(acckey);
	}

	protected int getMnemonicKey(String mnekey)
	{
		return KeyEvent.getExtendedKeyCodeForChar(mnekey.charAt(0));
	}

	protected Class<BPActionVerb> getVerbClass()
	{
		return BPActionVerb.class;
	}
}

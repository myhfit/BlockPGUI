package bp.ui.actions;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import bp.locale.BPLocaleHelpers;
import bp.ui.actions.BPAction.BPActionBuilder;
import bp.ui.actions.BPActionConst.BPActionVerb;

public class BPActionHelpers
{
	public final static BPAction getAction(BPActionConst key, Consumer<ActionEvent> cb)
	{
		return getAction(key, cb, null);
	}

	public final static BPAction getAction(BPActionConst key, Consumer<ActionEvent> cb, Consumer<BPActionBuilder> postfix)
	{
		BPActionHelper<BPActionConst> helper = BPLocaleHelpers.getHelper(key.getPackName());
		if (helper != null)
			return helper.makeAction(key, cb, postfix);
		return null;
	}

	public final static BPAction getActionWithAlias(BPActionConst key, BPActionConst alias, Consumer<ActionEvent> cb)
	{
		return getActionWithAlias(key, alias, cb, null);
	}

	public final static BPAction getActionWithAlias(BPActionConst key, BPActionConst alias, Consumer<ActionEvent> cb, Consumer<BPActionBuilder> postfix)
	{
		BPActionHelper<BPActionConst> helper = BPLocaleHelpers.getHelper(key.getPackName());
		if (helper != null)
			return helper.makeActionWithAlias(key, alias, cb, postfix);
		return null;
	}

	@SuppressWarnings("unchecked")
	public final static <T> T getValue(BPActionConst key, BPActionConst alias, BPActionVerb verb)
	{
		T rc = null;
		verb = verb != null ? verb : BPActionVerb.NAME;
		if (alias != null)
		{
			BPActionHelper<BPActionConst> helper = BPLocaleHelpers.getHelper(alias.getPackName());
			if (helper != null)
				rc = (T) helper.v(alias, null, verb);

		}
		if (rc == null)
		{
			BPActionHelper<BPActionConst> helper = BPLocaleHelpers.getHelper(key.getPackName());
			if (helper != null)
				rc = (T) helper.v(key, null, verb);
		}
		return rc;
	}
}
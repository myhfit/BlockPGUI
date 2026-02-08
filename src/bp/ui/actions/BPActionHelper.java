package bp.ui.actions;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import bp.locale.BPLocaleHelper;
import bp.ui.actions.BPAction.BPActionBuilder;
import bp.ui.actions.BPActionConst.BPActionVerb;

public interface BPActionHelper<C extends BPActionConst> extends BPLocaleHelper<C, BPActionVerb>
{
	default BPAction makeAction(C key, Consumer<ActionEvent> cb)
	{
		return makeAction(key, cb, null);
	}

	BPAction makeAction(C key, Consumer<ActionEvent> cb, Consumer<BPActionBuilder> postfix);

	default BPAction makeActionWithAlias(C key, C alias, Consumer<ActionEvent> cb)
	{
		return makeActionWithAlias(key, alias, cb, null);
	}

	BPAction makeActionWithAlias(C key, C alias, Consumer<ActionEvent> cb, Consumer<BPActionBuilder> postfix);
}

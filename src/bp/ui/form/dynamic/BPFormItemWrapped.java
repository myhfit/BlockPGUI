package bp.ui.form.dynamic;

import java.awt.Component;

public abstract class BPFormItemWrapped<C extends Component, ST extends Component> extends BPFormItemBase<C>
{
	public ST stcomp;

	public Component getSTComponent()
	{
		return stcomp;
	}
}

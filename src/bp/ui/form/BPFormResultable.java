package bp.ui.form;

import java.awt.Component;

public interface BPFormResultable<C extends Component> extends BPForm<C>
{
	<T> T getResult();
}

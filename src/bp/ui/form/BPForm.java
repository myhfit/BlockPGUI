package bp.ui.form;

import java.awt.Component;
import java.util.Map;

import bp.ui.BPComponent;

public interface BPForm<C extends Component> extends BPComponent<C>
{
	boolean validateForm();

	Map<String, Object> getFormData();

	default void showData(Map<String, ?> data)
	{
		showData(data, true);
	}

	void showData(Map<String, ?> data, boolean editable);
}

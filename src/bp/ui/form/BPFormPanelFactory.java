package bp.ui.form;

import java.util.function.BiConsumer;

public interface BPFormPanelFactory
{
	void register(BiConsumer<String, Class<? extends BPFormPanel>> regfunc);
}

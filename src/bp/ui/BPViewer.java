package bp.ui;

import bp.data.BPDataContainer;
import bp.res.BPResource;

public interface BPViewer<C extends BPDataContainer>
{
	default void bind(C con)
	{
		bind(con, false);
	}

	void bind(C con, boolean noread);

	default void rebind(C con)
	{
		bind(con, true);
	}

	void unbind();

	C getDataContainer();

	default String[] getViewerFormat()
	{
		return null;
	}

	default <T> T getViewerData(String part, String format)
	{
		return null;
	}

	default BPResource tryGetResource()
	{
		BPDataContainer dc = getDataContainer();
		return dc != null ? dc.getResource() : null;
	}
}

package bp.ui;

import bp.data.BPDataContainer;

public interface BPViewer<C extends BPDataContainer>
{
	default void bind(C con)
	{
		bind(con, false);
	}

	void bind(C con, boolean noread);

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
}

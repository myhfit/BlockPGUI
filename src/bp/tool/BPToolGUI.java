package bp.tool;

import java.awt.Component;

public interface BPToolGUI extends BPTool
{
	void showTool(Object... params);

	Component createToolGroup(BPToolGUIContainer container, Object... params);

	default boolean canInput(Class<?> cls)
	{
		return false;
	}

	public static interface BPToolGUIContainer
	{
		void setCloseCallback(Runnable cb);
	}
}

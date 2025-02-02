package bp.ui.editor;

import java.awt.Component;

import bp.data.BPTextContainer;
import bp.ui.BPViewer;
import bp.ui.scomp.BPTextPane;

public interface BPTextEditor<C extends Component,CON extends BPTextContainer> extends BPEditor<C>, BPViewer<CON>
{
	default BPComponentType getComponentType()
	{
		return BPComponentType.TEXTEDITOR;
	}

	BPTextPane getTextPanel();

	void loadText(String text);
	
	default boolean needActiveOnStart()
	{
		return true;
	}
}

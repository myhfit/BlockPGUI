package bp.ui.editor;

import java.awt.Component;

import bp.data.BPTextContainer;

public interface BPCodeEditor<C extends Component, CON extends BPTextContainer> extends BPTextEditor<C, CON>
{
	default BPComponentType getComponentType()
	{
		return BPComponentType.CODEEDITOR;
	}
}

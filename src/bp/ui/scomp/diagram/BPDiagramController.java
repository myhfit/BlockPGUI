package bp.ui.scomp.diagram;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import bp.ui.scomp.BPDiagramComponent;

public interface BPDiagramController extends MouseListener, MouseMotionListener, MouseWheelListener
{
	void setDiagramComponent(BPDiagramComponent comp);

	void clearState();

	default void initCursor()
	{
	}
}

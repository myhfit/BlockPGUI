package bp.ui.scomp.diagram;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import bp.data.BPDiagram.BPDiagramElement;
import bp.ui.scomp.BPDiagramComponent;

public class BPDiagramControllerNavigation extends BPDiagramControllerBase
{
	public void mouseDragged(MouseEvent e)
	{
		onDragging(e.getX(), e.getY());
	}

	public void mousePressed(MouseEvent e)
	{
		BPDiagramComponent comp = m_compref.get();
		if (comp == null)
			return;
		comp.requestFocusInWindow();
		int btn = e.getButton();
		int x = e.getX();
		int y = e.getY();
		boolean isctrl = e.isControlDown();
		if (btn == MouseEvent.BUTTON1)
		{
			BPDiagramElement downele = comp.getElementFromPos(x, y);
			if (downele != null)
			{
				comp.selectElement(downele, isctrl, btn);
				startDrag(downele, x, y);
			}
		}
		else if (btn == MouseEvent.BUTTON3)
		{
			BPDiagramElement ele = comp.getElementFromPos(x, y);
			if (ele != null)
			{
				if (!ele.isSelected())
					comp.selectElement(ele, isctrl, btn);
				comp.showContextMenu(ele, x, y);
			}
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		endDrag(e.getX(), e.getY());
	}

	public void mouseExited(MouseEvent e)
	{
		endDrag(e.getX(), e.getY());
	}

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		sendZoom(e);
	}

	public void clearState()
	{
		BPDiagramComponent comp = m_compref.get();
		if (comp != null)
			comp.stopDrag();
	}
}
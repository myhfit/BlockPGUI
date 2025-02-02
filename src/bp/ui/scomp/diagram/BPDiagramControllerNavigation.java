package bp.ui.scomp.diagram;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import bp.data.BPDiagram.BPDiagramElement;

public class BPDiagramControllerNavigation extends BPDiagramControllerBase
{
	public void mouseDragged(MouseEvent e)
	{
		onDragging(e.getX(), e.getY());
	}

	public void mousePressed(MouseEvent e)
	{
		m_compref.run(dcomp -> dcomp.requestFocusInWindow());
		int btn = e.getButton();
		int x = e.getX();
		int y = e.getY();
		boolean isctrl = e.isControlDown();
		if (btn == MouseEvent.BUTTON1)
		{
			BPDiagramElement downele = m_compref.exec(comp ->
			{
				BPDiagramElement ele = comp.getElementFromPos(x, y);
				if (ele != null)
					comp.selectElement(ele, isctrl, btn);
				return ele;
			});
			startDrag(downele, x, y);
		}
		else if (btn == MouseEvent.BUTTON3)
		{
			m_compref.run(comp ->
			{
				BPDiagramElement ele = comp.getElementFromPos(x, y);
				if (ele != null)
				{
					if(!ele.isSelected())
						comp.selectElement(ele, isctrl, btn);
					comp.showContextMenu(ele, x, y);
				}
			});
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
		m_compref.run(comp -> comp.stopDrag());
	}
}

package bp.ui.scomp.diagram;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import bp.data.BPDiagram.BPDiagramElement;
import bp.data.BPDiagram.BPDiagramNode;
import bp.ui.scomp.BPDiagramComponent;
import bp.util.LogicUtil.WeakRefGo;

public abstract class BPDiagramControllerBase implements BPDiagramController
{
	protected WeakRefGo<BPDiagramComponent> m_compref;
	protected WeakRefGo<BPDiagramElement> m_downeleref = new WeakRefGo<BPDiagramElement>();

	protected volatile int[] m_dragpts = new int[6];
	protected volatile double[] m_dragelepts = new double[2];

	public BPDiagramControllerBase()
	{
		m_compref = new WeakRefGo<BPDiagramComponent>();
	}

	public void setDiagramComponent(BPDiagramComponent comp)
	{
		m_compref.setTarget(comp);
	}

	public void mouseDragged(MouseEvent e)
	{
	}

	public void mouseMoved(MouseEvent e)
	{
	}

	public void mouseClicked(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
		m_compref.run(dcomp -> dcomp.requestFocusInWindow());
	}

	public void initCursor()
	{
		m_compref.run(comp -> comp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)));
	}

	public void mouseReleased(MouseEvent e)
	{
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mouseWheelMoved(MouseWheelEvent e)
	{
	}

	protected void sendZoom(MouseWheelEvent e)
	{
		int c = e.getUnitsToScroll();
		if (c != 0)
		{
			m_compref.run(comp -> comp.scale(c > 0 ? -1 : 1));
		}
	}

	protected void startDrag(BPDiagramElement downele, int x, int y)
	{
		int[] dragpts = m_dragpts;
		if (dragpts == null)
		{
			dragpts = new int[6];
			m_dragpts = dragpts;
		}
		dragpts[0] = x;
		dragpts[1] = y;
		dragpts[4] = x;
		dragpts[5] = y;
		m_downeleref.setTarget(downele);
		if (downele != null)
		{
			if (downele.getElementType() == BPDiagramElement.ELEMENTTYPE_NODE)
			{
				BPDiagramNode n = (BPDiagramNode) downele;
				m_dragelepts[0] = n.x;
				m_dragelepts[1] = n.y;
			}
		}
	}

	protected void endDrag(int x, int y)
	{
		int[] dragpts = m_dragpts;
		if (dragpts != null)
		{
			// dragpts[2] = x;
			// dragpts[3] = y;
			m_compref.run(comp -> comp.stopDrag());
		}
		m_dragpts = null;
		m_downeleref.setTarget(null);
		m_dragelepts[0] = 0;
		m_dragelepts[1] = 0;
	}

	protected void onDragging(int x, int y)
	{
		int[] dragpts = m_dragpts;
		if (dragpts != null)
		{
			BPDiagramElement downele = m_downeleref.get();
			if (downele == null)
			{
				dragpts[2] = x;
				dragpts[3] = y;
				m_compref.run(comp -> comp.moveDiagram(dragpts));
				dragpts[4] = x;
				dragpts[5] = y;
			}
			else if (downele.getElementType() == BPDiagramElement.ELEMENTTYPE_NODE)
			{
				dragpts[2] = x;
				dragpts[3] = y;
				m_compref.run(comp -> comp.dragElement(downele, dragpts, m_dragelepts));
				dragpts[4] = x;
				dragpts[5] = y;
			}
		}
	}
}
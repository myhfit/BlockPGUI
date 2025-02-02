package bp.ui.scomp.diagram;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

import bp.data.BPDiagram;
import bp.data.BPDiagram.BPDiagramElement;
import bp.ui.scomp.BPComponentOverlay;

public class BPDiagramControllerRectangleSelect extends BPDiagramControllerBase
{
	protected BPComponentOverlayRectSel m_overlay;
	protected int[] m_drect;

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		sendZoom(e);
	}

	public void mouseDragged(MouseEvent e)
	{
		if (m_drect != null)
			dragRect(e.getX(), e.getY());
		else if (m_dragpts != null)
			onDragging(e.getX(), e.getY());
	}

	public void mousePressed(MouseEvent e)
	{
		m_compref.run(dcomp -> dcomp.requestFocusInWindow());
		int btn = e.getButton();
		int x = e.getX();
		int y = e.getY();
		if (btn == MouseEvent.BUTTON1)
			startRect(e.getX(), e.getY());
		else if (btn == MouseEvent.BUTTON3)
		{
			BPDiagramElement downele = m_compref.exec(comp ->
			{
				BPDiagramElement ele = comp.getElementFromPos(x, y);
				return ele;
			});
			startDrag(downele, e.getX(), e.getY());
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		int btn = e.getButton();
		if (btn == MouseEvent.BUTTON1)
			endRect(e.getX(), e.getY());
		else if (btn == MouseEvent.BUTTON3)
			endDrag(e.getX(), e.getY());
	}

	protected void dragRect(int x, int y)
	{
		int[] drect = m_drect;
		if (drect != null)
		{
			drect[2] = x - drect[0];
			drect[3] = y - drect[1];
			m_overlay.setRectangle(drect);
			m_compref.run(comp -> comp.refresh());
		}
	}

	protected void startRect(int x, int y)
	{
		m_drect = new int[] { x, y, 0, 0 };
		m_overlay = new BPComponentOverlayRectSel();
		m_overlay.setRectangle(m_drect);
		m_compref.run(comp -> comp.setComponentOverlay(m_overlay));
	}

	protected void endRect(int x, int y)
	{
		int[] drect = m_drect;
		int rx = drect[0];
		int ry = drect[1];
		int rw = drect[2];
		int rh = drect[3];
		if (rw < 0)
		{
			rx += rw;
			rw = 0 - rw;
		}
		if (rh < 0)
		{
			ry += rh;
			rh = 0 - rh;
		}

		final double dx1 = rx;
		final double dy1 = ry;
		final double dx2 = rx + rw;
		final double dy2 = ry + rh;

		double[][] drectdmodel = m_compref.exec(comp ->
		{
			double[] pt1 = comp.viewPt2ModelPt(new double[] { dx1, dy1 });
			double[] pt2 = comp.viewPt2ModelPt(new double[] { dx2, dy2 });
			return new double[][] { pt1, pt2 };
		});
		final double[] drectd = new double[] { drectdmodel[0][0], drectdmodel[0][1], drectdmodel[1][0] - drectdmodel[0][0], drectdmodel[1][1] - drectdmodel[0][1] };
		List<String> selkeys = new ArrayList<String>();
		BPDiagram d = m_compref.exec(comp -> comp.getDiagram());
		if (d != null)
		{
			d.eachElement((ele, layer) ->
			{
				if (ele.intersectRectangle(drectd))
					selkeys.add(ele.key);
			});
			m_compref.run(comp -> comp.getRawSelection().setKeys(selkeys));
		}
		clearState();
	}

	public void clearState()
	{
		m_drect = null;
		m_compref.run(comp ->
		{
			comp.setComponentOverlay(null);
			comp.refresh();
		});
		m_overlay = null;
	}

	public void initCursor()
	{
		m_compref.run(comp -> comp.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR)));
	}

	protected static class BPComponentOverlayRectSel implements BPComponentOverlay
	{
		protected volatile int[] m_rect;

		public void setRectangle(int[] rect)
		{
			m_rect = new int[] { rect[0], rect[1], rect[2], rect[3] };
		}

		public void draw(Graphics g, int x, int y, int w, int h, int offsetx, int offsety, double scale)
		{
			int[] rect = m_rect;
			int rx = (int) Math.round(scale * (rect[0] + offsetx));
			int ry = (int) Math.round(scale * (rect[1] + offsety));
			int rw = (int) Math.round(scale * (rect[2]));
			int rh = (int) Math.round(scale * (rect[3]));
			if (rw == 0 || rh == 0)
				return;
			if (rw < 0)
			{
				rx = rx + rw;
				rw = 0 - rw;
			}
			if (rh < 0)
			{
				ry = ry + rh;
				rh = 0 - rh;
			}
			g.drawRect(rx, ry, rw, rh);
		}
	}

}

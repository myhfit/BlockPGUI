package bp.ui.scomp;

import java.awt.Graphics;

public interface BPComponentOverlay
{
	void draw(Graphics g, int x, int y, int w, int h, int offsetx, int offsety, double scale);
}

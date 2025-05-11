package bp.ui.res.icon;

import java.awt.Graphics;

import bp.config.UIConfigs;

public interface BPVIcon
{
	default void draw(Graphics g, int x, int y, int w, int h, boolean issel)
	{
		if (issel)
			drawSelRect(g, x, y, w, h);
		doDraw(g, x, y, w, h);
	}

	default void drawSelRect(Graphics g, int x, int y, int w, int h)
	{
		g.setColor(UIConfigs.COLOR_STRONGBORDER());
		g.drawRect(x, y, w - 1, h - 1);
	}

	void doDraw(Graphics g, int x, int y, int w, int h);

	default void drawDisable(Graphics g, int x, int y, int w, int h)
	{
		g.drawLine(w - 1, x, y, h - 1);
		g.drawLine(w - 5, x, y, h - 5);
		g.drawLine(w - 1, x + 4, y + 4, h - 1);
	}
}

package bp.ui.scomp;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@SuppressWarnings("serial")
public class BPInputHotkey extends BPTextField implements KeyListener
{
	public BPInputHotkey()
	{
		setEditable(false);
		setFocusable(true);
		addKeyListener(this);
		setMonoFont();
	}

	public void keyTyped(KeyEvent e)
	{

	}

	public void keyPressed(KeyEvent e)
	{
		char c = (char) KeyEvent.getExtendedKeyCodeForChar(e.getKeyCode());
		String m = KeyEvent.getKeyModifiersText(e.getModifiers());
		setText((m != null && m.length() > 0 ? (m + "+") : "") + ((c == 0 || c == Character.MAX_VALUE) ? "" : c));
	}

	public void keyReleased(KeyEvent e)
	{
	}
}

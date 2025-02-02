package bp.ui.actions;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import bp.ui.frame.BPMainFrame;
import bp.ui.util.CommonUIOperations;

public class BPMainFrameActions
{
	public Action filenewfile;
	public Action filenewproject;
	public Action fileneweditor;
	public Action fileopen;
	public Action fileopenas;
	public Action fileopenfolder;
	public Action filesave;
	public Action filesaveas;
	public Action fileexit;
	public Action fileprop;
	public Action filecfgs;
	public Action filereloadcontext;

	public Action editundo;
	public Action editredo;
	public Action editcut;
	public Action editcopy;
	public Action editpaste;

	public Action viewtoggleleftpan;
	public Action viewtogglebottompan;
	public Action viewtogglerightpan;
	public Action viewfullscreen;

	public Action scswitchnexttab;
	public Action scswitchlasttab;
	public Action scclosecurrenttab;

	public Action sctoggleleftpanel;

	public Action navresource;
	public Action navprjitem;
	public Action naveditor;
	public Action navoverview;
	public Action navcmd;

	public Action helpsysinfo;

	protected BPMainFrame m_mf;

	public BPMainFrameActions(BPMainFrame mf)
	{
		m_mf = mf;
		filenewfile = BPAction.build("File...").callback((e) -> m_mf.showNewFile(null)).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK)).mnemonicKey(KeyEvent.VK_F).getAction();
		filenewproject = BPAction.build("Project...").callback((e) -> m_mf.showNewProject()).mnemonicKey(KeyEvent.VK_P).getAction();
		fileneweditor = BPAction.build("Editor...").callback((e) -> m_mf.showNewEditor()).mnemonicKey(KeyEvent.VK_E).getAction();
		fileopen = BPAction.build("Open File...").callback((e) -> m_mf.showOpenFile(true)).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK)).mnemonicKey(KeyEvent.VK_O).getAction();
		fileopenas = BPAction.build("Open File As...").callback((e) -> m_mf.showOpenFile(false)).mnemonicKey(KeyEvent.VK_A).getAction();
		fileopenfolder = BPAction.build("Open Folder...").callback((e) -> m_mf.showOpenFolder()).getAction();
		filesave = BPAction.build("Save").callback((e) -> m_mf.save()).mnemonicKey(KeyEvent.VK_S).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK)).getAction();
		filesaveas = BPAction.build("Save as...").callback((e) -> m_mf.saveAs()).getAction();
		filecfgs = BPAction.build("Configs...").callback((e) -> m_mf.showConfigs()).getAction();
		filereloadcontext = BPAction.build("Reload Context").callback((e) -> m_mf.reloadContext()).getAction();
		fileprop = BPAction.build("Properties...").callback((e) -> m_mf.showProperty(m_mf.getSelectedResource())).getAction();
		fileexit = BPAction.build("Exit").callback((e) -> m_mf.exit()).mnemonicKey(KeyEvent.VK_X).getAction();

		viewtoggleleftpan = BPAction.build("Toggle Left Panel").callback((e) -> m_mf.toggleLeftPanel()).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_DOWN_MASK)).getAction();
		viewtogglebottompan = BPAction.build("Toggle Bottom Panel").callback((e) -> m_mf.toggleBottomPanel()).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_DOWN_MASK)).getAction();
		viewtogglerightpan = BPAction.build("Toggle Right Panel").callback((e) -> m_mf.toggleRightPanel()).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_DOWN_MASK)).getAction();
		viewfullscreen = BPAction.build("FullScreen").callback((e) -> m_mf.fullScreen()).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0)).getAction();

		scswitchnexttab = BPAction.build("SwitchNextTab").callback((e) -> m_mf.switchTab(1)).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.CTRL_DOWN_MASK)).getAction();
		scswitchlasttab = BPAction.build("SwitchLastTab").callback((e) -> m_mf.switchTab(-1)).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK)).getAction();
		scclosecurrenttab = BPAction.build("CloseCurrentTab").callback((e) -> m_mf.closeCurrentTab()).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK)).getAction();

		navresource = BPAction.build("Resource...").callback((e) -> m_mf.showLocateResoruce()).mnemonicKey(KeyEvent.VK_R).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK)).getAction();
		navprjitem = BPAction.build("Project Item...").callback((e) -> m_mf.showLocateProjectItem()).mnemonicKey(KeyEvent.VK_P).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.ALT_DOWN_MASK)).getAction();
		naveditor = BPAction.build("Editor...").callback((e) -> m_mf.showSwitchEditor()).mnemonicKey(KeyEvent.VK_E).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK)).getAction();
		navoverview = BPAction.build("Overview...").callback((e) -> m_mf.showOverview()).mnemonicKey(KeyEvent.VK_O).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.ALT_DOWN_MASK)).getAction();
		navcmd = BPAction.build("Command...").callback((e) -> m_mf.showCommandPane()).mnemonicKey(KeyEvent.VK_3).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.ALT_DOWN_MASK)).getAction();

		helpsysinfo = BPAction.build("System Info...").callback((e) -> CommonUIOperations.showSystemInfo()).getAction();
	}

	public Action[] getShortCutActions()
	{
		return new Action[] { scswitchnexttab, scswitchlasttab, scclosecurrenttab };
	}
}

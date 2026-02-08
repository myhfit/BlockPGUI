package bp.ui.actions;

import javax.swing.Action;

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
		filenewfile = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILENEWFILE, e -> m_mf.showNewFile(null));
		filenewproject = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILENEWPROJECT, e -> m_mf.showNewProject());
		fileneweditor = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILENEWEDITOR, e -> m_mf.showNewEditor());
		fileopen = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILEOPEN, e -> m_mf.showOpenFile(true));
		fileopenas = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILEOPENAS, e -> m_mf.showOpenFile(false));
		fileopenfolder = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILEOPENFOLDER, e -> m_mf.showOpenWorkspace());
		filesave = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILESAVE, e -> m_mf.save());
		filesaveas = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILESAVEAS, e -> m_mf.saveAs());
		filecfgs = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILECFGS, e -> m_mf.showConfigs());
		filereloadcontext = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILERELOADCONTEXT, e -> m_mf.reloadContext());
		fileprop = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILEPROP, e -> m_mf.showProperty(m_mf.getSelectedResource()));
		fileexit = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILEEXIT, e -> m_mf.exit());

		viewtoggleleftpan = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUVIEWTOGGLELEFTPAN, e -> m_mf.toggleLeftPanel());
		viewtogglebottompan = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUVIEWTOGGLEBOTTOMPAN, e -> m_mf.toggleBottomPanel());
		viewtogglerightpan = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUVIEWTOGGLERIGHTPAN, e -> m_mf.toggleRightPanel());
		viewfullscreen = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUVIEWFULLSCREEN, e -> m_mf.fullScreen());

		scswitchnexttab = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUSCSWITCHNEXTTAB, e -> m_mf.switchTab(1));
		scswitchlasttab = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUSCSWITCHLASTTAB, e -> m_mf.switchTab(-1));
		scclosecurrenttab = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUSCCLOSECURRENTTAB, e -> m_mf.closeCurrentTab());

		navresource = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUNAVRESOURCE, e -> m_mf.showLocateResource());
		navprjitem = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUNAVPRJITEM, e -> m_mf.showLocateProjectItem());
		naveditor = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUNAVEDITOR, e -> m_mf.showSwitchEditor());
		navoverview = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUNAVOVERVIEW, e -> m_mf.showOverview());
		navcmd = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUNAVCMD, e -> m_mf.showCommandPane());

		helpsysinfo = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUHELPSYSINFO, e -> CommonUIOperations.showSystemInfo());
	}

	public Action[] getShortCutActions()
	{
		return new Action[] { scswitchnexttab, scswitchlasttab, scclosecurrenttab };
	}
}

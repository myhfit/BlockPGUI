package bp.ui.actions;

import javax.swing.Action;

import bp.res.BPResource;
import bp.ui.frame.BPMainFrame;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIUtil;

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
	public Action vieweditortoggleleftpan;
	public Action vieweditortogglebottompan;
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
	public Action helpabout;

	protected BPMainFrame m_mf;

	public BPMainFrameActions(BPMainFrame mf)
	{
		filenewfile = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILENEWFILE, UIUtil.makeDynamicInstCB(mf, "showNewFile", (BPResource) null));
		filenewproject = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILENEWPROJECT, UIUtil.makeDynamicInstCB(mf, "showNewProject"));
		fileneweditor = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILENEWEDITOR, UIUtil.makeDynamicInstCB(mf, "showNewEditor"));
		fileopen = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILEOPEN, UIUtil.makeDynamicInstCB(mf, "showOpenFile", true));
		fileopenas = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILEOPENAS, UIUtil.makeDynamicInstCB(mf, "showOpenFile", false));
		fileopenfolder = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILEOPENFOLDER, UIUtil.makeDynamicInstCB(mf, "showOpenWorkspace"));
		filesave = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILESAVE, UIUtil.makeDynamicInstCB(mf, "save"));
		filesaveas = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILESAVEAS, UIUtil.makeDynamicInstCB(mf, "saveAs"));
		filecfgs = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILECFGS, UIUtil.makeDynamicInstCB(mf, "showConfigs"));
		filereloadcontext = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILERELOADCONTEXT, UIUtil.makeDynamicInstCB(mf, "reloadContext"));
		fileprop = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILEPROP, UIUtil.makeDynamicInstCB(mf, "showSelectedResourceProperty"));
		fileexit = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILEEXIT, UIUtil.makeDynamicInstCB(mf, "exit"));

		viewtoggleleftpan = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUVIEWTOGGLELEFTPAN, UIUtil.makeDynamicInstCB(mf, "toggleLeftPanel"));
		viewtogglebottompan = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUVIEWTOGGLEBOTTOMPAN, UIUtil.makeDynamicInstCB(mf, "toggleBottomPanel"));
		viewtogglerightpan = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUVIEWTOGGLERIGHTPAN, UIUtil.makeDynamicInstCB(mf, "toggleRightPanel"));
		viewfullscreen = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUVIEWFULLSCREEN, UIUtil.makeDynamicInstCB(mf, "fullScreen"));

		vieweditortoggleleftpan = BPActionHelpers.getActionWithAlias(BPActionConstCommon.MF_MNUVIEWTOGGLELEFTPAN, BPActionConstCommon.MF_MNUVIEWTOGGLELEFTPAN_INNER, UIUtil.makeDynamicInstCB(mf, "toggleEditorLeftPanel"));
		vieweditortogglebottompan = BPActionHelpers.getActionWithAlias(BPActionConstCommon.MF_MNUVIEWTOGGLEBOTTOMPAN, BPActionConstCommon.MF_MNUVIEWTOGGLEBOTTOMPAN_INNER, UIUtil.makeDynamicInstCB(mf, "toggleEditorBottomPanel"));

		scswitchnexttab = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUSCSWITCHNEXTTAB, UIUtil.makeDynamicInstCB(mf, "switchTab", 1));
		scswitchlasttab = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUSCSWITCHLASTTAB, UIUtil.makeDynamicInstCB(mf, "switchTab", -1));
		scclosecurrenttab = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUSCCLOSECURRENTTAB, UIUtil.makeDynamicInstCB(mf, "closeCurrentTab"));

		navresource = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUNAVRESOURCE, UIUtil.makeDynamicInstCB(mf, "showLocateResource"));
		navprjitem = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUNAVPRJITEM, UIUtil.makeDynamicInstCB(mf, "showLocateProjectItem"));
		naveditor = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUNAVEDITOR, UIUtil.makeDynamicInstCB(mf, "showSwitchEditor"));
		navoverview = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUNAVOVERVIEW, UIUtil.makeDynamicInstCB(mf, "showOverview"));
		navcmd = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUNAVCMD, UIUtil.makeDynamicInstCB(mf, "showCommandPane"));

		helpsysinfo = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUHELPSYSINFO, e -> CommonUIOperations.showSystemInfo());
		helpabout = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUHELPABOUT, e -> CommonUIOperations.showAbout());
	}

	public Action[] getShortCutActions()
	{
		return new Action[] { scswitchnexttab, scswitchlasttab, scclosecurrenttab };
	}
}

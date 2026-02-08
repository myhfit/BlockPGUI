package bp.ui.actions;

import static bp.ui.actions.BPActionConstCommon.*;

import bp.ui.res.icon.BPIconResV;

public class BPActionHelperCommon extends BPActionHelperBase<BPActionConstCommon>
{
	public final static String ACTIONHELPER_PACK_MAIN = "m";

	public String getPackName()
	{
		return ACTIONHELPER_PACK_MAIN;
	}

	public void initDefaults()
	{
		// Common text
		putAction(TXT_TEMP, "Temp", null, null, null, null);
		putAction(TXT_NEWEDITOR, "New Editor", null, null, null, null);
		putAction(TXT_PROCESSOR, "Processor", null, null, null, null);
		putAction(TXT_DATAPIPES, "DataPipes", null, null, null, null);
		putAction(TXT_TEXT, "Text", null, null, null, null);
		putAction(TXT_BYTEARR, "byte[]", null, null, null, null);
		putAction(TXT_SYSINFO, "System Info", null, null, null, null);
		putAction(TXT_ERR, "Error", null, null, null, null);
		putAction(TXT_SEL, "Select", null, null, null, null);
		putAction(TXT_SELEDITOR, "Select Editor", null, null, null, null);
		putAction(TXT_EDITOR, "Editor", null, null, null, null);
		putAction(TXT_TF, "Transformer", null, null, null, null);
		putAction(TXT_FORMAT, "Format", null, null, null, null);
		putAction(TXT_FUNC, "Function", null, null, null, null);
		putAction(TXT_EDIT, "Edit", null, null, null, null);
		putAction(TXT_RES, "Resource", null, null, null, null);
		putAction(TXT_LIST, "List", null, null, null, null);
		putAction(TXT_PRJ, "Project", null, null, null, null);
		putAction(TXT_PRJITEM, "Project Item", null, null, null, null);
		putAction(TXT_TOOL, "Tool", null, null, null, null);
		putAction(TXT_COMMON, "Common", null, null, null, null);
		putAction(TXT_FILE, "File", null, null, null, null);
		putAction(TXT_CONFIGS, "Configs", null, null, null, null);
		putAction(TXT_TASK, "Task", null, null, null, null);
		putAction(TXT_SCHEDULE, "Schedule", null, null, null, null);
		putAction(TXT_CONSOLE, "Console", null, null, null, null);
		putAction(TXT_WORKING, "Working", null, null, null, null);
		putAction(TXT_SOURCE, "Source", null, null, null, null);
		putAction(TXT_TIME, "Time", null, null, null, null);
		putAction(TXT_ENV, "Environment", null, null, null, null);

		// Common dialog
		putAction(DLG_OK, "OK", null, null, "ctrl ENTER", "O");
		putAction(DLG_CC, "Cancel", null, null, "ESCAPE", "C");
		putAction(DLG_APPLY, "Apply", null, null, null, "a");
		putAction(DLG_YES, "Yes", null, null, "ENTER", "Y");
		putAction(DLG_NO, "No", null, null, "ESCAPE", "N");

		putAction(FDLG_FIND, "Find", null, null, "ctrl ENTER", "F");
		putAction(FDLG_REPLACE, "Replace", null, null, null, "R");
		putAction(FDLG_REPLACEALL, "ReplaceAll", null, null, null, "A");

		// Mainframe
		putAction(MF_MNUFILE, "File", null, null, null, "F");
		putAction(MF_MNUEDIT, "Edit", null, null, null, "E");
		putAction(MF_MNUVIEW, "View", null, null, null, "V");
		putAction(MF_MNUTOOL, "Tool", null, null, null, "T");
		putAction(MF_MNUMAINUI, "Main Frame", null, null, null, "M");
		putAction(MF_MNUNAV, "Navigate", null, null, null, "N");
		putAction(MF_MNUSHORTCUTS, "Shortcut", null, null, null, "S");
		putAction(MF_MNUHELP, "Help", null, null, null, "H");

		putAction(MF_MNUFILENEW, "New", null, null, null, "N");
		putAction(MF_MNUFILENEWFILE, "File...", null, null, "ctrl N", "F");
		putAction(MF_MNUFILENEWPROJECT, "Project...", null, null, null, "P");
		putAction(MF_MNUFILENEWEDITOR, "Editor...", null, null, "ctrl T", "E");
		putAction(MF_MNUFILEOPEN, "Open File...", null, null, "ctrl O", "O");
		putAction(MF_MNUFILEOPENAS, "Open File As...", null, null, null, "A");
		putAction(MF_MNUFILEOPENFOLDER, "Open Workspace...", null, null, null, null);
		putAction(MF_MNUFILESAVE, "Save", null, null, "ctrl S", "S");
		putAction(MF_MNUFILESAVEAS, "Save as...", null, null, null, null);
		putAction(MF_MNUFILECFGS, "Configs...", null, null, null, null);
		putAction(MF_MNUFILERELOADCONTEXT, "Reload Context...", null, null, null, null);
		putAction(MF_MNUFILEPROP, "Properties...", null, null, null, null);
		putAction(MF_MNUFILEEXIT, "Exit", null, null, null, "X");
		putAction(MF_MNUFILECLOSE, "Close", null, null, null, "X");

		putAction(MF_MNUVIEWTOGGLELEFTPAN, "Toggle Left Panel", null, null, "alt Q", null);
		putAction(MF_MNUVIEWTOGGLEBOTTOMPAN, "Toggle Bottom Panel", null, null, "alt W", null);
		putAction(MF_MNUVIEWTOGGLERIGHTPAN, "Toggle Right Panel", null, null, "alt R", null);
		putAction(MF_MNUVIEWFULLSCREEN, "FullScreen", null, null, "F11", null);

		putAction(MF_MNUSCSWITCHNEXTTAB, "SwitchNextTab", null, null, "ctrl TAB", null);
		putAction(MF_MNUSCSWITCHLASTTAB, "SwitchLastTab", null, null, "ctrl shift TAB", null);
		putAction(MF_MNUSCCLOSECURRENTTAB, "CloseCurrentTab", null, null, "ctrl W", null);

		putAction(MF_MNUNAVRESOURCE, "Resource...", null, null, "ctrl shift R", "R");
		putAction(MF_MNUNAVPRJITEM, "Project Item...", null, null, "alt P", "P");
		putAction(MF_MNUNAVEDITOR, "Editor...", null, null, "ctrl shift E", "E");
		putAction(MF_MNUNAVOVERVIEW, "Overview...", null, null, "alt O", "O");
		putAction(MF_MNUNAVCMD, "Command...", null, null, "alt 3", "3");

		putAction(MF_MNUSCRIPTS, "Scripts...", null, null, null, null);
		putAction(MF_MNUEXTS, "Extensions...", null, null, null, null);
		putAction(MF_MNUMODS, "Modules...", null, null, null, null);

		putAction(MF_MNUHELPSYSINFO, "System Info...", null, null, null, null);
		putAction(MF_MNUSCSEDITSCS, "Edit Shortcuts...", null, null, null, null);

		// actbtn
		putAction(ACT_BTNRUN, "run", "Run", BPIconResV::START, null, null);
		putAction(ACT_BTNRUN_ACC, null, null, null, "F5", null);
		putAction(ACT_BTNADD, "add", "Add", BPIconResV::ADD, null, null);
		putAction(ACT_BTNADD_ADDTF, "addtf", "Add Transformer", null, null, null);
		putAction(ACT_BTNADD_ADDEP, "addep", "Add Endpoint", null, null, null);
		putAction(ACT_BTNADD_CREATEEDITOR, "create", "Create Editor", null, null, null);
		putAction(ACT_BTNADD_NEWLINE, "newline", "New Line", null, null, null);
		putAction(ACT_BTNADD_INSERT, "insert", "Insert", null, null, null);
		putAction(ACT_BTNADD_PUSH, "push", "Push", null, null, null);
		putAction(ACT_BTNADDLINK, "add link", "Add Link", BPIconResV::RELATION, null, null);
		putAction(ACT_BTNCONFIG, "config", "Config", BPIconResV::EDIT, null, null);
		putAction(ACT_BTNSETTINGS, "settings", "Settings", BPIconResV::EDIT, null, null);
		putAction(ACT_BTNUP, "moveup", "Move Up", BPIconResV::TOUP, null, null);
		putAction(ACT_BTNUP_BACK, "back", "Back", null, null, null);
		putAction(ACT_BTNDOWN, "movedown", "Move Down", BPIconResV::TODOWN, null, null);
		putAction(ACT_BTNDEL, "del", "Remove", BPIconResV::DEL, null, null);
		putAction(ACT_BTNLAYOUT, "layout", "Layout", BPIconResV::LAYOUT, null, null);
		putAction(ACT_BTNOPEN, "open", "Open", BPIconResV::START, null, null);
		putAction(ACT_BTNOPEN_ACC, null, null, null, "F2", null);
		putAction(ACT_BTNEDIT, "edit", "Edit", BPIconResV::EDIT, null, null);
		putAction(ACT_BTNEDIT_KV, "editkv", "Edit KV", null, null, null);
		putAction(ACT_BTNEDIT_GRABKEYS, "grabkeys", "Grab Keys", null, null, null);
		putAction(ACT_BTNSAVE, "save", "Save", BPIconResV::SAVE, null, null);
		putAction(ACT_BTNSAVE_ACC, null, null, null, "ctrl S", null);
		putAction(ACT_BTNREFRESH, "refresh", "Refresh", BPIconResV::REFRESH, null, null);
		putAction(ACT_BTNSTAT, "stat", "Statistics", BPIconResV::MORE, null, null);

		putAction(ACT_BTNTOGGLE, "toggle", "Toggle", BPIconResV::DROPDOWN, null, null);
		putAction(ACT_BTNTOGGLE_DETAIL, "toggledetail", "Toggle detail", null, null, null);

		putAction(ACT_BTNDEL, "del", "Delete", BPIconResV::DEL, null, null);
		putAction(ACT_BTNDEL_ACC, null, null, null, "DELETE", null);
		putAction(ACT_BTNDEL_POP, "pop", "Pop", null, null, null);
		putAction(ACT_BTNCLONE, "clone", "Clone", BPIconResV::CLONE, null, null);
		
		putAction(ACT_BTNSTART, "start", "Start", BPIconResV::START, null, null);
		putAction(ACT_BTNSTART_ACC, null, null, null, "F5", null);
		putAction(ACT_BTNSTOP, "stop", "Stop", BPIconResV::STOP, null, null);

		putAction(ACT_BTNENABLE, "enable", "Enable", BPIconResV::START, null, null);
		putAction(ACT_BTNDISABLE, "disable", "Disable", BPIconResV::STOP, null, null);

		putAction(ACT_BTNCOMPARE, "compare", "Compare", BPIconResV::LEFTRIGHT, null, null);
		putAction(ACT_BTNCLOSETAB, "close", "Close", BPIconResV::KILL, null, null);
		putAction(ACT_BTNCLOSETAB_ACC, null, null, null, "ctrl W", null);
		putAction(ACT_BTNGOTO, "goto", "Goto", BPIconResV::TORIGHT, null, null);
		putAction(ACT_BTNGOTO_ACC, null, null, null, "F6", null);

		// rmenu
		putAction(CTX_MNUCOPY, "Copy", null, null, null, "C");
		putAction(CTX_MNUCOPYTO, "Copy To...", null, null, null, null);
		putAction(CTX_MNUCUT, "Cut", null, null, null, "T");
		putAction(CTX_MNUPASTE, "Paste", null, null, null, "P");
		putAction(CTX_MNUCLEAR, "Clear", null, null, null, null);

		putAction(CTX_MNUADD, "Add", null, null, null, "A");
		putAction(CTX_MNUNEW, "New", null, null, null, "N");
		putAction(CTX_MNUNEWFILE, "File", null, null, null, "F");
		putAction(CTX_MNUNEWDIR, "Directory", null, null, null, "D");
		putAction(CTX_MNUOPEN, "Open", null, null, null, "O");
		putAction(CTX_MNUOPENAS, "Open As...", null, null, null, "A");
		putAction(CTX_MNUOPENEXT, "Open External", null, null, null, null);
		putAction(CTX_MNUOPENEXTSYS, "Open", null, null, null, "O");
		putAction(CTX_MNUOPENEXTEDIT, "Edit", null, null, null, "E");
		putAction(CTX_MNUOPENEXTPRINT, "Print", null, null, null, "P");
		putAction(CTX_MNUOPENEXTASSOC, "System Assoc", null, null, null, null);
		putAction(CTX_MNUOPENTOOL, "Open With Tool", null, null, null, "W");
		putAction(CTX_MNUDEL, "Delete", null, null, null, "D");
		putAction(CTX_MNUEDIT, "Edit", null, null, null, "E");
		putAction(CTX_MNUEDIT_CELL, "Edit Cell", null, null, null, null);
		putAction(CTX_MNUVIEW, "View", null, null, null, "V");
		putAction(CTX_MNUVIEW_CELL, "View Cell", null, null, null, null);
		putAction(CTX_MNURENAME, "Rename", null, null, null, "M");
		putAction(CTX_MNUPROP, "Properties", null, null, null, "P");
		putAction(CTX_MNUREFRESH, "Refresh", null, null, null, "R");
		putAction(CTX_MNUOVERVIEW, "Overview...", null, null, null, null);
		putAction(CTX_MNUSTATISTICS, "Statistics...", null, null, null, null);

		putAction(CTX_MNUENABLE, "Enable", null, null, null, null);
		putAction(CTX_MNUDISABLE, "Disable", null, null, null, null);

		// PTree
		putAction(PTREE_REFRESH, "Refresh", "Refresh", BPIconResV::REFRESH, null, null);
		putAction(PTREE_PATHTREE, "PathTree", "Path Tree", BPIconResV::PATHTREE, null, null);
		putAction(PTREE_PRJTREE, "ProjectsTree", "Projects Tree", BPIconResV::PRJSTREE, null, null);
		putAction(PTREE_COMPUTERTREE, "ComputerTree", "Computer Path Tree", BPIconResV::PATHTREE_COMPUTER, null, null);
		putAction(PTREE_SPTREE, "SpecialTree", "Special", BPIconResV::PATHTREE_SPECIAL, null, null);
		
		// XYTable
		putAction(XYTBL_CTX_MNUTRANSCELL, "Transform Cell", null, null, null, null);
		
		// RawEditor
		putAction(RAWET_CTX_MNUCOPYHEX, "Copy(Hex)", null, null, null, null);
		putAction(RAWET_CTX_MNUCOPYTEXT, "Copy(Text)", null, null, null, null);
		
		// Tool
		putAction(TNAME_DPTOOL, "Data Pipe Tool", null, null, null, null);
		putAction(TNAME_PARRAEDITOR, "Parallel Editor", null, null, null, null);
	}

	protected Class<BPActionConstCommon> getConstClass()
	{
		return BPActionConstCommon.class;
	}
}

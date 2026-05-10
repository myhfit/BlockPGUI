package bp.ui.actions;

import static bp.ui.actions.BPActionConstCommon.*;

import java.util.Map;

import bp.ui.res.icon.BPIconResV;

public class BPActionHelperCommon extends BPActionHelperBase<BPActionConstCommon>
{
	public final static String ACTIONHELPER_PACK_MAIN = "m";

	public String getPackName()
	{
		return ACTIONHELPER_PACK_MAIN;
	}

	public void initDefaults(Map<Integer, Object> actmap)
	{
		// Common text
		putAction(actmap, TXT_TEMP, "Temp", null, null, null, null);
		putAction(actmap, TXT_NEWEDITOR, "New Editor", null, null, null, null);
		putAction(actmap, TXT_PROCESSOR, "Processor", null, null, null, null);
		putAction(actmap, TXT_DATAPIPES, "DataPipes", null, null, null, null);
		putAction(actmap, TXT_SYSINFO, "System Info", null, null, null, null);
		putAction(actmap, TXT_SEL, "Select", null, null, null, null);
		putAction(actmap, TXT_SELEDITOR, "Select Editor", null, null, null, null);
		putAction(actmap, TXT_EDITOR, "Editor", null, null, null, null);
		putAction(actmap, TXT_TF, "Transformer", null, null, null, null);
		putAction(actmap, TXT_ENDPOINT, "Endpoint", null, null, null, null);
		putAction(actmap, TXT_FORMAT, "Format", null, null, null, null);
		putAction(actmap, TXT_VIEW, "View", null, null, null, null);
		putAction(actmap, TXT_EDIT, "Edit", null, null, null, null);
		putAction(actmap, TXT_LIST, "List", null, null, null, null);
		putAction(actmap, TXT_PRJ, "Project", null, null, null, null);
		putAction(actmap, TXT_PRJITEM, "Project Item", null, null, null, null);
		putAction(actmap, TXT_TOOL, "Tool", null, null, null, null);
		putAction(actmap, TXT_COMMON, "Common", null, null, null, null);
		putAction(actmap, TXT_DE_EN, "Decode/Encode", null, null, null, null);
		putAction(actmap, TXT_HASH, "Hash", null, null, null, null);
		putAction(actmap, TXT_GENERATOR, "Generator", null, null, null, null);
		putAction(actmap, TXT_WEB, "Web", null, null, null, null);
		putAction(actmap, TXT_NETWORK, "Network", null, null, null, null);
		putAction(actmap, TXT_CONFIGS, "Configs", null, null, null, null);
		putAction(actmap, TXT_SETTING, "Setting", null, null, null, null);
		putAction(actmap, TXT_TASK, "Task", null, null, null, null);
		putAction(actmap, TXT_SCHEDULE, "Schedule", null, null, null, null);
		putAction(actmap, TXT_CONSOLE, "Console", null, null, null, null);
		putAction(actmap, TXT_WORKING, "Working", null, null, null, null);
		putAction(actmap, TXT_RESULT, "Result", null, null, null, null);
		putAction(actmap, TXT_TIME, "Time", null, null, null, null);
		putAction(actmap, TXT_ENV, "Environment", null, null, null, null);
		putAction(actmap, TXT_EXTS, "Extensions", null, null, null, null);
		putAction(actmap, TXT_MODS, "Modules", null, null, null, null);
		putAction(actmap, TXT_SCRIPTS, "Scripts", null, null, null, null);
		putAction(actmap, TXT_CREATE, "Create", null, null, null, null);
		putAction(actmap, TXT_DATA, "Data", null, null, null, null);
		putAction(actmap, TXT_AUTO, "Auto", null, null, null, null);
		putAction(actmap, TXT_RAW, "Raw", null, null, null, null);
		putAction(actmap, TXT_ESCAPED, "Escaped", null, null, null, null);
		putAction(actmap, TXT_PROPS, "Properties", null, null, null, null);
		putAction(actmap, TXT_MODE, "Mode", null, null, null, null);
		putAction(actmap, TXT_DECODE, "Decode", null, null, null, null);
		putAction(actmap, TXT_ENCODE, "Encode", null, null, null, null);
		putAction(actmap, TXT_OW_PRJS, "Overview:Projects", null, null, null, null);
		putAction(actmap, TXT_STATISTICS, "Statistics", null, null, null, null);
		putAction(actmap, TXT_WAITING, "Waiting", null, null, null, null);
		putAction(actmap, TXT_LAF, "LookAndFeel", null, null, null, null);
		putAction(actmap, TXT_SEARCHING, "Searching", null, null, null, null);
		putAction(actmap, TXT_CONFIRMSTARTTASK, "Confirm start task(s)", null, null, null, null);
		
		// Common dialog
		putAction(actmap, DLG_OK, "OK", null, null, "ctrl ENTER", "O");
		putAction(actmap, DLG_CC, "Cancel", null, null, "ESCAPE", "C");
		putAction(actmap, DLG_APPLY, "Apply", null, null, null, "a");
		putAction(actmap, DLG_YES, "Yes", null, null, "ENTER", "Y");
		putAction(actmap, DLG_NO, "No", null, null, "ESCAPE", "N");

		putAction(actmap, FDLG_MATCH, "Match", null, null, null, "M");
		putAction(actmap, FDLG_FIND, "Find", null, null, "ctrl ENTER", "F");
		putAction(actmap, FDLG_SEARCH, "Search", null, null, null, "S");
		putAction(actmap, FDLG_REPLACE, "Replace", null, null, null, "R");
		putAction(actmap, FDLG_REPLACEALL, "ReplaceAll", null, null, null, "A");
		putAction(actmap, FDLG_WHOLEWORD, "Whole word", null, null, null, "W");
		putAction(actmap, FDLG_CASESENSITIVE, "Case sensitive", null, null, null, "C");
		putAction(actmap, FDLG_BACKWARD, "Backward", null, null, null, "B");

		// Mainframe
		putAction(actmap, MF_MNUFILE, "File", null, null, null, "F");
		putAction(actmap, MF_MNUEDIT, "Edit", null, null, null, "E");
		putAction(actmap, MF_MNUVIEW, "View", null, null, null, "V");
		putAction(actmap, MF_MNUTOOL, "Tool", null, null, null, "T");
		putAction(actmap, MF_MNUMAINUI, "Main Frame", null, null, null, "M");
		putAction(actmap, MF_MNULOCALE, "Locale", null, null, null, "L");
		putAction(actmap, MF_MNUNAV, "Navigate", null, null, null, "N");
		putAction(actmap, MF_MNUSHORTCUTS, "Shortcut", null, null, null, "S");
		putAction(actmap, MF_MNUHELP, "Help", null, null, null, "H");

		putAction(actmap, MF_MNUFILENEW, "New", null, null, null, "N");
		putAction(actmap, MF_MNUFILENEWFILE, "File...", null, null, "ctrl N", "F");
		putAction(actmap, MF_MNUFILENEWPROJECT, "Project...", null, null, null, "P");
		putAction(actmap, MF_MNUFILENEWEDITOR, "Editor...", null, null, "ctrl T", "E");
		putAction(actmap, MF_MNUFILEOPEN, "Open File...", null, null, "ctrl O", "O");
		putAction(actmap, MF_MNUFILEOPENAS, "Open File As...", null, null, null, "A");
		putAction(actmap, MF_MNUFILEOPENFOLDER, "Open Workspace...", null, null, null, null);
		putAction(actmap, MF_MNUFILESAVE, "Save", null, null, "ctrl S", "S");
		putAction(actmap, MF_MNUFILESAVEAS, "Save as...", null, null, null, null);
		putAction(actmap, MF_MNUFILECFGS, "Configs...", null, null, null, null);
		putAction(actmap, MF_MNUFILERELOADCONTEXT, "Reload Context...", null, null, null, null);
		putAction(actmap, MF_MNUFILEPROP, "Properties...", null, null, null, null);
		putAction(actmap, MF_MNUFILEEXIT, "Exit", null, null, null, "X");
		putAction(actmap, MF_MNUFILECLOSE, "Close", null, null, null, "X");

		putAction(actmap, MF_MNUVIEWTOGGLELEFTPAN, "Toggle Left Panel", null, null, "alt Q", null);
		putAction(actmap, MF_MNUVIEWTOGGLEBOTTOMPAN, "Toggle Bottom Panel", null, null, "alt W", null);
		putAction(actmap, MF_MNUVIEWTOGGLERIGHTPAN, "Toggle Right Panel", null, null, "alt R", null);
		putAction(actmap, MF_MNUVIEWFULLSCREEN, "FullScreen", null, null, "F11", null);
		putAction(actmap, MF_MNUVIEWSELLOCALE, "Select...", null, null, null, "S");

		putAction(actmap, MF_MNUSCSWITCHNEXTTAB, "SwitchNextTab", null, null, "ctrl TAB", null);
		putAction(actmap, MF_MNUSCSWITCHLASTTAB, "SwitchLastTab", null, null, "ctrl shift TAB", null);
		putAction(actmap, MF_MNUSCCLOSECURRENTTAB, "CloseCurrentTab", null, null, "ctrl W", null);

		putAction(actmap, MF_MNUNAVRESOURCE, "Resource...", null, null, "ctrl shift R", "R");
		putAction(actmap, MF_MNUNAVPRJITEM, "Project Item...", null, null, "alt P", "P");
		putAction(actmap, MF_MNUNAVEDITOR, "Editor...", null, null, "ctrl shift E", "E");
		putAction(actmap, MF_MNUNAVOVERVIEW, "Overview...", null, null, "alt O", "O");
		putAction(actmap, MF_MNUNAVCMD, "Command...", null, null, "alt 3", "3");

		putAction(actmap, MF_MNUSCRIPTS, "Scripts...", null, null, null, null);
		putAction(actmap, MF_MNUEXTS, "Extensions...", null, null, null, null);
		putAction(actmap, MF_MNUMODS, "Modules...", null, null, null, null);

		putAction(actmap, MF_MNUHELPSYSINFO, "System Info...", null, null, null, null);
		putAction(actmap, MF_MNUHELPABOUT, "About...", null, null, null, null);
		putAction(actmap, MF_MNUSCSEDITSCS, "Edit Shortcuts...", null, null, null, null);
		
		// func
		putAction(actmap, FUNC_SEL_FORMATEDITOR, "Select format and editor", null, null, null, null);

		// actbtn
		putAction(actmap, ACT_BTNRUN, "run", "Run", BPIconResV::START, null, null);
		putAction(actmap, ACT_BTNRUN_ACC, null, null, null, "F5", null);
		putAction(actmap, ACT_BTNADD, "add", "Add", BPIconResV::ADD, null, null);
		putAction(actmap, ACT_BTNADD_ADDTF, "addtf", "Add Transformer", null, null, null);
		putAction(actmap, ACT_BTNADD_ADDEP, "addep", "Add Endpoint", null, null, null);
		putAction(actmap, ACT_BTNADD_CREATEEDITOR, "create", "Create Editor", null, null, null);
		putAction(actmap, ACT_BTNADD_NEWLINE, "newline", "New Line", null, null, null);
		putAction(actmap, ACT_BTNADD_INSERT, "insert", "Insert", null, null, null);
		putAction(actmap, ACT_BTNADD_PUSH, "push", "Push", null, null, null);
		putAction(actmap, ACT_BTNADDLINK, "add link", "Add Link", BPIconResV::RELATION, null, null);
		putAction(actmap, ACT_BTNCONFIG, "config", "Config", BPIconResV::EDIT, null, null);
		putAction(actmap, ACT_BTNSETTINGS, "settings", "Settings", BPIconResV::EDIT, null, null);
		putAction(actmap, ACT_BTNUP, "moveup", "Move Up", BPIconResV::TOUP, null, null);
		putAction(actmap, ACT_BTNUP_BACK, "back", "Back", null, null, null);
		putAction(actmap, ACT_BTNDOWN, "movedown", "Move Down", BPIconResV::TODOWN, null, null);
		putAction(actmap, ACT_BTNLAYOUT, "layout", "Layout", BPIconResV::LAYOUT, null, null);
		putAction(actmap, ACT_BTNOPEN, "open", "Open", BPIconResV::OPEN, null, null);
		putAction(actmap, ACT_BTNOPEN_ACC, null, null, null, "ctrl O", null);
		putAction(actmap, ACT_BTNEDIT, "edit", "Edit", BPIconResV::EDIT, null, null);
		putAction(actmap, ACT_BTNEDIT_KV, "editkv", "Edit KV", null, null, null);
		putAction(actmap, ACT_BTNEDIT_XY, "editxy", "Edit XY", null, null, null);
		putAction(actmap, ACT_BTNEDIT_GRABKEYS, "grabkeys", "Grab Keys", null, null, null);
		putAction(actmap, ACT_BTNSAVE, "save", "Save", BPIconResV::SAVE, null, null);
		putAction(actmap, ACT_BTNSAVE_ACC, null, null, null, "ctrl S", null);
		putAction(actmap, ACT_BTNREFRESH, "refresh", "Refresh", BPIconResV::REFRESH, null, null);
		putAction(actmap, ACT_BTNREFRESH_ACC, null, null, null, "F5", null);
		putAction(actmap, ACT_BTNSTAT, "stat", "Statistics", BPIconResV::MORE, null, null);

		putAction(actmap, ACT_BTNTOGGLE, "toggle", "Toggle", BPIconResV::DROPDOWN, null, null);
		putAction(actmap, ACT_BTNTOGGLE_DETAIL, "toggledetail", "Toggle detail", null, null, null);
		putAction(actmap, ACT_BTNTOGGLE_LISTSUB, "listsub", "List sub", null, null, null);

		putAction(actmap, ACT_BTNDEL, "del", "Delete", BPIconResV::DEL, null, null);
		putAction(actmap, ACT_BTNDEL_ACC, null, null, null, "DELETE", null);
		putAction(actmap, ACT_BTNDEL_POP, "pop", "Pop", null, null, null);
		putAction(actmap, ACT_BTNCLONE, "clone", "Clone", BPIconResV::CLONE, null, null);
		putAction(actmap, ACT_BTNCLOSE, "Close", "Close", null, null, null);

		putAction(actmap, ACT_BTNSTART, "start", "Start", BPIconResV::START, null, null);
		putAction(actmap, ACT_BTNSTART_ACC, null, null, null, "F5", null);
		putAction(actmap, ACT_BTNSTOP, "stop", "Stop", BPIconResV::STOP, null, null);

		putAction(actmap, ACT_BTNENABLE, "enable", "Enable", BPIconResV::START, null, null);
		putAction(actmap, ACT_BTNDISABLE, "disable", "Disable", BPIconResV::STOP, null, null);

		putAction(actmap, ACT_BTNCOMPARE, "compare", "Compare", BPIconResV::LEFTRIGHT, null, null);
		putAction(actmap, ACT_BTNCLOSETAB, "close", "Close", BPIconResV::KILL, null, null);
		putAction(actmap, ACT_BTNCLOSETAB_ACC, null, null, null, "ctrl W", null);
		putAction(actmap, ACT_BTNGOTO, "goto", "Goto", BPIconResV::TORIGHT, null, null);
		putAction(actmap, ACT_BTNGOTO_ACC, null, null, null, "F6", null);
		putAction(actmap, ACT_BTNESCAPE, "Escape", "Escape", BPIconResV::TORIGHT, null, null);
		putAction(actmap, ACT_BTNUNESCAPE, "Unescape", "Unescape", BPIconResV::TOLEFT, null, null);
		putAction(actmap, ACT_BTNSET, "Set", "Set", BPIconResV::START, null, null);
		putAction(actmap, ACT_BTNFILTER, "filter", "Filter", BPIconResV::CLONE, null, null);
		putAction(actmap, ACT_BTNCHAINFILTER, "chain filter", "Chain filter", BPIconResV::CLONE, null, null);

		// rmenu
		putAction(actmap, CTX_MNUCOPY, "Copy", null, null, null, "C");
		putAction(actmap, CTX_MNUCOPYTO, "Copy To...", null, null, null, null);
		putAction(actmap, CTX_MNUCUT, "Cut", null, null, null, "T");
		putAction(actmap, CTX_MNUPASTE, "Paste", null, null, null, "P");
		putAction(actmap, CTX_MNUCLEAR, "Clear", null, null, null, null);

		putAction(actmap, CTX_MNUADD, "Add", null, null, null, "A");
		putAction(actmap, CTX_MNUNEW, "New", null, null, null, "N");
		putAction(actmap, CTX_MNUNEWFILE, "File", null, null, null, "F");
		putAction(actmap, CTX_MNUNEWDIR, "Directory", null, null, null, "D");
		putAction(actmap, CTX_MNUOPEN, "Open", null, null, null, "O");
		putAction(actmap, CTX_MNUOPENAS, "Open As...", null, null, null, "A");
		putAction(actmap, CTX_MNUOPENEXT, "Open External", null, null, null, null);
		putAction(actmap, CTX_MNUOPENEXTSYS, "Open", null, null, null, "O");
		putAction(actmap, CTX_MNUOPENEXTEDIT, "Edit", null, null, null, "E");
		putAction(actmap, CTX_MNUOPENEXTPRINT, "Print", null, null, null, "P");
		putAction(actmap, CTX_MNUOPENEXTASSOC, "System Assoc", null, null, null, null);
		putAction(actmap, CTX_MNUOPENTOOL, "Open With Tool", null, null, null, "W");
		putAction(actmap, CTX_MNUDEL, "Delete", null, null, null, "D");
		putAction(actmap, CTX_MNUEDIT, "Edit", null, null, null, "E");
		putAction(actmap, CTX_MNUEDIT_CELL, "Edit Cell", null, null, null, null);
		putAction(actmap, CTX_MNUVIEW, "View", null, null, null, "V");
		putAction(actmap, CTX_MNUVIEW_CELL, "View Cell", null, null, null, null);
		putAction(actmap, CTX_MNURENAME, "Rename", null, null, null, "M");
		putAction(actmap, CTX_MNUPROP, "Properties", null, null, null, "P");
		putAction(actmap, CTX_MNUREFRESH, "Refresh", null, null, null, "R");
		putAction(actmap, CTX_MNUOVERVIEW, "Overview...", null, null, null, null);
		putAction(actmap, CTX_MNUSTATISTICS, "Statistics...", null, null, null, null);

		putAction(actmap, CTX_MNUENABLE, "Enable", null, null, null, null);
		putAction(actmap, CTX_MNUDISABLE, "Disable", null, null, null, null);

		// PTree
		putAction(actmap, PTREE_REFRESH, "Refresh", "Refresh", BPIconResV::REFRESH, null, null);
		putAction(actmap, PTREE_PATHTREE, "PathTree", "Path Tree", BPIconResV::PATHTREE, null, null);
		putAction(actmap, PTREE_PRJTREE, "ProjectsTree", "Projects Tree", BPIconResV::PRJSTREE, null, null);
		putAction(actmap, PTREE_COMPUTERTREE, "ComputerTree", "Computer Path Tree", BPIconResV::PATHTREE_COMPUTER, null, null);
		putAction(actmap, PTREE_SPTREE, "SpecialTree", "Special", BPIconResV::PATHTREE_SPECIAL, null, null);

		// XYTable
		putAction(actmap, XYTBL_CTX_MNUTRANSCELL, "Transform Cell", null, null, null, null);

		// RawEditor
		putAction(actmap, RAWET_CTX_MNUCOPYHEX, "Copy(Hex)", null, null, null, null);
		putAction(actmap, RAWET_CTX_MNUCOPYTEXT, "Copy(Text)", null, null, null, null);

		// Tool
		putAction(actmap, TNAME_DPTOOL, "Data Pipe Tool", null, null, null, null);
		putAction(actmap, TNAME_PARRAEDITOR, "Parallel Editor", null, null, null, null);
		putAction(actmap, TNAME_STRESCAPE, "String Escape", null, null, null, null);
		putAction(actmap, TNAME_UNITS, "Units", null, null, null, null);
	}

	protected Class<BPActionConstCommon> getConstClass()
	{
		return BPActionConstCommon.class;
	}
}

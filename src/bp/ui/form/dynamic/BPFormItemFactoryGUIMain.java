package bp.ui.form.dynamic;

public class BPFormItemFactoryGUIMain implements BPFormItemFactory
{
	public boolean canHandle(String itemtype)
	{
		switch (itemtype)
		{
			case "separator":
			case "group":
			case "textfield":
			case "textfield_more":
			case "textfield_area":
			case "textarea":
			case "passfield":
			case "passfield_area":
			case "checkbox":
			case "groupheader":
			case "combobox":
			{
				return true;
			}
		}
		return false;
	}

	public BPFormItem create(String itemtype, BPFormItemDef def)
	{
		switch (itemtype)
		{
			case "textfield":
				return new BPFormItemTextField();
			case "textfield_more":
			case "textfield_area":
				return new BPFormItemTextFieldPane();
			case "textarea":
				return new BPFormItemTextArea();
			case "passfield":
				return new BPFormItemPasswordField();
			case "passfield_area":
				return new BPFormItemPasswordPane();
			case "checkbox":
				return new BPFormItemCheckBox();
			case "separator":
				return new BPFormItemSeparator();
			case "combobox":
				return new BPFormItemComboBox();
		}
		return null;
	}
}

package bp.ui.form.dynamic;

public interface BPFormItemFactory
{
	boolean canHandle(String itemtype);

	BPFormItem create(String itemtype, BPFormItemDef itemdef);
}

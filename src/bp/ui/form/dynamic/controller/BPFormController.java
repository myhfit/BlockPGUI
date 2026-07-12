package bp.ui.form.dynamic.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bp.ui.form.dynamic.BPFormContext;
import bp.ui.form.dynamic.BPFormItem;

public interface BPFormController
{
	default boolean validateValue(String key, BPFormContext context)
	{
		return true;
	}

	default List<?> listData(String key, BPFormContext context)
	{
		return new ArrayList<>();
	}

	default Object select(String key, Object oldvalue, BPFormContext context)
	{
		return null;
	}

	default void initSnapshot(Map<String, ?> data, BPFormContext context)
	{

	}

	default void callAction(String name, BPFormContext context)
	{

	}

	@SuppressWarnings("unchecked")
	default <T> T decodeValue(String key, Object v, BPFormContext context)
	{
		return (T) v;
	}

	@SuppressWarnings("unchecked")
	default <T> T encodeValue(String key, Object v, BPFormContext context)
	{
		return (T) v;
	}

	default String render(String key, Object v, BPFormContext context)
	{
		return v == null ? "" : v.toString();
	}

	default boolean initForm(BPFormContext context)
	{
		return false;
	}

	default boolean showData(Map<String, ?> data, boolean editable, BPFormContext context)
	{
		return false;
	}

	default Object controlSetValue(Object v, BPFormContext bpFormContext, BPFormItem item)
	{
		return v;
	}

	default Object controlGetValue(Object v, BPFormContext bpFormContext, BPFormItem item)
	{
		return v;
	}
}
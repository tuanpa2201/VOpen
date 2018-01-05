package base.view;

import java.util.Map;

public interface IVObjectChangeListener {
	public void onChanging(Map<String, Object> mapNewValue);
	public void onDiscard();
}

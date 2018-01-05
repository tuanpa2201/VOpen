package base.view;

import java.util.Map;

public interface HandlerSearchEvent {
 abstract void executeSearch(String Whereclause, Map<String, Object> params);
}

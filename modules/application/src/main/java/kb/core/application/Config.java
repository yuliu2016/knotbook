package kb.core.application;

import kb.service.api.Service;
import kb.service.api.application.ApplicationProps;
import kb.service.api.json.JSONObjectWrapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

class Config implements ApplicationProps {
    private JSONObject object;
    private KnotBook.ConfigHandle handle;

    private Map<String, JSONObjectWrapper> wrappers = new HashMap<>();

    Config(KnotBook.ConfigHandle handle) {
        this.handle = handle;
        try {
            object = new JSONObject(handle.read());
        } catch (JSONException e) {
            object = new JSONObject();
        }
    }

    void save() {
        handle.write(getJoinedText());
    }

    JSONObjectWrapper getConfig(Service service) {
        String key = service.getMetadata().getPackageName();
        JSONObjectWrapper wrapper = wrappers.get(key);
        if (wrapper != null) {
            return wrapper;
        }
        JSONObject savedConfig = object.optJSONObject(key);
        JSONObjectWrapper newWrapper;
        if (savedConfig != null) {
            newWrapper = new JSONObjectWrapper(savedConfig);
        } else {
            JSONObject newObject = new JSONObject();
            object.put(key, newObject);
            newWrapper = new JSONObjectWrapper(newObject);
        }
        wrappers.put(key, newWrapper);
        return newWrapper;
    }

    @Override
    public String getJoinedText() {
        return object.toString(4);
    }

    @Override
    public void setInputText(String inputText) {
        try {
            object = new JSONObject(inputText);
        } catch (JSONException e) {
            object = new JSONObject();
        }
    }
}

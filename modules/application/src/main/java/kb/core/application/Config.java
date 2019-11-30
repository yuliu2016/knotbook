package kb.core.application;

import kb.service.api.Service;
import kb.service.api.json.JSONObjectWrapper;
import org.json.JSONException;
import org.json.JSONObject;

public class Config {
    private JSONObject object;
    private ConfigHandle handle;

    Config(ConfigHandle handle) {
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

    JSONObjectWrapper createConfig(Service service) {
        String key = service.getMetadata().getName();
        JSONObject savedConfig = object.optJSONObject(key);
        JSONObjectWrapper newWrapper;
        if (savedConfig != null) {
            newWrapper = new JSONObjectWrapper(savedConfig);
        } else {
            JSONObject newObject = new JSONObject();
            object.put(key, newObject);
            newWrapper = new JSONObjectWrapper(newObject);
        }
        return newWrapper;
    }

    public String getJoinedText() {
        return object.toString(2);
    }

    public void setInputText(String inputText) {
        try {
            object = new JSONObject(inputText);
        } catch (JSONException e) {
            object = new JSONObject();
        }
    }
}

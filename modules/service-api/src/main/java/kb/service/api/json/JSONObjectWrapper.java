package kb.service.api.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Set;


@SuppressWarnings({"unused", "NullableProblems"})
public class JSONObjectWrapper implements Map<String, Object> {

    private JSONObject object;

    public JSONObjectWrapper(JSONObject object) {
        this.object = object;
    }

    public JSONObject getObject() {
        return object;
    }

    public JSONArrayWrapper getJSONArray(String key) throws JSONException {
        if (!object.has(key)) {
            object.put(key, new JSONArray());
        }
        return new JSONArrayWrapper(object.getJSONArray(key));
    }

    public JSONObjectWrapper getJSONObject(String key) throws JSONException {
        if (!object.has(key)) {
            object.put(key, new JSONObject());
        }
        return new JSONObjectWrapper(object.getJSONObject(key));
    }

    public int getInt(String key) throws JSONException {
        return object.getInt(key);
    }

    public <E extends Enum<E>> E getEnum(Class<E> clazz, String key) throws JSONException {
        return object.getEnum(clazz, key);
    }

    public boolean getBoolean(String key) throws JSONException {
        return object.getBoolean(key);
    }

    public BigInteger getBigInteger(String key) throws JSONException {
        return object.getBigInteger(key);
    }

    public BigDecimal getBigDecimal(String key) throws JSONException {
        return object.getBigDecimal(key);
    }

    public double getDouble(String key) throws JSONException {
        return object.getDouble(key);
    }

    public float getFloat(String key) throws JSONException {
        return object.getFloat(key);
    }

    public Number getNumber(String key) throws JSONException {
        return object.getNumber(key);
    }

    public long getLong(String key) throws JSONException {
        return object.getLong(key);
    }

    public String getString(String key) throws JSONException {
        return object.getString(key);
    }

    public Object opt(String key) {
        return object.opt(key);
    }

    public <E extends Enum<E>> E optEnum(Class<E> clazz, String key) {
        return object.optEnum(clazz, key);
    }

    public <E extends Enum<E>> E optEnum(Class<E> clazz, String key, E defaultValue) {
        return object.optEnum(clazz, key, defaultValue);
    }

    public boolean optBoolean(String key) {
        return object.optBoolean(key);
    }

    public boolean optBoolean(String key, boolean defaultValue) {
        return object.optBoolean(key, defaultValue);
    }

    public BigDecimal optBigDecimal(String key, BigDecimal defaultValue) {
        return object.optBigDecimal(key, defaultValue);
    }

    public BigInteger optBigInteger(String key, BigInteger defaultValue) {
        return object.optBigInteger(key, defaultValue);
    }

    public double optDouble(String key) {
        return object.optDouble(key);
    }

    public double optDouble(String key, double defaultValue) {
        return object.optDouble(key, defaultValue);
    }

    public float optFloat(String key) {
        return object.optFloat(key);
    }

    public float optFloat(String key, float defaultValue) {
        return object.optFloat(key, defaultValue);
    }

    public int optInt(String key) {
        return object.optInt(key);
    }

    public int optInt(String key, int defaultValue) {
        return object.optInt(key, defaultValue);
    }

    public JSONArray optJSONArray(String key) {
        return object.optJSONArray(key);
    }

    public JSONObjectWrapper optJSONObject(String key) {
        object.optJSONObject(key);
        return this;
    }

    public long optLong(String key) {
        return object.optLong(key);
    }

    public long optLong(String key, long defaultValue) {
        return object.optLong(key, defaultValue);
    }

    public Number optNumber(String key) {
        return object.optNumber(key);
    }

    public Number optNumber(String key, Number defaultValue) {
        return object.optNumber(key, defaultValue);
    }

    public String optString(String key) {
        return object.optString(key);
    }

    public String optString(String key, String defaultValue) {
        return object.optString(key, defaultValue);
    }

    public JSONObjectWrapper put(String key, boolean value) throws JSONException {
        object.put(key, value);
        return this;
    }

    public JSONObjectWrapper put(String key, Collection<?> value) throws JSONException {
        object.put(key, value);
        return this;
    }

    public JSONObjectWrapper put(String key, double value) throws JSONException {
        object.put(key, value);
        return this;
    }

    public JSONObjectWrapper put(String key, float value) throws JSONException {
        object.put(key, value);
        return this;
    }

    public JSONObjectWrapper put(String key, int value) throws JSONException {
        object.put(key, value);
        return this;
    }

    public JSONObjectWrapper put(String key, long value) throws JSONException {
        object.put(key, value);
        return this;
    }

    public JSONObjectWrapper put(String key, Map<?, ?> value) throws JSONException {
        object.put(key, value);
        return this;
    }

    public JSONObjectWrapper putOnce(String key, Object value) throws JSONException {
        object.putOnce(key, value);
        return this;
    }

    public JSONObjectWrapper putOpt(String key, Object value) throws JSONException {
        object.putOpt(key, value);
        return this;
    }

    @Override
    public int size() {
        return object.length();
    }

    @Override
    public boolean isEmpty() {
        return object.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return object.has(key.toString());
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("containsValue() is unsupported");
    }

    @Override
    public Object get(Object key) {
        return object.get(key.toString());
    }

    @Override
    public Object put(String key, Object value) {
        return object.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return object.remove(key.toString());
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        for (Entry<? extends String, ?> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("clear() is unsupported");
    }

    @Override
    public Set<String> keySet() {
        return object.keySet();
    }

    @Override
    public Collection<Object> values() {
        throw new UnsupportedOperationException("values() is unsupported");
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        throw new UnsupportedOperationException("entrySet() is unsupported");
    }
}

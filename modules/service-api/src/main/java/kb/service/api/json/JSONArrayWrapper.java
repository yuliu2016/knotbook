package kb.service.api.json;

import org.json.JSONArray;
import org.json.JSONException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@SuppressWarnings({"NullableProblems", "unused", "WeakerAccess"})
public class JSONArrayWrapper implements List<Object> {
    private JSONArray array;
    private ArrayList<Object> list;

    @SuppressWarnings("unchecked")
    public JSONArrayWrapper(JSONArray array) {
        this.array = array;
        try {
            var listField = JSONArray.class.getDeclaredField("myArrayList");
            listField.setAccessible(true);
            list = (ArrayList<Object>) listField.get(array);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public JSONArray getArray() {
        return array;
    }

    public boolean getBoolean(int index) throws JSONException {
        return array.getBoolean(index);
    }

    public double getDouble(int index) throws JSONException {
        return array.getDouble(index);
    }

    public float getFloat(int index) throws JSONException {
        return array.getFloat(index);
    }

    public Number getNumber(int index) throws JSONException {
        return array.getNumber(index);
    }

    public <E extends Enum<E>> E getEnum(Class<E> clazz, int index) throws JSONException {
        return array.getEnum(clazz, index);
    }

    public BigDecimal getBigDecimal(int index) throws JSONException {
        return array.getBigDecimal(index);
    }

    public BigInteger getBigInteger(int index) throws JSONException {
        return array.getBigInteger(index);
    }

    public int getInt(int index) throws JSONException {
        return array.getInt(index);
    }

    public JSONArrayWrapper getJSONArray(int index) throws JSONException {
        return new JSONArrayWrapper(array.getJSONArray(index));
    }

    public JSONObjectWrapper getJSONObject(int index) throws JSONException {
        return new JSONObjectWrapper(array.getJSONObject(index));
    }

    public long getLong(int index) throws JSONException {
        return array.getLong(index);
    }

    public String getString(int index) throws JSONException {
        return array.getString(index);
    }

    public boolean isNull(int index) {
        return array.isNull(index);
    }

    public Object opt(int index) {
        return array.opt(index);
    }

    public boolean optBoolean(int index) {
        return array.optBoolean(index);
    }

    public boolean optBoolean(int index, boolean defaultValue) {
        return array.optBoolean(index, defaultValue);
    }

    public double optDouble(int index) {
        return array.optDouble(index);
    }

    public double optDouble(int index, double defaultValue) {
        return array.optDouble(index, defaultValue);
    }

    public float optFloat(int index) {
        return array.optFloat(index);
    }

    public float optFloat(int index, float defaultValue) {
        return array.optFloat(index, defaultValue);
    }

    public int optInt(int index) {
        return array.optInt(index);
    }

    public int optInt(int index, int defaultValue) {
        return array.optInt(index, defaultValue);
    }

    public <E extends Enum<E>> E optEnum(Class<E> clazz, int index) {
        return array.optEnum(clazz, index);
    }

    public <E extends Enum<E>> E optEnum(Class<E> clazz, int index, E defaultValue) {
        return array.optEnum(clazz, index, defaultValue);
    }

    public BigInteger optBigInteger(int index, BigInteger defaultValue) {
        return array.optBigInteger(index, defaultValue);
    }

    public BigDecimal optBigDecimal(int index, BigDecimal defaultValue) {
        return array.optBigDecimal(index, defaultValue);
    }

    public JSONArrayWrapper optJSONArray(int index) {
        return new JSONArrayWrapper(array.optJSONArray(index));
    }

    public JSONObjectWrapper optJSONObject(int index) {
        return new JSONObjectWrapper(array.optJSONObject(index));
    }

    public long optLong(int index) {
        return array.optLong(index);
    }

    public long optLong(int index, long defaultValue) {
        return array.optLong(index, defaultValue);
    }

    public Number optNumber(int index) {
        return array.optNumber(index);
    }

    public Number optNumber(int index, Number defaultValue) {
        return array.optNumber(index, defaultValue);
    }

    public String optString(int index) {
        return array.optString(index);
    }

    public String optString(int index, String defaultValue) {
        return array.optString(index, defaultValue);
    }

    public JSONArrayWrapper put(boolean value) {
        array.put(value);
        return this;
    }

    public JSONArrayWrapper put(Collection<?> value) {
        array.put(value);
        return this;
    }

    public JSONArrayWrapper put(double value) throws JSONException {
        array.put(value);
        return this;
    }

    public JSONArrayWrapper put(float value) throws JSONException {
        array.put(value);
        return this;
    }

    public JSONArrayWrapper put(int value) {
        array.put(value);
        return this;
    }

    public JSONArrayWrapper put(long value) {
        array.put(value);
        return this;
    }

    public JSONArrayWrapper put(Map<?, ?> value) {
        array.put(value);
        return this;
    }

    public JSONArrayWrapper put(Object value) {
        array.put(value);
        return this;
    }

    public JSONArrayWrapper put(int index, boolean value) throws JSONException {
        array.put(index, value);
        return this;
    }

    public JSONArrayWrapper put(int index, Collection<?> value) throws JSONException {
        array.put(index, value);
        return this;
    }

    public JSONArrayWrapper put(int index, double value) throws JSONException {
        array.put(index, value);
        return this;
    }

    public JSONArrayWrapper put(int index, float value) throws JSONException {
        array.put(index, value);
        return this;
    }

    public JSONArrayWrapper put(int index, int value) throws JSONException {
        array.put(index, value);
        return this;
    }

    public JSONArrayWrapper put(int index, long value) throws JSONException {
        array.put(index, value);
        return this;
    }

    public JSONArrayWrapper put(int index, Map<?, ?> value) throws JSONException {
        array.put(index, value);
        return this;
    }

    public JSONArrayWrapper put(int index, Object value) throws JSONException {
        array.put(index, value);
        return this;
    }

    @Override
    public int size() {
        return array.length();
    }

    @Override
    public boolean isEmpty() {
        return array.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<Object> iterator() {
        return new Iterator<>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i == size() - 1;
            }

            @Override
            public Object next() {
                return get(i++);
            }
        };
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("toArray() is unsupported");
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("toArray() is unsupported");
    }

    @Override
    public boolean add(Object o) {
        array.put(o);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("containsAll() is unsupported");
    }

    @Override
    public boolean addAll(Collection<?> c) {
        for (Object o : c) {
            add(o);
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<?> c) {
        throw new UnsupportedOperationException("addAll() is unsupported");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("removeAll() is unsupported");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("retainAll() is unsupported");
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public Object get(int index) {
        return array.get(index);
    }

    @Override
    public Object set(int index, Object element) {
        return array.put(index, element);
    }

    @Override
    public void add(int index, Object element) {
        list.add(index, element);
    }

    @Override
    public Object remove(int index) {
        return array.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException("indexOf() is unsupported");
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException("lastIndexOf() is unsupported");
    }

    @Override
    public ListIterator<Object> listIterator() {
        throw new UnsupportedOperationException("listIterator() is unsupported");
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        throw new UnsupportedOperationException("listIterator() is unsupported");
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("subList() is unsupported");
    }
}

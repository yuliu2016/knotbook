package kb.core.application;

import kb.service.api.ServicePropListener;
import kb.service.api.ServiceProps;
import kb.service.api.application.ApplicationProps;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

class Registry2 implements ApplicationProps {

    static class ObservedProperties extends Properties {

        private Map<String, ServicePropListener> listeners = new HashMap<>();

        ObservedProperties() {
        }

        Map<String, ServicePropListener> getListeners() {
            return listeners;
        }

        @Override
        public synchronized Object put(Object key, Object value) {
            ServicePropListener listener = getListeners().get(key.toString());
            if (listener != null) {
                Object oldValue = super.put(key, value);
                String oldString = oldValue == null ? null : oldValue.toString();
                String newString = value == null ? null : value.toString();
                if (newString == null || !newString.equals(oldString)) {
                    listener.propertyChanged(oldString, newString);
                }
                return oldValue;
            }
            return super.put(key, value);
        }
    }

    private KnotBook.RegistryHandle handle;
    private boolean changed = false;
    private ObservedProperties props = new ObservedProperties();

    Registry2(KnotBook.RegistryHandle handle) {
        this.handle = handle;
        try (InputStream in = handle.input()) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getVal(String key) {
        return props.getProperty(key);
    }

    private void putVal(String key, String value) {
        props.put(key, value);
        changed = true;
    }

    private boolean containsVal(String key) {
        return props.contains(key);
    }

    private void removeVal(String key) {
        props.remove(key);
        changed = true;
    }

    void save() {
        if (changed) {
            try (OutputStream out = handle.output()) {
                props.store(out, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getJoinedText() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            props.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString(StandardCharsets.UTF_8);
    }

    @Override
    public void setInputText(String inputText) {
        try {
            props.load(new ByteArrayInputStream(inputText.getBytes()));
            changed = true;
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ServiceProps getProps(String name) {
        return new ServicePropsWrapper2(name);
    }

    @Override
    public boolean hasProps(String name) {
        for (Object o : props.keySet()) {
            if (o.toString().startsWith(name)) {
                return true;
            }
        }
        return false;
    }

    private void addListener0(String key, ServicePropListener listener) {
        props.getListeners().put(key, listener);
    }

    private void removeListener0(String key) {
        props.getListeners().remove(key);
    }

    private static boolean isValid(String key) {
        if (key == null || key.isEmpty() || !Character.isLetter(key.charAt(0))) {
            return false;
        }
        for (int i = 1; i < key.length(); i++) {
            if (!Character.isLetterOrDigit(key.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private class ServicePropsWrapper2 implements ServiceProps {

        String name;

        ServicePropsWrapper2(String name) {
            this.name = name;
        }

        private String wrap(String key) {
            return name + "/" + key;
        }

        @Override
        public void put(String key, String value) {
            if (isValid(key)) {
                putVal(wrap(key), value);
            }
        }

        @Override
        public String get(String key) {
            return getVal(wrap(key));
        }

        @Override
        public void remove(String key) {
            removeVal(wrap(key));
        }

        @Override
        public boolean contains(String key) {
            return containsVal(wrap(key));
        }

        @Override
        public void addListener(String key, ServicePropListener listener) {
            addListener0(wrap(key), listener);
        }

        @Override
        public void removeListener(String key) {
            removeListener0(wrap(key));
        }
    }
}

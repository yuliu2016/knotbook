package kb.service.api.application;

import kb.service.api.ServiceProps;

public interface ApplicationProps {
    String getJoinedText();

    void setInputText(String inputText);

    ServiceProps get(String name);

    boolean contains(String name);

    boolean remove(String name);
}

package kb.service.api.application;

import kb.service.api.ServiceProps;

@SuppressWarnings("unused")
public interface ApplicationProps {
    String getJoinedText();

    void setInputText(String inputText);

    ServiceProps getProps(String name);

    boolean hasProps(String name);
}

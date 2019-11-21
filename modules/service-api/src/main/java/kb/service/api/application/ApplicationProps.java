package kb.service.api.application;

import java.nio.file.Path;

@SuppressWarnings("unused")
public interface ApplicationProps {
    String getJoinedText();

    void setInputText(String inputText);

    Path getPath();
}

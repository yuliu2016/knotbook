package kb.service.api.application;

import kb.service.api.ServiceProps;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface ApplicationProps {
    String getJoinedText();

    void setInputText(@NotNull String inputText);

    ServiceProps getProps(@NotNull String name);

    boolean hasProps(@NotNull String name);
}

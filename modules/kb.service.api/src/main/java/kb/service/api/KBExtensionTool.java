package kb.service.api;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface KBExtensionTool {

    @NotNull
    String getName();

    void launch();
}

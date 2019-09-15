package kb.service.api.application;

import org.jetbrains.annotations.NotNull;

public interface ApplicationService {
    void launch(@NotNull PrivilagedContext context);
}

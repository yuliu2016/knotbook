package kb.core.application;

import java.io.InputStream;
import java.io.OutputStream;

public interface RegistryHandle {
    InputStream input();

    OutputStream output();
}

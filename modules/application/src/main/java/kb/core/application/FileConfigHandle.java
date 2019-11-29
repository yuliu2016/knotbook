package kb.core.application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class FileConfigHandle implements ConfigHandle {
    Path path;

    FileConfigHandle(Path path) {
        this.path = path;
    }

    @Override
    public String read() {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    @Override
    public void write(String s) {
        try {
            Files.writeString(path, s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

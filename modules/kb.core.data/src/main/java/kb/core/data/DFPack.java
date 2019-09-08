package kb.core.data;

import java.io.FileOutputStream;
import java.io.IOException;

@SuppressWarnings("unused")
public class DFPack {

    public void encode(DataFrame f, String path) {
        int ncol = f.getNcol();
        int nrow = f.getNrow();
        try (var stream = new FileOutputStream(path)) {
            stream.write(new byte[]{});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

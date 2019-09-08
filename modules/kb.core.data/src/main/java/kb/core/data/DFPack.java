package kb.core.data;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@SuppressWarnings("unused")
public class DFPack {

    public void encode(DataFrame f, String path) {
        int ncol = f.getNcol();
        int nrow = f.getNrow();
        try (var stream = new FileOutputStream(path)) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(100);
            buffer.put(10, (byte) 10);
            FileChannel channel = stream.getChannel();
            channel.write(buffer);
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

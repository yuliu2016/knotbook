package kb.service.api.array;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static kb.service.api.array.TableArray.*;

class ZipFormatHelper {
    /**
     * Extracts data from a zip file
     *
     * @param file The file path. Must be a real file. If an InputStream is used
     *             it must be saved to a temporary file before using this
     * @return the data
     */
    public static TableArray fromZipFormat(File file) {
        try (ZipFile zip = new ZipFile(file)) {
            
            ZipEntry modeEntry = zip.getEntry("array/mode");
            InputStream modeIn = zip.getInputStream(modeEntry);
            byte[] mode = modeIn.readAllBytes();
            
            ZipEntry numEntry = zip.getEntry("array/num");
            DataInputStream numIn = new DataInputStream(zip.getInputStream(numEntry));
            float[] num = new float[mode.length];
            for (int i = 0; i < mode.length; i++) {
                int m = mode[i];
                if (m == MODE_INT || m == MODE_FLOAT) {
                    num[i] = numIn.readFloat();
                }
            }
            
            ZipEntry strEntry = zip.getEntry("array/str");
            BufferedReader strIn = new BufferedReader(new InputStreamReader(zip.getInputStream(strEntry)));
            List<String> str = new ArrayList<>();
            for (int m : mode) {
                str.add(m == MODE_STR ? strIn.readLine() : null);
            }
            return new SimpleTableArray(1, mode.length, mode, num, str);
        } catch (IOException e) {
            e.printStackTrace();
            return Tables.emptyArray();
        }
    }

    public static void toZipFormat(TableArray array, OutputStream stream) {
        try (ZipOutputStream out = new ZipOutputStream(stream)) {
            out.setMethod(ZipEntry.DEFLATED);
            out.setLevel(9);
            ZipEntry modeEntry = new ZipEntry("array/mode");
            out.putNextEntry(modeEntry);
            out.write(array.mode, 0, array.len);
            out.closeEntry();
            ZipEntry numEntry = new ZipEntry("array/num");
            out.putNextEntry(numEntry);
            DataOutputStream numStream = new DataOutputStream(out);
            for (int i = 0; i < array.len; i++) {
                int m = array.mode[i];
                float v = array.num[i];
                if (m == MODE_FLOAT) {
                    numStream.writeFloat(v);
                }
            }
            numStream.flush();

            ZipEntry num2Entry = new ZipEntry("array/ints");
            out.putNextEntry(num2Entry);
            for (int i = 0; i < array.len; i++) {
                int m = array.mode[i];
                float v = array.num[i];
                if (m == MODE_INT) {
                    out.write((int) v);
                }
            }
            out.closeEntry();

            ZipEntry strEntry = new ZipEntry("array/str.txt");
            out.putNextEntry(strEntry);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out), 64);
            for (int i = 0; i < array.len; i++) {
                int m = array.mode[i];
                if (m == MODE_STR) {
                    String s = array.str.get(i);
                    writer.write(s);
                    writer.newLine();
                }
            }
            writer.flush();
            out.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

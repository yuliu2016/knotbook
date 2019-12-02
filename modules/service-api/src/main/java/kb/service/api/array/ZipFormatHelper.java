package kb.service.api.array;

import java.io.*;
import java.util.Map;
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
            TableArray array = Tables.emptyArray();
            ZipEntry modeEntry = zip.getEntry("array/mode");
            InputStream modeIn = zip.getInputStream(modeEntry);
            array.mode.value = modeIn.readAllBytes();
            array.mode.length = array.mode.value.length;
            array.len = array.mode.length;
            ZipEntry numEntry = zip.getEntry("array/num");
            DataInputStream numIn = new DataInputStream(zip.getInputStream(numEntry));
            array.num.resize(array.len);
            for (int i = 0; i < array.len; i++) {
                int m = array.mode.value[i];
                if (m == MODE_INT || m == MODE_FLOAT) {
                    array.num.value[i] = numIn.readFloat();
                }
            }
            ZipEntry strEntry = zip.getEntry("array/str");
            BufferedReader strIn = new BufferedReader(new InputStreamReader(zip.getInputStream(strEntry)));
            for (int i = 0; i < array.len; i++) {
                int m = array.mode.value[i];
                array.str.set(i, m == MODE_STR ? strIn.readLine() : null);
            }
            return array;
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
            out.write(array.mode.value, 0, array.len);
            out.closeEntry();
            ZipEntry numEntry = new ZipEntry("array/num");
            out.putNextEntry(numEntry);
            DataOutputStream numStream = new DataOutputStream(out);
            for (int i = 0; i < array.len; i++) {
                int m = array.mode.value[i];
                float v = array.num.value[i];
                if (m == MODE_FLOAT) {
                    numStream.writeFloat(v);
                }
            }
            numStream.flush();

            ZipEntry num2Entry = new ZipEntry("array/ints");
            out.putNextEntry(num2Entry);
            for (int i = 0; i < array.len; i++) {
                int m = array.mode.value[i];
                float v = array.num.value[i];
                if (m == MODE_INT) {
                    out.write((int) v);
                }
            }
            out.closeEntry();

            ZipEntry strEntry = new ZipEntry("array/str.txt");
            out.putNextEntry(strEntry);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out), 64);
            for (int i = 0; i < array.len; i++) {
                int m = array.mode.value[i];
                if (m == MODE_STR) {
                    String s = array.str.get(i);
                    writer.write(s);
                    writer.newLine();
                }
            }
            writer.flush();
            out.closeEntry();
            array.header.put("columns", String.valueOf(array.cols));
            ZipEntry headerEntry = new ZipEntry("array/header.txt");
            out.putNextEntry(headerEntry);
            BufferedWriter headerWriter = new BufferedWriter(new OutputStreamWriter(out), 64);
            for (Map.Entry<String, String> entry : array.header.entrySet()) {
                headerWriter.write(entry.getKey());
                headerWriter.write('=');
                headerWriter.write(entry.getValue());
            }
            headerWriter.flush();
            out.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.File;

public class Processor {
    private byte[] fileBytes;
    private int cantFiles;
    private int tableOffset;
    private ArrayList<FileInfo> processedFiles;
    private String path;
    public boolean DEBUG = true;

    public Processor(String pathToFile) {
        readFileBytes(Paths.get(pathToFile));
        extractHeaderData();
        this.path = createFolder("output");
    }

    public void analyzeFiles() {
        int j = tableOffset;
        for (int i = 0; i < this.cantFiles; i++) {
            processedFiles.add(processFile(j));
            j += 36;
        }
    }

    public void extractFiles() {
        FileInfo first = this.processedFiles.get(1);
        int nivelActual = first.getNivel();

        ArrayList<String> complement = new ArrayList<>();
        complement.add(first.getNombre());
        createFolder(getTotalPath(complement));

        for (int i = 2; i < this.cantFiles; i++) {
            FileInfo file = processedFiles.get(i);

            if (file.getTipo() == "directorio") {
                if (file.getNivel() > nivelActual) {
                    complement.add(file.getNombre());
                    nivelActual = file.getNivel();
                    createFolder(getTotalPath(complement));
                } else if (file.getNivel() < nivelActual) {
                    complement.remove(complement.size() - 1);
                    complement.remove(complement.size() - 1);
                    nivelActual = file.getNivel();
                    complement.add(file.getNombre());
                    createFolder(getTotalPath(complement));

                } else {
                    complement.remove(complement.size() - 1);
                    complement.add(file.getNombre());
                    nivelActual = file.getNivel();
                    createFolder(getTotalPath(complement));
                }
            } else {
                byte[] content = readBytes(file.getOffset(), file.getEndFile());
                createFile(getTotalPath(complement) + "\\" + file.getNombre(), content);
            }
        }

    }

    public String getTotalPath(ArrayList<String> complementos) {
        String aux = this.path;

        for (int i = 0; i < complementos.size(); i++) {
            aux += "\\" + complementos.get(i);
        }
        return aux;
    }

    public void createFile(String name, byte[] content) {
        try {
            File wFile = new File(name);
            if (wFile.createNewFile()) {
                Path path = wFile.toPath();
                try {
                    Files.write(path, content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public byte[] readBytes(int start, int end) {
        int positions = end - start;

        byte[] aux = new byte[positions];
        int j = 0;
        for (int i = start; i < end; i++) {
            aux[j] = this.fileBytes[i];
            j++;
        }

        return aux;
    }

    public FileInfo processFile(int entryOffset) {
        int i = 0;
        i += entryOffset;

        // If the start is "IX"
        if ((readByte(i) == 0x49) && (readByte(i + 1) == 0x58)) {
            int tipo = read2BytesInt(i + 2);
            // fileOffset == 0xcdcdcdcd es igual a directorio
            int fileOffset = read4BytesInt(i + 4);
            int fileSize = read4BytesInt(i + 8);

            // Omito la parte que no conozco
            // i += 16;

            String name = readName(i + 16);

            return new FileInfo(tipo, fileOffset, fileSize, name);
        }

        return null;
    }

    public String readName(int offset) {
        String name = "";

        name += read4BytesAscii(offset);
        name += read4BytesAscii(offset + 4);
        name += read4BytesAscii(offset + 8);
        name += read4BytesAscii(offset + 12);
        name += read4BytesAscii(offset + 16);

        return name;
    }

    public void readFileBytes(Path path) {
        try {
            this.fileBytes = Files.readAllBytes(path);
            System.out.println("Archivo leido con exito.");
        } catch (IOException e) {
            System.out.println("Error leyendo el archivo.");
            e.printStackTrace();
        }
    }

    public void extractHeaderData() {
        this.cantFiles = read4BytesInt(12);
        this.tableOffset = read4BytesInt(16);
        this.processedFiles = new ArrayList<>();
    }

    public String getMagicNumber() {
        String retorno = "";
        for (int i = 0; i < 11; i++) {
            retorno += byteToString(this.fileBytes[i]);
        }
        return retorno;
    }

    public int read4BytesBigEndian(int offset) {

        byte[] v = read4Bytes(offset);

        byteswap(v, 0, 3);
        byteswap(v, 1, 2);
        // return BitConverter.ToUInt32(v, 0);

        return 1;
    }

    public void byteswap(byte[] data, int left, int right) {
        byte t = data[left];
        data[left] = data[right];
        data[right] = t;
    }

    public byte[] read4Bytes(int offset) {
        byte[] v = new byte[4];

        int j = 0;
        for (int i = offset; i < offset + 4; i++) {
            v[j] = this.fileBytes[i];
            j++;
        }

        return v;
    }

    public int read2BytesInt(int offset) {
        int value = 0;
        for (int i = offset; i < offset + 2; i++) {
            value = (value << 8) + (this.fileBytes[i] & 0xFF);
        }

        return value;
    }

    public int read4BytesInt(int offset) {
        int value = 0;
        for (int i = offset; i < offset + 4; i++) {
            value = (value << 8) + (this.fileBytes[i] & 0xFF);
        }

        return value;
    }

    public void read8BytesInt(int offset) {

        byte[] buf = new byte[8];

        int j = 0;
        for (int i = offset; i < offset + 8; i++) {
            buf[j] = this.fileBytes[i];
            j++;
        }

        long l = ((buf[0] & 0xFFL) << 56) |
                ((buf[1] & 0xFFL) << 48) |
                ((buf[2] & 0xFFL) << 40) |
                ((buf[3] & 0xFFL) << 32) |
                ((buf[4] & 0xFFL) << 24) |
                ((buf[5] & 0xFFL) << 16) |
                ((buf[6] & 0xFFL) << 8) |
                ((buf[7] & 0xFFL) << 0);

        System.out.println(l);
    }

    public String byteToHex(byte var) {
        String st = String.format("%02X", var);
        return "0x" + st;
    }

    public String byteToString(byte var) {
        String st = String.format("%c", var);
        return st;
    }

    public byte readByte(int offset) {
        return this.fileBytes[offset];
    }

    public String bytesToString(byte[] var) {

        String aux = "";
        for (int i = 0; i < var.length; i++) {
            aux += String.format("%c", var[i]);
        }

        return aux;
    }

    public String read4BytesAscii(int offset) {
        String st = "";
        for (int i = offset; i < offset + 4; i++) {
            st += String.format("%c", this.fileBytes[i]);
        }
        return st;
    }

    public String createFolder(String name) {
        File theDir = new File(name);
        if (!theDir.exists()) {
            theDir.mkdirs();
        }
        return theDir.getAbsolutePath();
    }
}

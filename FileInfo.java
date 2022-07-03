public class FileInfo {
    private String tipo;
    private String formato;
    private int offset;
    private int nivel;
    private int tamanio;
    private String nombre;

    public FileInfo(int t, int offset, int tamanio, String nombre) {
        if (t == 1)
            this.tipo = "archivo";
        else
            this.tipo = "directorio";

        this.offset = offset;
        this.tamanio = tamanio;
        this.nombre = normalyzeName(nombre);
        this.nivel = extractLevel(nombre);

        if (t == 1)
            this.formato = extractFileFormat(this.nombre);
        else
            this.formato = "";

    }

    private int extractLevel(String name) {

        char c = name.charAt(1);
        if (c != ':') {
            int end = name.indexOf("_");

            String resultado = "";
            resultado = resultado + c;
            for (int i = 2; i < end; i++) {
                resultado += name.charAt(i);
            }
            return Integer.parseInt(resultado);
        } else
            return -1;
    }

    private String normalyzeName(String name) {
        // Largo 16
        char empty = '\0';

        String aux = "";

        int start = name.indexOf("_");
        // int i_offset = name.indexOf("_") != -1
        for (int i = start + 1; i < name.length(); i++) {
            if (name.charAt(i) != empty) {
                aux += name.charAt(i);
            }
        }
        return aux;
    }

    private String extractFileFormat(String name) {
        int start = name.indexOf(".");
        String format = "";
        for (int i = 1; i <= 3; i++) {
            char c = name.charAt(start + i);
            format += String.valueOf(c);
        }
        return format;
    }

    public int getNivel() {
        return nivel;
    }

    public String getNombre() {
        return nombre;
    }

    public int getOffset() {
        return offset;
    }

    public String getFormato() {
        return formato;
    }

    public int getTamanio() {
        return tamanio;
    }

    public String getTipo() {
        return tipo;
    }

    public int getEndFile() {
        return offset + tamanio;
    }

    @Override
    public String toString() {
        System.out.println("---------------------------");
        return "Tipo archivo: " + tipo + "\n" +
                "Formato: " + formato + "\n" +
                "Nombre: " + nombre + "\n" +
                "Nivel: " + getNivel() + "\n" +
                "Offset: " + offset + "\n" +
                "Tamaño: " + tamanio + "\n" +
                "Fin: " + getEndFile();
    }
}

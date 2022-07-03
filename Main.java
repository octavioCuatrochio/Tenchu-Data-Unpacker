public class Main {
    public static void main(String[] args) {

        Processor p = new Processor("DATA.VOL");
        p.analyzeFiles();
        p.extractFiles();
    }
}
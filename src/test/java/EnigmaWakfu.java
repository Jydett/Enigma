public class EnigmaWakfu {
    public static void main(String[] args) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process exec = rt.exec(new String[]{
                    "java",
                    "-jar",
                    "E:\\Java\\Enigma\\build\\libs\\enigma-0.14.2.jar"
            });
//            exec.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

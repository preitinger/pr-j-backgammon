package pr.control;

import java.io.IOException;

public class CubeTemplateSearcher extends TemplateSearcherOld {
    
    public CubeTemplateSearcher(int cubeVal) throws IOException {
        super(fileName(cubeVal), TemplateSearcherOld.DEFAULT_MIN_LIMIT, ".png");
    }

    private static String fileName(int cubeVal) {
        switch (cubeVal) {
            case 1: return "cubeEmpty";
            case 2: return "cube2";
            case 4: return "cube4";
            case 8: return "cube8";
            case 16: return "cube16";
            case 32: return "cube32";
            case 64: return "cube64";
        }
        return "";
    }
}

package cubiq.io;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DebugOutput {

    private final String fileLocation = "src/resources/debug/";

    public void printSchemes(List<int[][]> colorSchemes, String name) {
        List<String[][]> convertedSchemes = convertSchemeToString(colorSchemes);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileLocation + name + ".txt"));
            for (int i = 0; i < convertedSchemes.size(); i++) {
                for (int y = 0; y < 3; y++) {
                    for (int x = 0; x < 3; x++) {
                        bw.write(convertedSchemes.get(i)[x][y]);
                        if (x < 2) bw.write(", ");
                    }
                    if (y < 2) bw.write("\n");
                }
                if (i < 5) bw.write("\n\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printImage(Mat image, int index) {
        Imgproc.cvtColor(image, image, Imgproc.COLOR_HSV2BGR);
        Imgcodecs.imwrite(fileLocation + index + ".jpg", image);
    }

    private List<String[][]> convertSchemeToString(List<int[][]> colorSchemes) {
        List<String[][]> convertedSchemes = new ArrayList<>();
        for (int i = 0; i < colorSchemes.size(); i++) {
            convertedSchemes.add(new String[3][3]);
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    switch (colorSchemes.get(i)[x][y]) {
                        case 0: convertedSchemes.get(i)[x][y] = "WHITE ";
                        break;
                        case 1: convertedSchemes.get(i)[x][y] = "GREEN ";
                        break;
                        case 2: convertedSchemes.get(i)[x][y] = "RED   ";
                        break;
                        case 3: convertedSchemes.get(i)[x][y] = "ORANGE";
                        break;
                        case 4: convertedSchemes.get(i)[x][y] = "BLUE  ";
                        break;
                        case 5: convertedSchemes.get(i)[x][y] = "YELLOW";
                        break;
                    }
                }
            }
        }
        return convertedSchemes;
    }
}

package pr.backgammon.spin.control;

import java.awt.image.BufferedImage;
import java.io.IOException;

import pr.backgammon.model.Cube;
import pr.backgammon.model.Match;

public class ScanUtils {

    public static void scanCube(BufferedImage board, Match match, BoardSearchers bs, TemplateSearchers ts)
            throws IOException {
        Cube cube = match.cube;
        cube.used = false;
        cube.value = 1;
        cube.owner = -1;
        cube.offered = false;

        int midY = bs.cal.midY();

        // for (int i = 0; i < 7; ++i) {
        for (int i = 0; i < 7; ++i) {
            int cubeVal = 1 << i;

            var searcher = ts.cube(cubeVal);

            var pos = ts.search(searcher, board);

            if (pos != null) {
                cube.used = true;
                cube.value = cubeVal;

                if (cubeVal > 1) {
                    cube.used = true;

                    if (pos.y < midY - bs.cal.dy * 3) {
                        // opponent owns the cube
                        cube.owner = 1 - match.own;
                    } else {
                        // own player owns the cube
                        cube.owner = match.own;
                    }

                }
                return;
            }
        }

        // if (bs.cubeEmpty.run(board) != null) {
        //     cube.used = true;
        //     return;
        // }

        // Searcher[] searchers = {
        //         bs.cube2,
        //         bs.cube4,
        //         bs.cube8,
        //         bs.cube16,
        //         bs.cube32,
        //         bs.cube64,
        // };
        // int[] vals = {
        //         2,
        //         4,
        //         8,
        //         16,
        //         32,
        //         64,
        // };

        // for (int i = 0; i < searchers.length; ++i) {
        //     Point pos = searchers[i].run(board);
        //     if (pos != null) {
        //         cube.used = true;

        //         if (pos.y < midY - bs.cal.dy * 3) {
        //             // opponent owns the cube
        //             cube.owner = 1 - match.own;
        //         } else {
        //             // own player owns the cube
        //             cube.owner = match.own;
        //         }

        //         cube.value = vals[i];
        //         return;
        //     }
        // }
    }

}

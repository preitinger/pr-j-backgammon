package pr.backgammon.spin.control;

import java.awt.Point;
import java.awt.image.Raster;

import pr.backgammon.model.Cube;
import pr.backgammon.model.Match;
import pr.control.Searcher;

public class ScanUtils {

    public static void scanCube(Raster board, Match match, BoardSearchers bs) {
        Cube cube = match.cube;
        cube.used = false;
        cube.value = 1;
        cube.owner = -1;
        cube.offered = false;

        if (bs.cubeEmpty.run(board) != null) {
            cube.used = true;
            return;
        }

        Searcher[] searchers = {
                bs.cube2,
                bs.cube4,
                bs.cube8,
                bs.cube16,
                bs.cube32,
        };
        int[] vals = {
                2,
                4,
                8,
                16,
                32,
        };

        int midY = bs.cal.midY();

        for (int i = 0; i < searchers.length; ++i) {
            Point pos = searchers[i].run(board);
            if (pos != null) {
                cube.used = true;
                
                if (pos.y < midY - bs.cal.dy * 3) {
                    // opponent owns the cube
                    cube.owner = 1 - match.own;
                } else {
                    // own player owns the cube
                    cube.owner = match.own;
                }

                cube.value = vals[i];
                return;
            }
        }
    }
    
}

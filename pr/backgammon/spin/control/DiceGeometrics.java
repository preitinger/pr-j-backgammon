package pr.backgammon.spin.control;

import java.awt.Rectangle;

public class DiceGeometrics {
        public DiceGeometrics(CalibrationForSpin cal) {
            this.cal = cal;
            screenshotX = cal.left + cal.dxLeft * 2 - cal.dxLeft / 2 - cal.dxLeft / 4 + cal.dxLeft / 32;
            screenshotY = cal.top + cal.dy * 5 - 2 * cal.dy / 8;
        }

        private final int screenshotX, screenshotY;

        public void screenshot(Rectangle r) {
            r.x = screenshotX;
            r.y = cal.top + cal.dy * 5 - 2 * cal.dy / 8;
            int right = cal.right - cal.dxRight * 2 + cal.dxRight / 2 + cal.dxRight / 4 - cal.dxRight / 32;
            r.width =  right - r.x;
            int bottom = cal.bottom - cal.dy * 5 + 2 * cal.dy / 8;
            r.height = bottom - r.y;
        }

        public void initialRoll(Rectangle resLeft, Rectangle resRight) {
            resLeft.x = cal.left + cal.dxLeft * 2;
            resLeft.y = cal.top + cal.dy * 5 - 2 * cal.dy / 8;
            resLeft.width = cal.dxLeft;
            resLeft.height = cal.bottom - cal.dy * 5 + 2 * cal.dy / 8 - resLeft.y;

            resRight.x = cal.right - cal.dxRight * 3;
            resRight.width = cal.dxRight;
            resRight.y = resLeft.y;
            resRight.height = resLeft.height;
            
            resLeft.x -= screenshotX;
            resRight.x -= screenshotX;
            resLeft.y -= screenshotY;
            resRight.y -= screenshotY;
        }

        public void oppRoll(Rectangle resLeft, Rectangle resRight) {
            resLeft.x = cal.left + cal.dxLeft * 2 - cal.dxLeft / 2 - cal.dxLeft / 4 + cal.dxLeft / 32 ;
            // resLeft.y = cal.top + cal.dy * 5 - 2 * cal.dy / 8;
            resLeft.y = cal.top + cal.dy * 5 - 2 * cal.dy / 8 - 5;
            resLeft.width = cal.dxLeft;
            // resLeft.height = cal.bottom - cal.dy * 5 + 2 * cal.dy / 8 - resLeft.y;
            resLeft.height = cal.bottom - cal.dy * 5 + 2 * cal.dy / 8 - resLeft.y + 10;

            resRight.x = cal.left + cal.dxLeft * 3 - 5 * cal.dxLeft / 16;
            resRight.width = cal.dxRight;
            resRight.y = resLeft.y;
            resRight.height = resLeft.height;
            
            resLeft.x -= screenshotX;
            resRight.x -= screenshotX;
            resLeft.y -= screenshotY;
            resRight.y -= screenshotY;
        }

        public void ownRoll(Rectangle resLeft, Rectangle resRight) {
            resRight.x = cal.right - cal.dxRight * 2 + cal.dxRight / 2 + cal.dxRight / 4 - cal.dxRight / 32 - cal.dxRight ;
            // resRight.y = cal.top + cal.dy * 5 - 2 * cal.dy / 8;
            resRight.y = cal.top + cal.dy * 5 - 2 * cal.dy / 8 - 5;
            resRight.width = cal.dxRight;
            // resRight.height = cal.bottom - cal.dy * 5 + 2 * cal.dy / 8 - resRight.y;
            resRight.height = cal.bottom - cal.dy * 5 + 2 * cal.dy / 8 - resRight.y + 10;

            resLeft.x = cal.right - cal.dxRight * 3 + 5 * cal.dxRight / 16 - cal.dxRight;
            resLeft.width = cal.dxRight;
            resLeft.y = resRight.y;
            resLeft.height = resRight.height;
            
            resLeft.x -= screenshotX;
            resRight.x -= screenshotX;
            resLeft.y -= screenshotY;
            resRight.y -= screenshotY;
        }
    
        private CalibrationForSpin cal;
}

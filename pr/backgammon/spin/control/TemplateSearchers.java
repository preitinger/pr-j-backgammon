package pr.backgammon.spin.control;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import pr.backgammon.spin.templatesearchers.BAufgeben;
import pr.backgammon.spin.templatesearchers.BSchliessen;
import pr.backgammon.spin.templatesearchers.BVerlassen;
import pr.backgammon.spin.templatesearchers.Bereit;
import pr.backgammon.spin.templatesearchers.ChequerBlack;
import pr.backgammon.spin.templatesearchers.ChequerBlackStar;
import pr.backgammon.spin.templatesearchers.ChequerWhite;
import pr.backgammon.spin.templatesearchers.ChequerWhiteStar;
import pr.backgammon.spin.templatesearchers.Cube16;
import pr.backgammon.spin.templatesearchers.Cube2;
import pr.backgammon.spin.templatesearchers.Cube32;
import pr.backgammon.spin.templatesearchers.Cube4;
import pr.backgammon.spin.templatesearchers.Cube64;
import pr.backgammon.spin.templatesearchers.Cube8;
import pr.backgammon.spin.templatesearchers.CubeEmpty;
import pr.backgammon.spin.templatesearchers.DlgAnnehmen;
import pr.backgammon.spin.templatesearchers.DlgAufgeben;
import pr.backgammon.spin.templatesearchers.DlgCorner;
import pr.backgammon.spin.templatesearchers.DlgJa;
import pr.backgammon.spin.templatesearchers.DlgNein;
import pr.backgammon.spin.templatesearchers.DlgResignFromOpp1;
import pr.backgammon.spin.templatesearchers.DlgResignFromOpp2;
import pr.backgammon.spin.templatesearchers.DlgResignFromOpp3;
import pr.backgammon.spin.templatesearchers.DlgResignFromOwn1;
import pr.backgammon.spin.templatesearchers.DlgResignFromOwn2;
import pr.backgammon.spin.templatesearchers.DlgResignFromOwn3;
import pr.backgammon.spin.templatesearchers.DlgVerdoppelnOpp;
import pr.backgammon.spin.templatesearchers.EnterBlack;
import pr.backgammon.spin.templatesearchers.EnterWhite;
import pr.backgammon.spin.templatesearchers.LinkBackgammon;
import pr.backgammon.spin.templatesearchers.NeuesSpiel;
import pr.backgammon.spin.templatesearchers.PlayerBoxBlackReady;
import pr.backgammon.spin.templatesearchers.PlayerBoxWhiteReady;
import pr.backgammon.spin.templatesearchers.SpielAnlegen;
import pr.backgammon.spin.templatesearchers.Spiele;
import pr.backgammon.spin.templatesearchers.StatusHasRejected;
import pr.backgammon.spin.templatesearchers.UnsichtbaresSpiel;
import pr.control.CubeTemplateSearcher;
import pr.control.MyRobot;
import pr.control.TemplateSearcher;

public class TemplateSearchers {
    // private TemplateSearcherOld playerBoxWhiteSearcher;
    // private TemplateSearcherOld playerBoxBlackSearcher;
    // private TemplateSearcherOld playerBoxWhiteReadySearcher;
    // private TemplateSearcherOld playerBoxBlackReadySearcher;
    private final Map<Integer, CubeTemplateSearcher> cube = new HashMap<>();
    // private TemplateSearcherOld verdoppelnOpp;

    public final Spiele spiele = new Spiele();
    public final LinkBackgammon linkBackgammon = new LinkBackgammon();
    public final NeuesSpiel neuesSpiel = new NeuesSpiel();
    public final UnsichtbaresSpiel unsichtbaresSpiel = new UnsichtbaresSpiel();
    public final SpielAnlegen spielAnlegen = new SpielAnlegen();
    public final EnterWhite enterWhite = new EnterWhite();
    public final EnterBlack enterBlack = new EnterBlack();
    public final Bereit bereit = new Bereit();
    public final BVerlassen bVerlassen = new BVerlassen();
    public final BSchliessen bSchliessen = new BSchliessen();
    public final BAufgeben bAufgeben = new BAufgeben();
    public final CubeEmpty cubeEmpty = new CubeEmpty();
    public final Cube2 cube2 = new Cube2();
    public final Cube4 cube4 = new Cube4();
    public final Cube8 cube8 = new Cube8();
    public final Cube16 cube16 = new Cube16();
    public final Cube32 cube32 = new Cube32();
    public final Cube64 cube64 = new Cube64();
    public final DlgCorner dlgCorner = new DlgCorner();
    public final DlgAnnehmen dlgAnnehmen = new DlgAnnehmen();
    public final DlgAufgeben dlgAufgeben = new DlgAufgeben();
    public final DlgVerdoppelnOpp dlgVerdoppelnOpp = new DlgVerdoppelnOpp();
    public final DlgResignFromOpp1 dlgResignFromOpp1 = new DlgResignFromOpp1();
    public final DlgResignFromOpp2 dlgResignFromOpp2 = new DlgResignFromOpp2();
    public final DlgResignFromOpp3 dlgResignFromOpp3 = new DlgResignFromOpp3();
    public final DlgJa dlgJa = new DlgJa();
    public final DlgNein dlgNein = new DlgNein();
    public final DlgResignFromOwn1 dlgResignFromOwn1 = new DlgResignFromOwn1();
    public final DlgResignFromOwn2 dlgResignFromOwn2 = new DlgResignFromOwn2();
    public final DlgResignFromOwn3 dlgResignFromOwn3 = new DlgResignFromOwn3();
    // public final PlayerBoxWhiteReady playerBoxWhite = new PlayerBoxWhiteReady();
    public final PlayerBoxWhiteReady playerBoxWhiteReady = new PlayerBoxWhiteReady();
    // public final PlayerBoxBlack playerBoxWhite = new PlayerBoxWhiteReady();
    public final PlayerBoxBlackReady playerBoxBlackReady = new PlayerBoxBlackReady();
    public final StatusHasRejected statusHasRejected = new StatusHasRejected();
    public final ChequerWhite chequerWhite = new ChequerWhite();
    public final ChequerWhiteStar chequerWhiteStar = new ChequerWhiteStar();
    public final ChequerBlack chequerBlack = new ChequerBlack();
    public final ChequerBlackStar chequerBlackStar = new ChequerBlackStar();
    
    public final Rectangle wholeScreen;

    public TemplateSearchers(Rectangle wholeScreen) throws IOException {
        this.wholeScreen = wholeScreen;
        // for (int i = 0; i < 7; ++i) {
        // int cubeVal = 1 << i;
        // cube.put(cubeVal, new CubeTemplateSearcher(cubeVal));
        // }
    }

    // public TemplateSearcherOld cube(int cubeVal) throws IOException {
    // var searcher = cube.get(cubeVal);
    // if (searcher == null) {
    // cube.put(cubeVal, searcher = new CubeTemplateSearcher(cubeVal));
    // }
    // return searcher;
    // }

    public TemplateSearcher cube(int cubeVal) throws IOException {
        switch (cubeVal) {
            case 1:
                return cubeEmpty;
            case 2:
                return cube2;
            case 4:
                return cube4;
            case 8:
                return cube8;
            case 16:
                return cube16;
            case 32:
                return cube32;
            case 64:
                return cube64;
        }
        return null;
    }

    // public TemplateSearcherOld playerBoxWhite() throws IOException {
    // if (playerBoxWhiteSearcher == null) {
    // playerBoxWhiteSearcher = new TemplateSearcherOld("playerBoxWhiteComplete",
    // TemplateSearcherOld.DEFAULT_MIN_LIMIT, ".png");
    // }

    // return playerBoxWhiteSearcher;
    // }

    // public TemplateSearcherOld playerBoxBlack() throws IOException {
    // if (playerBoxBlackSearcher == null) {
    // playerBoxBlackSearcher = new TemplateSearcherOld("playerBoxBlackComplete",
    // TemplateSearcherOld.DEFAULT_MIN_LIMIT, ".png");
    // }

    // return playerBoxBlackSearcher;
    // }

    // public TemplateSearcherOld playerBoxWhiteReady() throws IOException {
    // if (playerBoxWhiteReadySearcher == null) {
    // playerBoxWhiteReadySearcher = new
    // TemplateSearcherOld("playerBoxWhiteReadyComplete");
    // }

    // return playerBoxWhiteReadySearcher;
    // }

    // public TemplateSearcherOld playerBoxBlackReady() throws IOException {
    // if (playerBoxBlackReadySearcher == null) {
    // playerBoxBlackReadySearcher = new
    // TemplateSearcherOld("playerBoxBlackReadyComplete");
    // }

    // return playerBoxBlackReadySearcher;
    // }

    // public TemplateSearcherOld verdoppelnOpp() throws IOException {
    // if (verdoppelnOpp == null) {
    // verdoppelnOpp = new TemplateSearcherOld("verdoppelnOpp",
    // TemplateSearcherOld.DEFAULT_MIN_LIMIT, ".png");
    // }

    // return verdoppelnOpp;
    // }

    // private TemplateSearcherOld annehmenUnpressed;

    // public TemplateSearcherOld annehmenUnpressed() throws IOException {
    // if (annehmenUnpressed == null) {
    // annehmenUnpressed = new TemplateSearcherOld("annehmenUnpressed",
    // TemplateSearcherOld.DEFAULT_MIN_LIMIT,
    // ".png");
    // }

    // return annehmenUnpressed;
    // }

    // private TemplateSearcherOld aufgebenUnpressed;

    // public TemplateSearcherOld aufgebenUnpressed() throws IOException {
    // if (aufgebenUnpressed == null) {
    // aufgebenUnpressed = new TemplateSearcherOld("aufgebenUnpressed",
    // TemplateSearcherOld.DEFAULT_MIN_LIMIT,
    // ".png");
    // }

    // return aufgebenUnpressed;
    // }

    public int searchOppResign(BufferedImage board) {
        if (visible(dlgResignFromOpp1, board))
            return 1;
        if (visible(dlgResignFromOpp2, board))
            return 2;
        if (visible(dlgResignFromOpp3, board))
            return 3;
        return 0;
    }

    // private TemplateSearcherOld resignFromOpp1;

    // public TemplateSearcherOld resignFromOpp1() throws IOException {
    // if (resignFromOpp1 == null) {
    // resignFromOpp1 = new TemplateSearcherOld("resignFromOpp1",
    // TemplateSearcherOld.DEFAULT_MIN_LIMIT, ".png");
    // }

    // return resignFromOpp1;
    // }

    // private TemplateSearcherOld resignFromOpp2;

    // public TemplateSearcherOld resignFromOpp2() throws IOException {
    // if (resignFromOpp2 == null) {
    // resignFromOpp2 = new TemplateSearcherOld("resignFromOpp2",
    // TemplateSearcherOld.DEFAULT_MIN_LIMIT, ".png");
    // }

    // return resignFromOpp2;
    // }

    // private TemplateSearcherOld resignFromOpp3;

    // public TemplateSearcherOld resignFromOpp3() throws IOException {
    // if (resignFromOpp3 == null) {
    // resignFromOpp3 = new TemplateSearcherOld("resignFromOpp3",
    // TemplateSearcherOld.DEFAULT_MIN_LIMIT, ".png");
    // }

    // return resignFromOpp3;
    // }

    //
    // utility functions
    //

    private Point pos = null;

    public boolean visible(TemplateSearcher s, BufferedImage board) {
        return search(s, board) != null;
    }

    public Point search(TemplateSearcher s, BufferedImage board) {
        Point found = s.run(board, pos, false);
        if (found != null) {
            pos = found;
        }

        return found;
    }

    public void waitAndClick(BoardSearchers bs, TemplateSearcher s) throws InterruptedException {
        int n = 0;
        Point found = null;

        do {
            found = s.run(bs.boardShot(), pos, false);
            System.out.println("found: " + found);
            if (found != null) {
                pos = found;
                break;
            }

            if (++n % 20 == 0) {
                System.out.println("Searching " + s.name + " ... (" + n + " tries)");
            }
            Thread.sleep(100);
        } while (true);

        System.out.println("boardRect: " + bs.boardRect());
        MyRobot.click(bs.boardRect().x + found.x, bs.boardRect().y + found.y, s.width(), s.height());
    }

}

package pr.backgammon.control;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

public class AnalyzeMatch {
    public static final String OUT_PATH = "../pr-home/public/gnubg";
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            usage();
            return;
        }

        String fileName = args[0];
        File file = new File(fileName);

        if (!file.isFile()) {
            System.err.println("Could not find file '" + fileName + "'.");
            return;
        }

        BufferedWriter w = new BufferedWriter(new FileWriter(file, true));

        String nameWithoutExtension = file.getName();
        if (nameWithoutExtension.endsWith(".gnubg")) {
            nameWithoutExtension = nameWithoutExtension.substring(0, nameWithoutExtension.length() - ".gnubg".length());
        }
        
        try {
            w.newLine();
            w.newLine();
            w.newLine();
            w.append("# Appended by pr.backgammon.control.AnalyzeMatch:");
            w.newLine();
            w.newLine();
            w.append("analyze match");
            w.newLine();
            w.append("export match html ").append(OUT_PATH).append('/').append(nameWithoutExtension).append(".html");
            w.newLine();
        } finally {
            w.close();
        }
        
        var proc1 = new ProcessBuilder("gnubg", "-tc", file.getAbsolutePath()).start();
        forwardProcOutput(proc1);
        int code = proc1.waitFor();
        System.out.println("Exit code von proc1: " + code);

        if (code != 0) {
            System.exit(code);
        }

        var createIndexOfPublishedMatches = new CreateIndexOfPublishedMatches(OUT_PATH);
        createIndexOfPublishedMatches.run();
        
        if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(null, "pr-home gleich testen?", "AnalyzeMatch", JOptionPane.YES_NO_OPTION)) {
            return;
        }

        var proc2 = new ProcessBuilder("gnome-terminal",  "--profile=keep-open",  "--title=Bitte testen und dann mit Strg-C abbrechen", "--wait", "--", "/bin/sh", "-c",  "cd /home/peter/my_projects/individual-gits/pr-home; npm run incVersionsBuildStart").start();
        forwardProcOutput(proc2);
        code = proc2.waitFor();
        
        if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(null, "Nun gleich ./scripts/commit-matches.sh ausführen?", "AnalyzeMatch", JOptionPane.YES_NO_OPTION)) {
            return;
        }
        var proc3 = new ProcessBuilder("/bin/sh", "./scripts/commit-matches.sh").start();
        forwardProcOutput(proc3);
        code = proc3.waitFor();
        System.out.println("Exit code of comit-matches.sh: " + code);
        System.exit(code);
        
    }

    private static void usage() {
        System.err.println("usage: java pr.backgammon.control.AnalyzeMatch <gnubg file>");
    }

    private static void forwardProcOutput(Process p) throws IOException {
        InputStreamReader r = new InputStreamReader(p.getInputStream());
        int c;
        while ((c = r.read()) != -1) {
            System.out.write(c);
        }
    }
}

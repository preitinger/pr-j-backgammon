package pr.backgammon.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import pr.control.Tools;

public class CreateIndexOfPublishedMatches {
    private final String dir;

    public CreateIndexOfPublishedMatches(String dir) {
        this.dir = dir;
    }

    public void run() {
        try {
            File f = new File(dir);
            String[] matches = f.list(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    if ("index.html".equals(name))
                        return false;
                    if (name.endsWith(".pdf")) {
                        return true;
                    }
                    if (!name.endsWith(".html"))
                        return false;
                    int len = name.length();
                    if (len <= 9)
                        return false;

                    if (name.charAt(len - 9) != '_')
                        return true;
                    for (int i = len - 8; i <= len - 6; ++i) {
                        var c = name.charAt(i);
                        if (c >= '0' && c <= '9') {
                            continue;
                        } else {
                            return true;
                        }
                    }

                    return false;
                }

            });

            Arrays.sort(matches);

            var sb = new StringBuilder();

            for (var match : matches) {
                sb.append("<li><a href='").append(match).append("'>").append(match).append("</a></li>\n");
            }

            System.out.println("sb: \n" + sb);

            String all = replaceIndented(loadTemplate(), "${MATCHES}", sb.toString());
            var writer = new FileWriter(dir + "/index.html");
            writer.write(all);
            writer.close();
            System.out.println("result:\n" + all);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String replaceIndented(String template, String var, String linesForVar) {
        int pos = template.indexOf(var);
        if (pos == -1) {
            throw new IllegalArgumentException("Arg var not found in arg template");
        }

        int oldPos = pos;

        while (pos > 0 && template.charAt(pos - 1) != '\n')
            --pos;
        System.out.println("oldPos " + oldPos + "   pos " + pos);

        var indent = template.substring(pos, oldPos);

        StringBuilder sb = new StringBuilder();
        sb.append(template.substring(0, pos));

        String[] newLines = linesForVar.split("\n");

        int n = newLines.length;

        for (int i = 0; i < n; ++i) {
            var newLine = newLines[i];
            System.out.println("newLine: " + newLine);
            sb.append(indent);
            sb.append(newLine);
            if (i + 1 < n) {
                sb.append('\n');
            }
        }

        sb.append(template.substring(oldPos + var.length()));
        return sb.toString();
    }

    private String loadTemplate() throws IOException {
        BufferedReader r = new BufferedReader(
                new InputStreamReader(Tools.readResourceFile("pr/res/gnubg/templateIndexOfPublishedMatches.txt")));
        var sb = new StringBuilder();
        r.lines().forEach(line -> sb.append(line).append('\n'));
        r.close();
        return sb.toString();
    }
}

package pr.backgammon.spin.trackmove;

import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import pr.backgammon.spin.control.CalibrationForSpin;
import pr.backgammon.spin.control.TemplateSearchers;

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.start();
    }

    void start() {
        SwingUtilities.invokeLater(() -> {
            view = new View(onCalibrate, onStart, onStop);
            view.setSize(1500, 1000);
            view.setVisible(true);
        });

    }

    final ArrayList<Data> data = new ArrayList<>(128);
    View view = null;
    CalibrationForSpin cal = null;
    Worker worker = null;

    final ActionListener onCalibrate = (e) -> {
        try {
            cal = new CalibrationForSpin();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    };

    final ActionListener onStart = (e) -> {
        if (cal == null) {
            System.out.println("Not calibrated");
            return;
        }

        data.clear();

        try {
            worker = new Worker(cal, new TemplateSearchers(new Rectangle(0, 0, 1600, 1000))) {

                @Override
                protected void process(List<Data> chunks) {
                    System.out.println("chunks.size() " + chunks.size());
                    data.addAll(chunks);
                    view.setData(data);
                }

            };
            worker.execute();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    };

    final ActionListener onStop = (e) -> {
        if (worker == null) {
            System.out.println("Not running");
            return;
        }

        System.out.println("canceling");
        worker.cancel(true);
        worker = null;

    };
}

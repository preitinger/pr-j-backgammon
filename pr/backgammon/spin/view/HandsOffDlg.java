package pr.backgammon.spin.view;

import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.Timer;

public class HandsOffDlg extends JDialog {
    private final JButton ok, cancel;
    private final AbstractAction onOkAction, onCancelAction;
    private final Timer timer;
    private static final int SECONDS = 5;

    public HandsOffDlg(Window parent, Runnable onOk, Runnable onCancel) {
        super(parent, "Hands off!", ModalityType.DOCUMENT_MODAL);
        setLocationRelativeTo(parent);
        onOkAction = new AbstractAction("OK (automatisch in " + SECONDS + "s)") {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                ok.setVisible(false);
                timer.stop();
                onOk.run();

            }
        };
        onCancelAction = new AbstractAction("Abbrechen") {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.stop();
                onCancel.run();
            }
        };
        ok = new JButton(onOkAction);
        cancel = new JButton(onCancelAction);
        getRootPane().setDefaultButton(ok);
        timer = new Timer(1000, new ActionListener() {
            int count = SECONDS;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (--count > 0) {
                    ok.setText("OK (automatisch in " + count + "s)");
                } else {
                    ok.setVisible(false);
                    timer.stop();
                    onOk.run();
                }
            }
        });
        timer.start();
        setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        add(ok);
        add(cancel);
    }
}

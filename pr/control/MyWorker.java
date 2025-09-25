package pr.control;

import javax.swing.SwingWorker;

public abstract class MyWorker<T, V> extends SwingWorker<T, V> {

    public abstract void resultOnEventDispatchThread(T result);

    @Override
    protected void done() {
        try {
            if (!isDone()) {
                throw new IllegalStateException("background task not done when SwingWorker.done() is called?!");
            }
            if (!isCancelled()) {
                this.resultOnEventDispatchThread(get());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected final T doInBackground() throws Exception {
        return doIt();
    }

    public abstract T doIt() throws Exception;
}

package pr.control;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

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

    public final void join() throws InterruptedException {
        try {
            get();
        } catch (ExecutionException e) {
            // Catch silently because we are not interested in the exact result.
            // join() is called to give short pending actions, as save small data to a storage, time before the application is exited.
        } catch (CancellationException e) {
            // Catch silently because we are not interested in the exact result.
            // join() is called to give short pending actions, as save small data to a storage, time before the application is exited.
        }
    }
}

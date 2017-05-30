package vandy.mooc.assignments.assignment.downloader;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * A HaMeR downloader implementation that uses two Runnables and a background
 * thread to download a single image in a background thread.
 * <p/>
 * The base ImageDownloader class provides helper methods to perform the
 * download operation as well as to return the resulting image bitmap to the
 * framework where it will be displayed in a layout ImageView.
 */
public class HaMeRDownloader extends ImageDownloader {
    /**
     * Logging tag.
     */
    private static final String TAG = "HaMeRDownloader";

    /**
     * Create a new handler that is associated with the main thread looper.
     */
    // Create a private final Handler associated with the main
    // thread looper. Note that this class and all its fields are instantiated
    // in the main thread.
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * A reference to the background thread to support the cancel hook.
     */
    private Thread mThread;

    /**
     * Starts the asynchronous download request.
     */
    @Override
    public void execute() {
        Log.i("TRACE", "Within execute");
        // Create a new final Runnable called 'downloadRunnable' to
        // process the download request (replace the null).
        final Runnable downloadRunnable = new Runnable() {

            // Within the new runnable's run() method:
            @Override
            public void run() {

                // Call the HaMeRDownloader helper method to
                // determine if this thread has been interrupted; if so,
                // just return to terminate the thread.
                if (isInterrupted()) {
                    cancel();
                    return;
                }

                // Call ImageDownloader super class helper method
                // to download the bitmap.
                Bitmap bitMap = download();

                // Since the download my take a while, check again to
                // make sure that this thread has not been interrupted (using
                // the same helper method as above); if it has been interrupted
                // then just return to terminate the thread.
                if (isInterrupted()) {
                    cancel();
                    return;
                }

                // Use the mHandler post(...) helper method to post
                // a new Runnable to the main thread. This Runnable's run()
                // method should simply call the ImageDownloader super class
                // helper method postResult(...) to pass the downloaded bitmap
                // to the application UI layer (activity) to display.
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        postResult(bitMap);
                    }
                };

                mHandler.post(runnable);
            }
        };

        // Create a new Thread instance that will run the
        // 'downloadRunnable' created above, and assign it to the mThread
        // field. This assignment is necessary to support cancelling this
        // thread and the download operation.
        mThread = new Thread(downloadRunnable);

        // Start the thread.
        mThread.start();
    }

    /**
     * Cancels the current download operation.
     */
    @Override
    public void cancel() {
        // Call local isRunning() helper method to check if mThread
        // is currently running; if it is running, cancel it by calling its
        // interrupt() helper method.
        if (this.isRunning())
            mThread.interrupt();
    }

    /**
     * Checks to see if the download thread has been interrupted.
     *
     * @return {@code true} if the calling thread has been interrupted; {@code
     * false} if not.
     */
    private boolean isInterrupted() {
        // return 'true' if mThread is not null and has been
        // interrupted (see isInterrupted() helper method).
        if (mThread == null)
            return true;
        else
            if (mThread.isInterrupted())
                return true;
            else
                return false;
    }

    /**
     * Reports if the download thread is currently running.
     *
     * @return {@code true} if the thread is running; {@code false} if not.
     */
    @Override
    public boolean isRunning() {
        // Return 'true' if mThread is not null and is running
        // (see isAlive() helper method)
        if (mThread == null)
            return false;
        else
            if (mThread.isAlive())
                return true;
            else
                return false;
    }

    /**
     * Reports if the download thread has been cancelled.
     *
     * @return {@code true} if the thread has cancelled ; {@code false} if not.
     */
    @Override
    public boolean isCancelled() {
        // Return 'true' if mThread is not null and has been
        // interrupted (see isInterrupted() helper method).
        if (mThread == null)
            return true; // No thread running
        else
            if (mThread.isInterrupted())
                return true;
            else
                return false;

    }

    /**
     * Reports if the download thread has completed.
     *
     * @return {@code true} if the thread has successfully completed; {@code
     * false} if not.
     */
    @Override
    public boolean hasCompleted() {
        // Return 'true' if mThread is not null and has successfully
        // terminated (completed). To determine if a thread has terminated,
        // you will need to use the Thread's getState() helper method and
        // compare it to the the appropriate Thread.State enumerated value.
        if (mThread == null)
            return false;
        else
            if (mThread.getState().equals(Thread.State.TERMINATED) || !this.isRunning())
                return true;
            else
                return false;
    }
}

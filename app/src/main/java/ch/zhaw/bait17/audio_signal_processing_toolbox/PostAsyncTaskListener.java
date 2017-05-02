package ch.zhaw.bait17.audio_signal_processing_toolbox;

/**
 * Callback for AsyncTask.
 *
 * @author georgrem, stockan1
 */

public interface PostAsyncTaskListener<T> {
    // T is the type of the result object of the async task
    void onPostAsyncTask(T result);
}

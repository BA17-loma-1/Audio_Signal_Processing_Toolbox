package ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter;

import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * <p>
 *     Filter interface
 * </p>
 * @author georgrem, stockan1
 */

public interface Filter extends Parcelable {

    short[] apply(@NonNull short[] input);

    int getOrder();

}

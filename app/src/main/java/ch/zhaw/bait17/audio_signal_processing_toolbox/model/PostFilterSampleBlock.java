package ch.zhaw.bait17.audio_signal_processing_toolbox.model;

/**
 * <p>
 *     This class is used to pass PCM sample blocks between fragments.
 *     EventBus is used to simplify communication by implementing the publish/subscriber pattern.
 *     Source: http://greenrobot.org/eventbus
 * </p>
 *
 * @author georgrem, stockan1
 */
public class PostFilterSampleBlock extends PCMSampleBlock {

    public PostFilterSampleBlock(short[] samples, final int sampleRate) {
        super(samples, sampleRate);
    }

}

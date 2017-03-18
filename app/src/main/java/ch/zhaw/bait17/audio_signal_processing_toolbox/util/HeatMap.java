package ch.zhaw.bait17.audio_signal_processing_toolbox.util;

/**
 * @author georgrem, stockan1
 */
public class HeatMap {

    private final int NUMBER_OF_STEPS = 255;
    private final int ALPHA_FULL_TRANSPARENCY = 0;
    private final int ALPHA_NO_TRANSPARENCY = 0xff;

    private final Colour YELLOW = new Colour(0xff, 0xff, 0x00, ALPHA_NO_TRANSPARENCY);
    private final Colour ORANGE = new Colour(0xff, 0xa5, 0x00, ALPHA_NO_TRANSPARENCY);
    private final Colour RED    = new Colour(0xff, 0x00, 0x00, ALPHA_NO_TRANSPARENCY);
    private final Colour GREEN  = new Colour(0x00, 0x80, 0x00, ALPHA_NO_TRANSPARENCY);
    private final Colour BLUE   = new Colour(0x00, 0x00, 0xff, ALPHA_NO_TRANSPARENCY);
    private final Colour BLACK  = new Colour(0x00, 0x00, 0x00, ALPHA_NO_TRANSPARENCY);
    private final Colour WHITE  = new Colour(0xff, 0xff, 0xff, ALPHA_NO_TRANSPARENCY);

    public final Colour[] GREY_SCALE = Gradient.createGradient(WHITE, BLACK, NUMBER_OF_STEPS);

    public final Colour[] YELLOW_ORANGE_BLUE = Gradient.createMultiGradient(
            new Colour[]{YELLOW, ORANGE, BLUE}, NUMBER_OF_STEPS);

}

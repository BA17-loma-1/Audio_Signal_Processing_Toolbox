package ch.zhaw.bait17.audio_signal_processing_toolbox.util;

/**
 * <p>
 *     Helper class containing predefined heat maps in the form of colour gradients.
 *     These heat maps can be used to plot magnitudes in a spectrogram.
 * </p>
 *
 * @author georgrem, stockan1
 */
public final class HeatMap {

    private static final int NUMBER_OF_STEPS = 256;
    private static final int ALPHA_FULL_TRANSPARENCY = 0;
    private static final int ALPHA_NO_TRANSPARENCY = 0xff;

    private static final Colour YELLOW = new Colour(0xff, 0xff, 0x00, ALPHA_NO_TRANSPARENCY);
    private static final Colour ORANGE = new Colour(0xff, 0xa5, 0x00, ALPHA_NO_TRANSPARENCY);
    private static final Colour RED    = new Colour(0xff, 0x00, 0x00, ALPHA_NO_TRANSPARENCY);
    private static final Colour GREEN  = new Colour(0x00, 0x80, 0x00, ALPHA_NO_TRANSPARENCY);
    private static final Colour BLUE   = new Colour(0x00, 0x00, 0xff, ALPHA_NO_TRANSPARENCY);
    private static final Colour LILA   = new Colour(181, 32, 0xff, ALPHA_NO_TRANSPARENCY);
    private static final Colour BLACK  = new Colour(0x00, 0x00, 0x00, ALPHA_NO_TRANSPARENCY);
    private static final Colour WHITE  = new Colour(0xff, 0xff, 0xff, ALPHA_NO_TRANSPARENCY);

    public static final Colour[] GREY_SCALE = Gradient.createGradient(WHITE, BLACK, NUMBER_OF_STEPS);

    public static final Colour[] RAINBOW = Gradient.createMultiGradient(
            new Colour[]{BLACK, BLUE, LILA, RED, ORANGE, YELLOW, WHITE}, 2 * NUMBER_OF_STEPS);

    public static final Colour[] HOT_1 = Gradient.createMultiGradient(
            new Colour[] {WHITE, YELLOW, ORANGE, RED, GREEN, BLUE}, NUMBER_OF_STEPS);

    public static final Colour[] HOT_2 = Gradient.createMultiGradient(
            new Colour[] {WHITE, YELLOW, ORANGE, RED}, NUMBER_OF_STEPS);

    public static final Colour[] COOL_1 = Gradient.createGradient(BLUE, YELLOW, NUMBER_OF_STEPS);

    public static final Colour[] COOL_2 = Gradient.createGradient(BLUE, WHITE, NUMBER_OF_STEPS);
}

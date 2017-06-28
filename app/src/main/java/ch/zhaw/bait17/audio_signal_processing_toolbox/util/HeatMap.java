package ch.zhaw.bait17.audio_signal_processing_toolbox.util;

/**
 * <p>
 *     Helper class containing predefined heat maps in the form of colour gradients. <br>
 *     These heat maps can be used to plot magnitudes in a spectrogram.
 * </p>
 *
 * @author georgrem, stockan1
 */
public final class HeatMap {

    private static final int NUMBER_OF_STEPS = 128;
    private static final int ALPHA_NO_TRANSPARENCY = 0xff;

    private static final Colour YELLOW = new Colour(0xff, 0xff, 0x00, ALPHA_NO_TRANSPARENCY);
    private static final Colour ORANGE = new Colour(0xff, 0xa5, 0x00, ALPHA_NO_TRANSPARENCY);
    private static final Colour RED = new Colour(0xff, 0x00, 0x00, ALPHA_NO_TRANSPARENCY);
    private static final Colour GREEN  = new Colour(0x00, 0x80, 0x00, ALPHA_NO_TRANSPARENCY);
    private static final Colour LIGHT_BLUE = new Colour(0x89, 0xcf, 0xf0, ALPHA_NO_TRANSPARENCY);
    private static final Colour DARK_BLUE = new Colour(0x00, 0x00, 0xff, ALPHA_NO_TRANSPARENCY);
    private static final Colour LILA = new Colour(181, 32, 0xff, ALPHA_NO_TRANSPARENCY);
    private static final Colour BLUE_GREY = new Colour(0x65, 0x99, 0xcc, ALPHA_NO_TRANSPARENCY);
    private static final Colour DARK_SLATE_GREY = new Colour(0x31, 0x4e, 0x69, ALPHA_NO_TRANSPARENCY);
    private static final Colour DARK_GREY = new Colour(0xa3, 0xa3, 0xa3, ALPHA_NO_TRANSPARENCY);
    private static final Colour LIGHT_GREY = new Colour(0xd3, 0xd3, 0xd3, ALPHA_NO_TRANSPARENCY);
    private static final Colour WHITE = new Colour(0xff, 0xff, 0xff, ALPHA_NO_TRANSPARENCY);
    private static final Colour BLACK = new Colour(0x00, 0x00, 0x00, ALPHA_NO_TRANSPARENCY);

    public static final Colour[] GREYSCALE = Gradient.createGradient(BLACK, WHITE, NUMBER_OF_STEPS);

    public static final Colour[] INVERSE_GREYSCALE = Gradient.createGradient(WHITE, BLACK, NUMBER_OF_STEPS);

    public static final Colour[] RAINBOW = Gradient.createMultiGradient(
            new Colour[] {BLACK, DARK_BLUE, LILA, RED, ORANGE, YELLOW, WHITE}, NUMBER_OF_STEPS);

    public static final Colour[] NORMAL_COLOUR = Gradient.createMultiGradient(
            new Colour[] {DARK_BLUE, LIGHT_BLUE, GREEN, YELLOW, ORANGE, RED}, NUMBER_OF_STEPS);

    public static final Colour[] INVERSE_COLOUR = Gradient.createMultiGradient(
            new Colour[] {RED, ORANGE, YELLOW, GREEN, LIGHT_BLUE, DARK_BLUE}, NUMBER_OF_STEPS);

    public static final Colour[] HOT1 = Gradient.createMultiGradient(
            new Colour[] {WHITE, YELLOW, ORANGE, RED, LILA}, NUMBER_OF_STEPS);

    public static final Colour[] HOT2 = Gradient.createMultiGradient(
            new Colour[] {LILA, RED, ORANGE, YELLOW, WHITE}, NUMBER_OF_STEPS);

    public static final Colour[] COOL1 = Gradient.createMultiGradient(
            new Colour[] {DARK_SLATE_GREY, BLUE_GREY, DARK_GREY, LIGHT_GREY, WHITE}, NUMBER_OF_STEPS);

    public static final Colour[] COOL2 = Gradient.createMultiGradient(
            new Colour[] {WHITE, LIGHT_GREY, DARK_GREY, BLUE_GREY, DARK_SLATE_GREY}, NUMBER_OF_STEPS);
}

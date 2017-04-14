package ch.zhaw.bait17.audio_signal_processing_toolbox.util;

/**
 * Represents a colour.
 *
 * @author georgrem, stockan1
 */

public class Colour {

    private final int RED;
    private final int GREEN;
    private final int BLUE;
    private final int ALPHA;
    private final int VALUE;

    /**
     *
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     * @param a the alpha component
     */
    public Colour(int r, int g, int b, int a) {
        this.RED = r;
        this.GREEN = g;
        this.BLUE = b;
        this.ALPHA = a;
        VALUE = ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) <<  8) |
                ((b & 0xFF) <<  0);
    }

    /**
     * Returns the red component of the {@code Colour}.
     *
     * @return the red component
     */
    public int getRed() {
        return RED;
    }

    /**
     * Returns the green component of the {@code Colour}.
     *
     * @return the green component
     */
    public int getGreen() {
        return GREEN;
    }

    /**
     * Returns the blue component of the {@code Colour}.
     *
     * @return the blue component
     */
    public int getBlue() {
        return BLUE;
    }

    /**
     * Returns the alpha component of the {@code Colour}.
     *
     * @return the alpha component
     */
    public int getAlpha() {
        return ALPHA;
    }

    /**
     * <p>
     *     Returns the RGB value representing the color in the default sRGB. <br>
     *     (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue).
     * </p>
     *
     * @return integer value of the {@code Colour}
     */
    public int getRGB() {
        return VALUE;
    }

}

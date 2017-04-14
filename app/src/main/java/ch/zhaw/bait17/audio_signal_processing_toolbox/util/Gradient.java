package ch.zhaw.bait17.audio_signal_processing_toolbox.util;

import android.support.annotation.NonNull;

/**
 * This class provides static methods to compute colour gradients.
 *
 * From: <a href="https://github.com/matthewbeckler/HeatMap/blob/master/Gradient.java">GitHub of Mattthew Beckler</a>
 */

public class Gradient {

    /**
     * <p>
     *     Creates an array of Colour objects for use as a gradient, using a linear
     *     interpolation between the two specified colors.
     * </p>
     *
     * @param firstColor    colour used for the bottom of the gradient
     * @param secondColor   colour used for the top of the gradient
     * @param numSteps      the number of steps in the gradient (resolution)
     * @return              an array of {@code Colour} objects
     * @throws IllegalArgumentException numSteps must be greater than 0
     */
    public static Colour[] createGradient(final Colour firstColor, final Colour secondColor,
                                          final int numSteps)
            throws IllegalArgumentException {
        if (numSteps <= 0) {
            throw new IllegalArgumentException("Number of steps must be greater than 0.");
        }

        // Bottom colour
        int red1 = firstColor.getRed();
        int green1 = firstColor.getGreen();
        int blue1 = firstColor.getBlue();
        int alpha1 = firstColor.getAlpha();

        // Top colour
        int red2 = secondColor.getRed();
        int green2 = secondColor.getGreen();
        int blue2 = secondColor.getBlue();
        int alpha2 = secondColor.getAlpha();

        int red = 0, green = 0, blue = 0, alpha = 0;
        Colour[] gradient = new Colour[numSteps];
        double iNorm = 0;
        for (int i = 0; i < numSteps; i++) {
            iNorm = i / (double) numSteps;
            red = (int) (red1 + (iNorm * (red2 - red1)));
            green = (int) (green1 + (iNorm * (green2 - green1)));
            blue = (int) (blue1 + (iNorm * (blue2 - blue1)));
            alpha = (int) (alpha1 + (iNorm * (alpha2 - alpha1)));
            gradient[i] = new Colour(red, green, blue, alpha);
        }

        return gradient;
    }

    /**
     * <p>
     *     Creates an array of Colour objects for use as a gradient,
     *     using an array of Colour objects.
     *     It uses a linear interpolation between each pair of points.
     *     The parameter numSteps defines the total number of colours in the returned array,
     *     not the number of colours per segment.
     * </p>
     *
     * @param colours       an array of Colour objects used for the gradient.
     *                      The Colour at index 0 will be the lowest colour
     * @param numSteps      the number of steps in the gradient (resolution)
     * @return              an array of {@code Colour} objects
     * @throws IllegalArgumentException numSteps must be greater than 0
     *                                  and Colour[] must contain at least tow colours.
     */
    public static Colour[] createMultiGradient(@NonNull Colour[] colours, int numSteps)
            throws IllegalArgumentException {
        if (numSteps <= 0) {
            throw new IllegalArgumentException("Number of steps must be greater than 0.");
        }

        int numSections = colours.length - 1;
        if (numSections <= 0) {
            throw new IllegalArgumentException("Colour array must contain at least two colours.");
        }
        int gradientIndex = 0;
        Colour[] gradient = new Colour[numSteps];
        Colour[] temp;

        for (int section = 0; section < numSections; section++) {
            // Compute sub-gradient: the gradient is divided into (n-1) sections
            temp = createGradient(colours[section], colours[section + 1], numSteps / numSections);
            for (int i = 0; i < temp.length; i++) {
                gradient[gradientIndex++] = temp[i];
            }
        }

        /*
            The rounding didn't work out. There is at least one unfilled slot in gradient[].
            Just copy the final Colour in the unfilled slots.
         */
        if (gradientIndex < numSteps) {
            for ( ; gradientIndex < numSteps; gradientIndex++) {
                gradient[gradientIndex] = colours[colours.length - 1];
            }
        }

        return gradient;
    }

}

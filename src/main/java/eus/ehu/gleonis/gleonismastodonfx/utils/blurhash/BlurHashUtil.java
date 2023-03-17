package eus.ehu.gleonis.gleonismastodonfx.utils.blurhash;

import java.util.Arrays;

class BlurHashUtil {

    private static final double[] SRGB2LINEAR = new double[256];

    static {
        for (int i = 0; i < 256; i++) {
            double v = i / 255.0;
            if (v <= 0.04045) {
                SRGB2LINEAR[i] = v / 12.92;
            } else {
                SRGB2LINEAR[i] = Math.pow((v + 0.055) / 1.055, 2.4);
            }
        }
    }

    static double toLinear(int value) {
        if (value < 0) {
            return SRGB2LINEAR[0];
        } else if (value >= 256) {
            return SRGB2LINEAR[255];
        } else {
            return SRGB2LINEAR[value];
        }
    }

    static int fromLinear(double value) {
        int pos = Arrays.binarySearch(SRGB2LINEAR, value);
        if (pos < 0)
            pos = ~pos;

        if (pos < 0)
            return 0;
        else if (pos >= 256) {
            return 255;
        } else {
            return pos;
        }
    }

    private static double signPow(double value) {
        // Precedent line: return Math.copySign(Math.pow(Math.abs(value), power), value); with power = 2.0
        return Math.copySign(value * value, value);
    }

    static void decodeDC(String str, double[] color) {
        int dcValue = Base83.decode(str);
        color[0] = toLinear(dcValue >> 16);
        color[1] = toLinear((dcValue >> 8) & 255);
        color[2] = toLinear(dcValue & 255);
    }

    static void decodeAC(String str, double realMaxValue, double[] color) {
        int acValue = Base83.decode(str);
        int quantR = acValue / (19 * 19);
        int quantG = (acValue / 19) % 19;
        int quantB = acValue % 19;
        color[0] = signPow((quantR - 9.0) / 9.0) * realMaxValue;
        color[1] = signPow((quantG - 9.0) / 9.0) * realMaxValue;
        color[2] = signPow((quantB - 9.0) / 9.0) * realMaxValue;
    }
}
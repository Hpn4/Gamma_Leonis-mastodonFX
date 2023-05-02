package eus.ehu.gleonis.gleonismastodonfx.utils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.util.HashMap;
import java.util.Map;

public final class BHDecoder {
    public static final BHDecoder INSTANCE;
    private static final HashMap<Integer, double[]> cacheCosinesX;
    private static final HashMap<Integer, double[]> cacheCosinesY;
    private static final Map<Character, Integer> charMap;

    static {
        INSTANCE = new BHDecoder();

        cacheCosinesX = new HashMap<>();
        cacheCosinesY = new HashMap<>();

        charMap = new HashMap<>();

        char[] bases = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
                'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '#', '$', '%', '*',
                '+', ',', '-', '.', ':', ';', '=', '?', '@', '[', ']', '^', '_', '{', '|', '}', '~'};
        for (int i = 0; i < bases.length; i++)
            charMap.put(bases[i], i);
    }

    private BHDecoder() {
    }

    // $FF: synthetic method
    public static Image decode(BHDecoder var0, String hash, int width, int height, float punch, boolean var5, int var6) {
        if ((var6 & 8) != 0)
            punch = 1.0F;

        if ((var6 & 16) != 0)
            var5 = true;

        return var0.decode(hash, width, height, punch, var5);
    }

    // $FF: synthetic method
    static int decode83(BHDecoder var0, String hash, int var2, int var3, int var4) {
        if ((var4 & 2) != 0)
            var2 = 0;

        if ((var4 & 4) != 0)
            var3 = hash.length();

        return var0.decode83(hash, var2, var3);
    }

    public void clearCache() {
        cacheCosinesX.clear();
        cacheCosinesY.clear();
    }

    public Image decode(String blurHash, int width, int height, float punch, boolean useCache) {
        if (blurHash != null && blurHash.length() >= 6) {
            int numCompEnc = decode83(blurHash, 0, 1);
            int numCompX = numCompEnc % 9 + 1;
            int numCompY = numCompEnc / 9 + 1;

            if (blurHash.length() != 4 + 2 * numCompX * numCompY)
                return null;

            int maxAcEnc = decode83(blurHash, 1, 2);
            float maxAc = (float) (maxAcEnc + 1) / 166.0F;
            int compSize = numCompX * numCompY;
            float[][] colors = new float[compSize][];

            for (int comp = 0; comp < compSize; ++comp) {
                float[] row;
                int colorEnc;
                if (comp == 0) {
                    colorEnc = INSTANCE.decode83(blurHash, 2, 6);
                    row = INSTANCE.decodeDc(colorEnc);
                } else {
                    colorEnc = 4 + comp * 2;
                    colorEnc = INSTANCE.decode83(blurHash, colorEnc, colorEnc + 2);
                    row = INSTANCE.decodeAc(colorEnc, maxAc * punch);
                }

                colors[comp] = row;
            }

            return composeBitmap(width, height, numCompX, numCompY, colors, useCache);
        }

        return null;
    }

    private int decode83(String str, int from, int to) {
        int result = 0;

        for (int i = from; i < to; ++i) {
            Integer index = charMap.get(str.charAt(i));

            if (index != null)
                result = result * 83 + index;
        }

        return result;
    }

    private float[] decodeDc(int colorEnc) {
        int r = colorEnc >> 16;
        int g = (colorEnc >> 8) & 255; // undo
        int b = colorEnc & 255;

        return new float[]{this.srgbToLinear(r), this.srgbToLinear(g), this.srgbToLinear(b)};
    }

    private float srgbToLinear(int colorEnc) {
        float v = (float) colorEnc / 255.0F;

        float linear;
        if (v <= 0.04045F)
            linear = v / 12.92F;
        else
            linear = (float) Math.pow((v + 0.055F) / 1.055F, 2.4F);

        return linear;
    }

    private float[] decodeAc(int value, float maxAc) {
        int r = value / 361;
        int g = value / 19 % 19;
        int b = value % 19;

        return new float[]{
                this.signedPow2((float) (r - 9) / 9.0F) * maxAc,
                this.signedPow2((float) (g - 9) / 9.0F) * maxAc,
                this.signedPow2((float) (b - 9) / 9.0F) * maxAc};
    }

    private float signedPow2(float value) {
        float pow = (float) Math.pow(value, 2);

        return Math.copySign(pow, value);
    }

    private Image composeBitmap(int width, int height, int numCompX, int numCompY, float[][] colors, boolean useCache) {
        int[] imageArray = new int[width * height];

        // Setup cosX cache
        boolean calculateCosX = !useCache || !cacheCosinesX.containsKey(width * numCompX);
        double[] cosinesX = this.getArrayForCosinesX(calculateCosX, width, numCompX);

        // Setup cosY cache
        boolean calculateCosY = !useCache || !cacheCosinesY.containsKey(height * numCompY);
        double[] cosinesY = this.getArrayForCosinesY(calculateCosY, height, numCompY);

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                float r = 0.0F;
                float g = 0.0F;
                float b = 0.0F;

                // Loop through components
                for (int j = 0; j < numCompY; ++j) {
                    for (int i = 0; i < numCompX; ++i) {
                        double cosX = this.getCos(cosinesX, calculateCosX, i, numCompX, x, width);
                        double cosY = this.getCos(cosinesY, calculateCosY, j, numCompY, y, height);

                        float basis = (float) (cosX * cosY);
                        float[] color = colors[j * numCompX + i];
                        r += color[0] * basis;
                        g += color[1] * basis;
                        b += color[2] * basis;
                    }
                }

                int rC = (linearToSrgb(r) & 0xFF)<< 16;
                int gC = (linearToSrgb(g) & 0xFF) << 8;
                int bC = (linearToSrgb(b) & 0xFF);

                imageArray[x + width * y] = (0xFF<<24) | rC | gC | bC;
            }
        }

        return ugglyConversion(imageArray, width, height);
    }


    private Image ugglyConversion(int[] buff, int width, int height) {
        WritableImage img = new WritableImage(width, height);
        PixelWriter pw = img.getPixelWriter();

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                pw.setArgb(x, y, buff[x + width * y]);

        return img;
    }

    private double[] getArrayForCosinesY(boolean calculate, int height, int numCompY) {
        if (calculate) {
            double[] toCached = new double[height * numCompY];
            cacheCosinesY.put(height * numCompY, toCached);

            return toCached;
        }

        double[] cached = cacheCosinesY.get(height * numCompY);
        if (cached == null)
            System.err.println("cached is null: cacheCosinesY[height * numCompY]");

        return cached;
    }

    private double[] getArrayForCosinesX(boolean calculate, int width, int numCompX) {
        if (calculate) {
            double[] toCached = new double[width * numCompX];
            cacheCosinesX.put(width * numCompX, toCached);

            return toCached;
        }

        double[] cached = cacheCosinesX.get(width * numCompX);
        if (cached == null)
            System.err.println("cached is null: cacheCosinesX[width * numCompX]");

        return cached;
    }

    private double getCos(double[] $this$getCos, boolean calculate, int x, int numComp, int y, int size) {
        if (calculate) {
            double angle = Math.PI * (double) y * (double) x / (double) size;
            $this$getCos[x + numComp * y] = Math.cos(angle);
        }

        return $this$getCos[x + numComp * y];
    }

    private int linearToSrgb(float value) {
        float v = Math.max(0.0F, Math.min(1.0F, value)); // Clamp

        if (v <= 0.0031308F)
            return (int) (v * 12.92F * 255.0F + 0.5F);

        return (int) ((1.055F * (float) Math.pow(v, 0.41666666F) - 0.055F) * (float) 255 + 0.5F);
    }
}

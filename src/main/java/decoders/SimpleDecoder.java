package decoders;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * A decoder that extracts the provided secret within the lsb of each subpixel contained in the image.
 */
public class SimpleDecoder implements IDecoder {

    private BufferedImage sourceImage;

    /**
     * A decoder that extracts the provided secret within the lsb of each subpixel contained in the image.
     *
     * @param sourceImage The source image to extract the secret from.
     */
    public SimpleDecoder(BufferedImage sourceImage) {
        this.sourceImage = sourceImage;
    }

    /**
     * Extracts the secret bytes stored within the provided image.
     *
     * @param sourceImage The image containing a secret to be extracted.
     * @return A byte array containing the secret.
     */
    private byte[] simpleDecode(BufferedImage sourceImage) {

        // Pixels are 32 bit values stored as an ARGB format with each colour channel/subpixel taking up 8,
        // bits, we completely ignore the alpha channel though.
        int[] pixels = new int[sourceImage.getWidth() * sourceImage.getHeight()];
        // Copy buffer with pixel values for easy access.
        sourceImage.getRGB(0, 0, sourceImage.getWidth(), sourceImage.getHeight(), pixels, 0, sourceImage.getWidth());

        byte secretByte = 0;
        ArrayList<Byte> secretBytes = new ArrayList<>();

        boolean lookingForSecrets = true;
        int offset = 0;
        // For each subpixel in the image, we want to extract the secret bit stored as the lsb.
        // The way we do this is by looping through every subpixel sequentially, checking after
        // every secret byte for the value of a continue bit, which determines if there is another
        // secret byte to be read.
        for (int bitIndex = 0; lookingForSecrets; bitIndex++) {
            int pixelIndex = (bitIndex + offset) / 3;
            int subpixelIndex = (bitIndex + offset) % 3;

            secretByte <<= 1;

            int[] subpixel = {
                    (pixels[pixelIndex] & 0xFF0000) >> 16,
                    (pixels[pixelIndex] & 0x00FF00) >> 8,
                    (pixels[pixelIndex] & 0x0000FF)
            };

            secretByte |= subpixel[subpixelIndex] & 0x1;

            // Every 8 bits we want to check if we have another secret byte to read
            if (((bitIndex + 1) % 8) == 0) {
                offset++;

                // Add the completed secret byte to the list
                // Note: We don't need to zero out the secretByte as we'll be reading 8 bits into it
                // by the time we get here again, so all the values will be overwritten.
                secretBytes.add(secretByte);

                if (subpixelIndex == 2) {
                    pixelIndex++;
                    subpixelIndex = 0;
                } else {
                    subpixelIndex++;
                }

                // Since we may have changed pixel, we need to access the continue bit directly
                // from the pixel map, instead of using subpixel
                // We do this by masking out everything except the lsb of the appropriate subpixel.
                // If the continue bit is set to 1, that means there is an additional secret byte
                // to read.
                lookingForSecrets = (pixels[pixelIndex] & (0x1 << (16 - 8 * subpixelIndex))) != 0;
            }
        }

        byte[] output = new byte[secretBytes.size()];
        for (int i = 0; i < output.length; i++) output[i] = secretBytes.get(i);
        return output;
    }

    @Override
    public byte[] decode() {
        return simpleDecode(sourceImage);
    }
}

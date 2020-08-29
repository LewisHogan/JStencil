package encoders;

import java.awt.image.BufferedImage;

/**
 * An encoder that stores the provided secret within the lsb of each subpixel contained in the image.
 */
public class SimpleEncoder implements IEncoder {

    private BufferedImage sourceImage;
    private byte[] secret;

    /**
     * An encoder that stores the provided secret within the lsb of each subpixelcontained in the image.
     *
     * @param sourceImage The source image to store the secret in.
     * @param secret      The secret to store.
     */
    public SimpleEncoder(BufferedImage sourceImage, byte[] secret) {
        this.sourceImage = sourceImage;
        this.secret = secret;
    }

    /**
     * Hides the secret within a copy of the provided source image.
     *
     * @param sourceImage The original, unmodified image to hide the secret in.
     * @param secret      The secret to hide in the image.
     * @return A new copy of sourceImage containing the encoded secret.
     */
    public BufferedImage simpleEncode(BufferedImage sourceImage, byte[] secret) {
        // Pixels are 32 bit values stored as an ARGB format with each colour channel taking up 8 bits,
        // we completely ignore the alpha channel though.
        int[] pixels = new int[sourceImage.getWidth() * sourceImage.getHeight()];
        // Populate buffer with original pixel values
        sourceImage.getRGB(0, 0, sourceImage.getWidth(), sourceImage.getHeight(), pixels, 0, sourceImage.getWidth());

        int offset = 0;
        // For each colour subpixel in the image, we want to store a single bit of our secret.
        // The way we do this is we loop through each bit of the secret (secret.length * 8 as we may
        // have multiple bytes for our secret) and for each corresponding subpixel we set the lsb to
        // match the secret bit. Due to the fact that we're only changing the lsb for each subpixel,
        // these changes should be hard to visually identify.
        //
        // For every full byte of secret we store, a continue bit is inserted to inform the decoder if
        // another byte of data is available.
        for (int bitIndex = 0; bitIndex < secret.length * 8; bitIndex++) {
            int secretByteIndex = bitIndex / 8;
            // We store 3 bits of the secret per pixel (one for each colour subpixel excluding the alpha)
            int pixelIndex = (bitIndex + offset) / 3;
            int subpixelIndex = (bitIndex + offset) % 3;

            // Shift which bit of the secret we are using to the right each time, and if it's non zero that means
            // we have a 1 for the secret bit.
            int secretBit = (secret[secretByteIndex] & (0x80 >> (((bitIndex) % 8)))) == 0 ? 0 : 1;

            // Split the current pixel into R, G, B
            int[] subpixel = {
                    (pixels[pixelIndex] & 0xFF0000) >> 16,
                    (pixels[pixelIndex] & 0x00FF00) >> 8,
                    (pixels[pixelIndex] & 0x0000FF)
            };

            // We need to compare the lsb of the image to the secret to see if a modification is necessary.
            if (secretBit != (subpixel[subpixelIndex] & 0x1)) {
                // Set the lsb to whatever the secret bit says it should be
                subpixel[subpixelIndex] = (secretBit == 1 ? subpixel[subpixelIndex] | 1 : subpixel[subpixelIndex] & 0xFE);
                pixels[pixelIndex] = (subpixel[0] << 16) + (subpixel[1] << 8) + subpixel[2];
            }

            // Every 8th data bit we need a continue bit if the secret is not finished
            // This will be written to the next subpixel, and then we need to offset the rest of the data
            // by one subpixel.
            if (((bitIndex + 1) % 8) == 0) {
                offset++;

                // If we need to switch pixel, we need to ensure we write to the first subpixel
                if (subpixelIndex == 2) {
                    pixelIndex++;
                    subpixelIndex = 0;
                } else {
                    subpixelIndex++;
                }

                // We want to know if there are more secret bytes after this one, if this is the case
                // then we want to write a 1 (saying more data is available), otherwise we want to write a 0.
                if (secretByteIndex < (secret.length - 1)) {
                    subpixel[subpixelIndex] |= 1;
                } else {
                    subpixel[subpixelIndex] &= 0xFFFFFE;
                }

                // Since we must have modified a subpixel, update the buffer
                pixels[pixelIndex] = (subpixel[0] << 16) + (subpixel[1] << 8) + subpixel[2];
            }
        }

        BufferedImage output = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), sourceImage.getType());
        output.setRGB(0, 0, output.getWidth(), output.getHeight(), pixels, 0, output.getWidth());

        return output;
    }

    @Override
    public boolean isEncodable() {
        // The size of the secret (in pixels) is going to be all of the bits required
        // for the secret plus the number of bytes the secret takes up (as we need to allocate space
        // for the continue bits).
        int secretSize = secret.length * 8 + secret.length;
        // TODO: Verify that this assumption is valid
        // Assume we have access to at least R,G,B
        int imageSize = sourceImage.getHeight() * sourceImage.getWidth() * 3;

        return imageSize >= secretSize;
    }


    @Override
    public BufferedImage encode() {
        return simpleEncode(sourceImage, secret);
    }
}
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
            int secretByteIndex = (bitIndex + offset) / 8;
            // We store 3 bits of the secret per pixel (one for each colour channel excluding the alpha)
            int pixelIndex = (bitIndex + offset) / 3;
            int channelIndex = (bitIndex + offset) % 3;

            // Shift which bit of the secret we are using to the right each time, and if it's non zero that means
            // we have a 1 for the secret bit.
            int secretBit = (secret[secretByteIndex] & (0x80 >> (((bitIndex + offset) % 8)))) == 0 ? 0 : 1;

            // Split the current pixel into R, G, B
            int[] subpixel = {
                    (pixels[pixelIndex] & 0xFF0000) >> 16,
                    (pixels[pixelIndex] & 0x00FF00) >> 8,
                    (pixels[pixelIndex] & 0x0000FF)
            };

            // We need to compare the lsb of the image to the secret to see if a modification is necessary.
            if (secretBit != (subpixel[channelIndex] & 0x1)) {
                // Set the lsb to whatever the secret bit says it should be
                subpixel[channelIndex] = (secretBit == 1 ? subpixel[channelIndex] | 1 : subpixel[channelIndex] & 0xFE);
                pixels[pixelIndex] = (subpixel[0] << 16) + (subpixel[1] << 8) + subpixel[2];
            }

            // Every 8th data bit we need a continue bit if the secret is not finished
            if ((bitIndex % 8) == 0 && bitIndex != 0) {
                offset++;

                if ((bitIndex + 1) == (secret.length * 8) - 1) {
                    // We need to set the lsb to 1 so that the decoder will know the secret ends here
                    pixels[pixelIndex] |= 1;
                } else {
                    // 0 the lsb if we're on the last byte
                    pixels[pixelIndex] &= 0xFFFFFE;
                }
            }
        }

        BufferedImage output = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), sourceImage.getType());
        output.setRGB(0, 0, output.getWidth(), output.getHeight(), pixels, 0, output.getWidth());

        return output;
    }

    /**
     * Checks if the source image is suitable for hiding the secret within.
     *
     * @return If the secret can be stored within the image.
     */
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
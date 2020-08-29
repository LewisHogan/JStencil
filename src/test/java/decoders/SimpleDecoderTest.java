package decoders;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class SimpleDecoderTest {
    @Test
    public void decode() {
        BufferedImage sourceImage = new BufferedImage(7, 1, BufferedImage.TYPE_INT_ARGB);
        int[] sourcePixels = {0x20FFFE, 0x2C4316, 0x1CB62C};
        sourceImage.setRGB(0, 0, 3, 1, sourcePixels, 0, 1);

        IDecoder decoder = new SimpleDecoder(sourceImage);
        byte[] output = decoder.decode();

        assertArrayEquals("H".getBytes(), output);
    }

    @Test
    public void decodeMultipleBytes() {
        BufferedImage sourceImage = new BufferedImage(7, 1, BufferedImage.TYPE_INT_ARGB);
        int[] sourcePixels = {0x20FFFE, 0x2C4316, 0x1CB62D, 0x26F30A, 0x0C6120, 0x4E6330};
        sourceImage.setRGB(0, 0, 6, 1, sourcePixels, 0, 1);

        IDecoder decoder = new SimpleDecoder(sourceImage);
        byte[] output = decoder.decode();

        assertArrayEquals("HI".getBytes(), output);
    }
}
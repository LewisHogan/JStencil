package encoders;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

class SimpleEncoderTest {

    /**
     * Checks that both provided BufferedImages contain the same data.
     *
     * @param firstImage The first buffered image.
     * @param secondImage The second buffered image.
     * @return True if both buffered images contain the same data.
     */
    private boolean areEqual(BufferedImage firstImage, BufferedImage secondImage) {
        DataBuffer firstBuffer = firstImage.getData().getDataBuffer();
        DataBuffer secondBuffer = secondImage.getData().getDataBuffer();

        int bufferSize = firstBuffer.getSize();
        int bankCount = firstBuffer.getNumBanks();

        if (firstImage.getType() != secondImage.getType()) return false;
        if (bufferSize != secondBuffer.getSize()) return false;
        if (bankCount != secondBuffer.getNumBanks()) return false;

        for (int i = 0; i < bankCount; i++) {
            for (int j = 0; j < bufferSize; j++) {
                if (firstBuffer.getElem(i, j) != secondBuffer.getElem(i, j)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Test
    void encode() {
        // TODO: Split into more detailed tests, possibly in separate test functions.

        // This test was worked out by hand and the results may be wrong due to human error
        // TODO: Replace with real input cases once the decoder is made.
        BufferedImage input = new BufferedImage(7, 1, BufferedImage.TYPE_INT_ARGB);
        int[] inputPixels = {0x20FFFF, 0x2D4317, 0x1DB72C};
        input.setRGB(0, 0, 3, 1, inputPixels, 0, 1);

        BufferedImage expectedOutput = new BufferedImage(7, 1, BufferedImage.TYPE_INT_ARGB);
        int[] expectedOutputPixels = {0x20FFFE, 0x2C4316, 0x1CB62C};
        expectedOutput.setRGB(0, 0, 3, 1, expectedOutputPixels, 0, 1);

        IEncoder encoder = new SimpleEncoder(input, "H".getBytes());
        BufferedImage output = encoder.encode();

        assertTrue(areEqual(output, expectedOutput));
    }

    @Test
    void encodeMultipleBytes() {
        // This test was worked out by hand and the results may be wrong due to human error
        // TODO: Replace with real input cases once the decoder is made.
        BufferedImage input = new BufferedImage(7, 1, BufferedImage.TYPE_INT_ARGB);
        int[] inputPixels = {0x20FFFF, 0x2D4317, 0x1DB72C, 0x27F30B, 0x0C6021, 0x4E6330};
        input.setRGB(0, 0, 6, 1, inputPixels, 0, 1);

        BufferedImage expectedOutput = new BufferedImage(7, 1, BufferedImage.TYPE_INT_ARGB);
        int[] expectedOutputPixels = {0x20FFFE, 0x2C4316, 0x1CB62D, 0x26F30A, 0x0C6120, 0x4E6330};
        expectedOutput.setRGB(0, 0, 6, 1, expectedOutputPixels, 0, 1);

        IEncoder encoder = new SimpleEncoder(input, "HI".getBytes());
        BufferedImage output = encoder.encode();

        assertTrue(areEqual(output, expectedOutput));
    }

    @Test
    void isEncodable() {
        // TODO: Add more test cases, possible in separate test functions.
        // TODO: Find out if the assumption is true for SimpleEncoder.isEncodable.
        BufferedImage input = new BufferedImage(7, 1, BufferedImage.TYPE_INT_ARGB);
        int[] inputPixels = {0x20FFFF, 0x2D4317, 0x1DB72C};
        input.setRGB(0, 0, 3, 1, inputPixels, 0, 1);

        IEncoder encoder = new SimpleEncoder(input, "H".getBytes());

        assertTrue(encoder.isEncodable());
    }
}
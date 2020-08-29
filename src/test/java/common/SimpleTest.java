package common;

import decoders.IDecoder;
import decoders.SimpleDecoder;
import encoders.IEncoder;
import encoders.SimpleEncoder;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleTest {

    @Test
    public void encodeAndDecode() {
        int[] sourcePixels = {0x20FFFF, 0x2D4317, 0x1DB72C, 0x27F30B, 0x0C6021, 0x4E6330};
        byte[] secret = "L".getBytes();

        BufferedImage sourceImage = new BufferedImage(7, 10, BufferedImage.TYPE_INT_ARGB);
        sourceImage.setRGB(0, 0, 6, 1, sourcePixels, 0, 1);

        IEncoder encoder = new SimpleEncoder(sourceImage, secret);
        assertTrue(encoder.isEncodable());

        BufferedImage encodedImage = encoder.encode();

        IDecoder decoder = new SimpleDecoder(encodedImage);
        assertArrayEquals(secret, decoder.decode());

    }

    @Test
    public void encodeAndDecodeMultipleBytes() {
        int[] sourcePixels = {0x20FFFF, 0x2D4317, 0x1DB72C, 0x27F30B, 0x0C6021, 0x4E6330};
        byte[] secret = "This is a secret".getBytes();

        BufferedImage sourceImage = new BufferedImage(7, 10, BufferedImage.TYPE_INT_ARGB);
        sourceImage.setRGB(0, 0, 6, 1, sourcePixels, 0, 1);

        IEncoder encoder = new SimpleEncoder(sourceImage, secret);
        assertTrue(encoder.isEncodable());

        BufferedImage encodedImage = encoder.encode();

        IDecoder decoder = new SimpleDecoder(encodedImage);
        assertArrayEquals(secret, decoder.decode());

    }
}

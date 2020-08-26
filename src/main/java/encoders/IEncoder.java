package encoders;

import java.awt.image.BufferedImage;

/**
 * An IEncoder is an encoder that given a source image and a secret to hide, can store the secret within the image.
 */
public interface IEncoder {

    /**
     * Hide a secret inside a buffered image.
     * @return A buffered image containing the secret.
     */
    BufferedImage encode();

    /**
     * Checks encoding is possible with the encoder's current secret and source image.
     * @return If the encoder's secret can be hidden in the encoder's image.
     */
    boolean isEncodable();
}

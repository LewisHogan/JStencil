package decoders;

/**
 * An IDecoder is a decoder that given a source image can extract a secret.
 */
public interface IDecoder {
    /**
     * Extracts a secret from within the image.
     *
     * @return A byte array containing the secret.
     */
    byte[] decode();
}

import decoders.SimpleDecoder;
import encoders.SimpleEncoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple command line interface to allow the user to select either encoding or decoding of image files.
 */

public class JStencil {
    public static void main(String[] arguments) {
        //Check user entered arguments to see if help flag has been entered
        if (arguments.length == 0) {
            helpMessage();
            return;
        } else if (arguments.length == 1 && arguments[0].equals("-h")) {
            helpMessage();
            return;
            //Checks if the minimum amount of user arguments have been entered if not give user help message
        } else if (arguments.length < 8) {
            errorMessage("Insufficient number of arguments given...");
            helpMessage();
            return;
        }
        //Put all of user arguments into map for ease of use later
        Map<String, String> argumentMap = mapArguments(arguments);
        //Check that user wants to encode and not decode
        if (argumentMap.containsKey("-e") && !argumentMap.containsKey("-d")) {

            try {
                //try to read in the user specified file
                BufferedImage image = ImageIO.read((new File(argumentMap.get("-i"))));
                //pass the user entered image and secret to encoder method
                SimpleEncoder encoder = new SimpleEncoder(image, argumentMap.get("-s").getBytes());
                //checks if file is large enough to hold secret
                if (!encoder.isEncodable()) {
                    errorMessage("selected image is too small to hide the secret, please pick a larger image...");
                    return;
                }
                try {
                    //saves encoded image with secret in file specified by user
                    BufferedImage encodedImage = encoder.encode();
                    File outputFile = new File(argumentMap.get("-o"));
                    ImageIO.write(encodedImage, "png", outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    errorMessage("problem during writing of file, please try again...");
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                errorMessage("Invalid input file location...");
                return;
            }
            //checks user wants to use decode and not encode
        } else if (argumentMap.containsKey("-d") && !argumentMap.containsKey("-e")) {
            try {
                //tries to read in user specified input file to be decoded
                BufferedImage image = ImageIO.read(new File(argumentMap.get("-i")));
                //passes image to decoder method
                SimpleDecoder decoder = new SimpleDecoder(image);
                //saves retuning byte array for be outputted to user
                byte[] outputString = decoder.decode();
                //write outputed byte array to file
                try (FileOutputStream fos = new FileOutputStream(argumentMap.get("-o"))) {
                    fos.write(outputString);
                } catch (IOException e) {
                    e.printStackTrace();
                    errorMessage("writing secret to output file");
                }
            } catch (IOException e) {
                e.printStackTrace();
                errorMessage("Error reading image...");
            }
            //checks for user input outside of expected use cases
        } else if (argumentMap.containsKey("-e") && argumentMap.containsKey("-d")) {
            errorMessage("unable to decode and encode at the same time");
        } else {
            errorMessage("incorrect arguments provided...");
        }
    }

    /**
     * Puts all user entered data into a map.
     *
     * @param arguments The user entered flags and their contents
     * @return A map containing the user entered flags and their contents
     */

    private static Map<String, String> mapArguments(String[] arguments) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i].startsWith("-")) {
                map.put(arguments[i], arguments[i + 1]);
                System.out.println(arguments[i] + arguments[i + 1]);
            }
        }
        return map;
    }


    /**
     * Displays to the user a help message on how to use the program including a usage example
     */
    private static void helpMessage() {
        System.out.println("\tUsage: JStencil -i <input-file> -o <output-file> -e [encoder]/ -d [decoder] -s <secret> -k <key>");
        String currentDirectory = System.getProperty("user.dir");
        System.out.println("\nExample: JStencil -i " + currentDirectory + "/img1.png -o " + currentDirectory + "/img2.png -e Simple -s " + currentDirectory + "/secret.txt -k " + currentDirectory + "/key.txt");
    }

    /**
     * Displays to the user an error message.
     *
     * @param Error error message
     */
    private static void errorMessage(String Error) {
        System.err.println("\tError! " + Error);
    }

}

import encoders.IEncoder;
import encoders.SimpleEncoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class JStencil {
    public static void main(final String... arguments) {
        if (arguments.length == 0) {
            System.err.println("Error! Insufficient number of arguments parsed...");
            return;
        }
        // 5 arguments -i, -o, -e/-d, -s, -k
        if ((arguments.length < 9) && !arguments[0].equals("-h")) {
            System.err.println("Error! Insufficient number of arguments parsed");
            System.out.println("\tUsage: JStencil -i <input-file> -o <output-file> -e [encoder]/ -d [decoder] -s <Secret> -k <key>");
            System.out.println("\tUse -h for a list of possible options");
            String currentDirectory = System.getProperty("user.dir");
            System.out.println("Example: JStencil -i " + currentDirectory + "/img1.png -o " + currentDirectory + "/img2.png -e Simple -s " + currentDirectory + "/secret.txt -k " + currentDirectory + "/key.txt");
        } else if (arguments[0].equals("-h")) {
            System.out.println("\tUsage: JStencil -i <input-file> -o <output-file> -e [encoder]/ -d [decoder] -s <Secret> -k <key>");
            String currentDirectory = System.getProperty("user.dir");
            System.out.println("Example: JStencil -i " + currentDirectory + "/img1.png -o " + currentDirectory + "/img2.png -e Simple -s " + currentDirectory + "/secret.txt -k " + currentDirectory + "/key.txt");
        } else {

            boolean encode = false;
            boolean decode = false;
            boolean input = false;
            boolean output = false;
            boolean secret = false;
            boolean key = false;

            for (int i = 0; i < arguments.length; i++) {
                if (arguments[i].equals("-e")) {
                    encode = true;
                    if (!arguments[i].equals("simple") || !arguments[i].equals("Simple")) {
                        System.err.println("\tError! Incorrect encoding use...");
                        return;
                    }
                } else if (arguments[i].equals("-d")) {
                    decode = true;
                    if (!arguments[i].equals("simple") || !arguments[i].equals("Simple")) {
                        System.err.println("\tError! Incorrect decoding use...");
                        return;
                    }
                } else if (arguments[i].equals("-i")) {
                    input = true;
                } else if (arguments[i].equals("-o")) {
                    output = true;
                } else if (arguments[i].equals("-s")) {
                    secret = true;
                } else if (arguments[i].equals("-k")) {
                    key = true;
                }
            }
            if (encode && input && output && secret && key && !decode) {
                //call encode function
                System.out.println("Calling encode function");
                try {
                    BufferedImage image = ImageIO.read(new File(arguments[2]));
                    SimpleEncoder encoder = new SimpleEncoder(image, arguments[8].getBytes());
                    encoder.isEncodable();
                    encoder.encode();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Error! image is either not an image or not found");
                }


            } else if (decode && input && output && secret && key && !encode) {
                //call decode function
                System.out.println("Calling decode function");
            } else {
                System.err.println("\tError! Arguments supplied are either wrong or insufficient");
                System.out.println("\tUsage: JStencil -i <input-file> -o <output-file> -e [encoder]/ -d [decoder] -s <Secret> -k <key>");
                String currentDirectory = System.getProperty("user.dir");
                System.out.println("Example: JStencil -i " + currentDirectory + "/img1.png -o " + currentDirectory + "/img2.png -e -s " + currentDirectory + "/secret.txt -k " + currentDirectory + "/key.txt");
            }
        }

    }
}

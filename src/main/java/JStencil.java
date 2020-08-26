import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;

public class JStencil {
    public static void main(final String...arguments) {
        for (String arg : arguments) {
            System.out.println(arg);
        }

        // 5 arguments -i, -o, -e/-d, -s, -k
        if ((arguments.length < 9) && !arguments[0].equals("-h")) {
            System.out.println("Error! Insufficient number of arguments parsed");
            System.out.println("\tUsage: JStencil -i <input-file> -o <output-file> -e [encoder]/ -d [decoder] -s <Secret> -k <key>");
            System.out.println("\tUse -h for a list of possible options");
            String currentDirectory = System.getProperty("user.dir");
            System.out.println("Example: JStencil -i " + currentDirectory + "/img1.png -o " + currentDirectory + "/img2.png -e -s " + currentDirectory + "/secret.txt -k " + currentDirectory + "/key.txt");
        } else if (arguments[0].equals("-h")) {
            System.out.println("\tUsage: JStencil -i <input-file> -o <output-file> -e [encoder]/ -d [decoder] -s <Secret> -k <key>");
            String currentDirectory = System.getProperty("user.dir");
            System.out.println("Example: JStencil -i " + currentDirectory + "/img1.png -o " + currentDirectory + "/img2.png -e -s " + currentDirectory + "/secret.txt -k " + currentDirectory + "/key.txt");
        } else {

            boolean encode = false;
            boolean decode = false;
            boolean input = false;
            boolean output = false;
            boolean secret = false;
            boolean key = false;

            for (int i =0; i< arguments.length;i++){
                if (arguments[i].equals("-e")){
                    encode = true;
                }else if (arguments[i].equals("-d")){
                    decode = true;
                }else if (arguments[i].equals("-i")){
                    input = true;
                }else if (arguments[i].equals("-o")){
                    output = true;
                }else if (arguments[i].equals("-s")){
                    secret = true;
                }else if (arguments[i].equals("-k")){
                    key = true;
                }
            }
            if (encode && input && output && secret && key && !decode){
                //call encode function
                System.out.println("Calling encode function");
            }else if (decode && input && output && secret && key && !encode){
                //call decode function
                System.out.println("Calling decode function");
            }else {
                System.out.println("Error! Arguments supplied are either wrong or insufficient");
                System.out.println("\tUsage: JStencil -i <input-file> -o <output-file> -e [encoder]/ -d [decoder] -s <Secret> -k <key>");
                String currentDirectory = System.getProperty("user.dir");
                System.out.println("Example: JStencil -i " + currentDirectory + "/img1.png -o " + currentDirectory + "/img2.png -e -s " + currentDirectory + "/secret.txt -k " + currentDirectory + "/key.txt");
            }
        }
    }
}

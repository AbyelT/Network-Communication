import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;

/**
 * TCP client acts as an client-side of an TCP communication, it creates an TCP
 * socket and uses them to send and recive data from a server-side process.
 */
public class TCPClient {
    /**
     * askServer creates an TCP socket and through it sends and receives data from a
     * server. Depending if the user included query, one of the two methods is used
     * 
     * @param hostname the hostname of the web server to connect
     * @param port     the portnumber of the server-side process
     * @param ToServer the query sent to the server
     * @return a string containing the response from the server
     */
    public static String askServer(String hostname, int port, String ToServer) throws IOException {
        if (ToServer == null) {
            return askServer(hostname, port);
        } else {
            String message;
            Socket mySocket;
            mySocket = new Socket(hostname, port);
            byte[] fromUser = ToServer.getBytes(StandardCharsets.UTF_8);
            byte[] fromServer = new byte[2048];

            mySocket.getOutputStream().write(fromUser);
            mySocket.getOutputStream().write('\n');
            int length = mySocket.getInputStream().read(fromServer);
            message = new String(fromServer, 0, length, StandardCharsets.UTF_8);
            mySocket.close();
            return message;
        }
    }

    public static String askServer(String hostname, int port) throws IOException {
        byte[] fromServer = new byte[2048];
        String message;
        Socket mySocket = new Socket(hostname, port);
        int length = mySocket.getInputStream().read(fromServer);

        message = new String(fromServer, 0, length, StandardCharsets.UTF_8);
        mySocket.close();
        return message;
    }
}

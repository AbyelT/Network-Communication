import java.net.*;
import java.io.*;

/**
 * HTTPEcho acts as a web-server process that accepts incoming TCP connections,
 * reads data and echoes an HTTP response back the same data that was given.
 */
public class HTTPEcho {

    /**
     * main implements the server-side process by creating 
     * a socket with a port for clients to connect to, 
     * @param args the given port number
     * @throws IOException
     */
    public static void main(String[] args) throws IOException{
        int portNr = Integer.parseInt(args[0]);
        try{
            ServerSocket server = new ServerSocket(portNr);          //create a server socket to port 20
            System.out.println("Running server, port: " + portNr);
            String response;
            String s;

            while(true) {
                //used as a bridge between byte-stream and char-streams
                Socket newClient = server.accept(); 
                BufferedReader buff = new BufferedReader(new InputStreamReader(newClient.getInputStream()));
                BufferedWriter wuff = new BufferedWriter(new OutputStreamWriter(newClient.getOutputStream()));
                response = "HTTP/1.1 200 OK\r\n\r\n";
                while(!((s = buff.readLine()).isEmpty())) {
                    response += (s + "\r\n");
                }
                wuff.write(response, 0, response.length());
                wuff.close();
                newClient.close();
            }
        }
        catch(Exception e){
            System.out.println("An unexpected error has occured");
        }
    }
}

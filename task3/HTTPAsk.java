import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class HTTPAsk {
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        ServerSocket server = new ServerSocket(port);
        System.out.println("Running server, port: " + port);
        String httpOK = "HTTP/1.1 200 OK\r\nContent-Type: text/plain; Charset=utf-8\r\n\r\n";

        while (true) {
            Socket newClient = server.accept();
            BufferedReader buff = new BufferedReader(new InputStreamReader(newClient.getInputStream()));
            BufferedWriter wuff = new BufferedWriter(new OutputStreamWriter(newClient.getOutputStream()));
            String sHost = "";
            int sPort = 0;
            String sQuery = null;
            try {
                String request = URLDecoder.decode(buff.readLine(), StandardCharsets.UTF_8.name()); //makes sure all %20 is replaced with blankspace
                request = request.substring(4, request.length());                                   //Remove the /GET
                if (!request.substring(0, 5).equals("/ask?"))                                       //check if the HTTP request is valid
                    throw new SocketException();
                else {
                    URL theUrl = new URL("http://localhost" + request);             //the URL constructor needs the protocol and hostname to work
                    String[] arry = theUrl.getQuery().split("[=?&]");                                       
                    if (!(arry[0].equals("hostname") && arry[2].equals("port"))) {             //checks if the given query is valid
                        throw new Exception();
                    } else {
                        if(arry.length > 4) {   
                            if(!arry[4].equals("string"))
                                throw new Exception();
                            else 
                                sQuery = arry[5].split(" HTTP/1.1")[0];
                        }
                        sHost = arry[1];
                        sPort = Integer.parseInt(arry[3].split(" HTTP/1.1")[0]);
                        String serverOutput = TCPClient.askServer(sHost, sPort, sQuery);
                        wuff.write(httpOK + serverOutput);
                    }
                }
            } catch (ConnectException e) {
                wuff.write("HTTP/1.1 200 OK\r\n\r\nCannot connect to server at " + sHost + ":" + sPort);
            } catch (UnknownHostException e) {
                wuff.write("HTTP/1.1 200 OK\r\n\r\nCannot find the host " + sHost);
            } catch (SocketTimeoutException e) {
                wuff.write("HTTP/1.1 408 request timeout\r\n\r\nconnection to " + sHost + " timed out");
            } catch (SocketException e) {
                wuff.write("HTTP/1.1 404 not found\r\n\r\n404 not found");
            } catch (Exception e) {
                wuff.write("HTTP/1.1 400 bad request\r\n\r\n400 bad request");
            }
            wuff.close();
        }
    }
}

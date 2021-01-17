import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

class MyRunnable implements Runnable {
    private Socket socket;

    public MyRunnable(Socket s) {
        socket = s;
    }

    public void run() {
        try {
            BufferedReader buff = null;
            BufferedWriter wuff = null;
            String httpOK = "HTTP/1.1 200 OK\r\nContent-Type: text/plain; Charset=utf-8\r\n\r\n";
                try {
                    buff = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                    wuff = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
                    String sHost = "";
                    int sPort = 0;
                    String sQuery = null;
                    String request = buff.readLine();

                    if (request.equals("GET /favicon.ico HTTP/1.1") || request.equals(null)) { // in case the web browser requests favicon
                        wuff.close();
                        socket.close();
                        return;
                    }

                    request = request.substring(4, request.length());
                    if (request.substring(0, 5).equals("/ask?")) {                              // check if the HTTP request is valid
                        request = URLDecoder.decode(request, StandardCharsets.UTF_8.name());
                        URL theUrl = new URL("http://localhost" + request);
                        String[] arry = theUrl.getQuery().split("[=?&\n]");
                        if ((arry[0].equals("hostname") && arry[2].equals("port"))) {           // checks if the given query is valid 
                            if (arry.length > 4) {
                                if (arry[4].equals("string"))
                                    sQuery = arry[5].split(" HTTP/1.1")[0];
                                else {
                                    wuff.write("HTTP/1.1 400 bad request\r\n\r\n400 bad request\r\n");
                                    wuff.close();
                                    socket.close();
                                    return;
                                }
                            }
                            sHost = arry[1];
                            sPort = Integer.parseInt(arry[3].split(" HTTP/1.1")[0]);
                            String serverOutput = TCPClient.askServer(sHost, sPort, sQuery);
                            wuff.write(httpOK + serverOutput);
                        } else {
                            wuff.write("HTTP/1.1 400 bad request\r\n\r\n400 bad request\r\n");
                            wuff.close();
                            socket.close();
                            return;
                        }
                    } else {
                        wuff.write("HTTP/1.1 404 not found\r\n\r\n404 not found\r\n");
                        wuff.close();
                        socket.close();
                        return;
                    }
                }  
                catch (ConnectException e) {
                    wuff.write("HTTP/1.1 200 OK\r\n\r\nCannot connect to server, please check the given hostname and port\r\n");
                } catch (UnknownHostException e) {
                    wuff.write("HTTP/1.1 200 OK\r\n\r\nCannot find the host\r\n");
                } catch (SocketTimeoutException e) {
                    wuff.write("HTTP/1.1 408 request timeout\r\n\r\nconnection timed out\r\n");
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
                wuff.close();
                socket.close();
        } catch (IOException e) {}
    }
}

public class ConcHTTPAsk {
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        ServerSocket server = new ServerSocket(port);
        System.out.println("Running server, port: " + port);

        while (true) {
            MyRunnable runIt = new MyRunnable(server.accept());
            new Thread(runIt).start();
        }
    }
}

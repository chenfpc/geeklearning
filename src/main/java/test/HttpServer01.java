package test;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer01 {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8001);
        while (true) {
            Socket socket = serverSocket.accept();
            service(socket);
        }
    }

    public static void service(Socket socket) throws IOException {
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println("HTTP/1.1 200 OK");
        writer.println("Content-Type:text/html;charset=UTF-8");
        String body = "hello start";
        writer.println("Content-Length:"+body.getBytes().length);
        writer.println();
        writer.write(body);
        writer.close();
        socket.close();
    }
}

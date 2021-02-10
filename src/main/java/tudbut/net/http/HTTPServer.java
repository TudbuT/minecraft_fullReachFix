package tudbut.net.http;

import de.tudbut.io.StreamReader;
import de.tudbut.io.StreamWriter;
import de.tudbut.type.Stoppable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

public class HTTPServer implements Stoppable {
    private final int port;
    private final ServerSocket serverSocket;
    private final HTTPResponse serverError;
    private final ArrayList<HTTPHandler> handlers = new ArrayList<>();
    private final Executor executor;

    public HTTPServer(int portIn, HTTPResponse serverErrorIn, Executor executorIn) throws IOException {
        port = portIn;
        serverError = serverErrorIn;
        serverSocket = new ServerSocket(port);
        executor = executorIn;
    }

    public void listen() {
        new Thread(() -> {
            Socket socket;
            while (!isStopped()) {
                try {
                    socket = serverSocket.accept();
                    Socket finalSocket = socket;
                    executor.execute(() -> {
                        try {
                            List<HTTPHandler> handlers = Arrays.asList(this.handlers.toArray(new HTTPHandler[0]));

                            String s;
                            ArrayList<HTTPHeader> headers = new ArrayList<>();
                            StringBuilder fullRequest = new StringBuilder();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(finalSocket.getInputStream()));
                            int line = 0;
                            while ((s = reader.readLine()) != null) {
                                fullRequest.append(s).append("\n");
                                if (s.equals("")) {
                                    break;
                                }
                                if (line != 0) {
                                    headers.add(new HTTPHeader(s.split(": ")[0], s.split(": ")[1]));
                                }
                                line++;
                            }
                            int contentLength = 0;
                            for (HTTPHeader header : headers) {
                                if (header.key().equalsIgnoreCase("Content-Length")) {
                                    contentLength = Integer.parseInt(header.value());
                                }
                            }
                            for (int i = 0; i < contentLength; i++) {
                                fullRequest.append((char) reader.read());
                            }
                            for (HTTPHandler handler : handlers) {
                                HTTPServerRequest request = new HTTPServerRequest(fullRequest.toString(), finalSocket);
                                handler.handle(request);
                            }
                        } catch (Throwable e) {
                            try {
                                new StreamWriter(finalSocket.getOutputStream()).writeChars(serverError.value.toCharArray());
                            } catch (IOException ignore) {
                            }
                        }
                    });
                } catch (IOException ignore) { }
            }
        }).start();
    }

    public void addHandler(HTTPHandler handler) {
        handlers.add(handler);
    }

    public interface HTTPHandler {
        void handle(HTTPServerRequest request) throws Exception;
    }
}

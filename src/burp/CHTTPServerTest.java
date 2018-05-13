package burp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class CHTTPServerTest extends Thread{
	private final IBurpCollaboratorClientContext ccc;
	private final IExtensionHelpers helpers;

	public CHTTPServerTest(IBurpCollaboratorClientContext ccc,
			IExtensionHelpers helpers) {
		this.ccc = ccc;
		this.helpers = helpers;
	}

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/getProperty", new getProperty());
        
        server.createContext("/generatePayload", new generatePayload());

        server.createContext("/fetchAllCollaboratorInteractions", new fetchAllCollaboratorInteractions());

        server.createContext("/fetchCollaboratorInteractionsFor", new fetchCollaboratorInteractionsFor());

        server.createContext("/fetchAllInfiltratorInteractions", new fetchAllInfiltratorInteractions());

        server.createContext("/fetchInfiltratorInteractionsFor", new fetchInfiltratorInteractionsFor());

        server.createContext("/getCollaboratorServerLocation", new getCollaboratorServerLocation());

        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class getProperty implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "This is the response";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    static class generatePayload implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "This is the response";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    static class fetchAllCollaboratorInteractions implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "This is the response";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    static class fetchCollaboratorInteractionsFor implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "This is the response";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    static class fetchAllInfiltratorInteractions implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "To Do ....";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    static class fetchInfiltratorInteractionsFor implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "To Do ...";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    static class getCollaboratorServerLocation implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "This is the response";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    

}
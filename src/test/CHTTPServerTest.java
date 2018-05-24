package test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import burp.IBurpCollaboratorClientContext;
import burp.IExtensionHelpers;

public class CHTTPServerTest extends Thread{
	private final IBurpCollaboratorClientContext ccc;
	private final IExtensionHelpers helpers;

	public CHTTPServerTest(IBurpCollaboratorClientContext ccc,
			IExtensionHelpers helpers) {
		this.ccc = ccc;
		this.helpers = helpers;
	}

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);
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
            System.out.println(response.getBytes().length);
            System.out.println(response.getBytes().toString());
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
            System.out.println(response.getBytes().length);
            System.out.println(response.getBytes().toString());
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
        	Map<String, String> params = queryToMap(t.getRequestURI().getQuery()); 
        	String payload =  params.get("payload");
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
    public static Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }

}
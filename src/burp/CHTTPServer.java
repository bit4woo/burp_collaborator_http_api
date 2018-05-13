package burp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class CHTTPServer extends Thread{
	private final IBurpCollaboratorClientContext ccc;
	private final IExtensionHelpers helpers;
	private final PrintWriter stdout;//用于输出，主要用于代码调试
	private final BurpExtender BE;
	HttpServer server;

	public CHTTPServer(BurpExtender BE) {
		this.ccc = BE.ccc;
		this.helpers = BE.helpers;
		this.stdout = BE.stdout;
		this.BE = BE;
		try {
			server = HttpServer.create(new InetSocketAddress(8000), 0);
			this.stdout.println("Http server started at port 8000");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			this.stdout.println(e);
		}
	}
	public void exit() {
		server.stop(MAX_PRIORITY);
		this.stdout.println("Http server stopped!");
	}
    
    public void run(){
		server.createContext("/generatePayload", new generatePayload(this.ccc));
		server.createContext("/fetchAllCollaboratorInteractions", new fetchAllCollaboratorInteractions(this.ccc));
		server.createContext("/fetchCollaboratorInteractionsFor", new fetchCollaboratorInteractionsFor(this.ccc,this.helpers,this.stdout));
		server.createContext("/fetchAllInfiltratorInteractions", new fetchAllInfiltratorInteractions(this.ccc));
		server.createContext("/fetchInfiltratorInteractionsFor", new fetchInfiltratorInteractionsFor(this.ccc));
		server.createContext("/getCollaboratorServerLocation", new getCollaboratorServerLocation(this.ccc));
		ExecutorService httpThreadPool = Executors.newFixedThreadPool(10);//
		server.setExecutor(httpThreadPool);
		server.start();

    }
    
    static class generatePayload implements HttpHandler {
    	private final IBurpCollaboratorClientContext ccc;

    	public generatePayload(IBurpCollaboratorClientContext ccc) {
    		this.ccc = ccc;
    	}//python中的__init__()
    	
        @Override
        public void handle(HttpExchange t) throws IOException {
            String payload = ccc.generatePayload(true);
            String response = payload;
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    static class fetchAllCollaboratorInteractions implements HttpHandler {
    	private final IBurpCollaboratorClientContext ccc;

    	public fetchAllCollaboratorInteractions(IBurpCollaboratorClientContext ccc) {
    		this.ccc = ccc;
    	}
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "No Need TO Use this Method";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    static class fetchCollaboratorInteractionsFor implements HttpHandler {
    	private final IBurpCollaboratorClientContext ccc;
    	private final IExtensionHelpers helpers;
    	private final PrintWriter stdout;
    	
    	public fetchCollaboratorInteractionsFor(IBurpCollaboratorClientContext ccc,IExtensionHelpers helpers,PrintWriter stdout) {
    		this.ccc = ccc;
    		this.helpers =helpers;
    		this.stdout = stdout;
    	}
    	
        @Override
        public void handle(HttpExchange t) throws IOException {
        	//http://127.0.0.1:8000/fetchCollaboratorInteractionsFor?payload=xxxxx
        	Map<String, String> params = queryToMap(t.getRequestURI().getQuery()); 
        	String payload =  params.get("payload");
    		final List<IBurpCollaboratorInteraction> bci = ccc.fetchCollaboratorInteractionsFor(payload);
    		stdout.println(bci.size()+" record found:\n");
    		String response ="";
    		for (IBurpCollaboratorInteraction interaction : bci) {
    			final Map<String, String> props = interaction.getProperties();
    			stdout.println(props);
    	    	
    			/*
    			for (final Map.Entry<String, String> entry : props.entrySet()) {
    				final String k = entry.getKey();
    				if (k.equals("interaction_id")) continue;
    				final String v = entry.getValue();
    				if (k.equals("request") || k.equals("response") || k.equals("raw_query")) {
    					final byte[] buf = helpers.base64Decode(v);
    				} else if (k.equals("client_ip")) {
    					final byte[] buf = InetAddress.getByName(v).getAddress();
    				}
    			}*/
    		} 
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            stdout.println(response.getBytes().length);
            stdout.println(response.getBytes().toString());
            os.write(response.getBytes());
            os.close();
        }
    }
    static class fetchAllInfiltratorInteractions implements HttpHandler {
    	private final IBurpCollaboratorClientContext ccc;

    	public fetchAllInfiltratorInteractions(IBurpCollaboratorClientContext ccc) {
    		this.ccc = ccc;
    	}
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
    	private final IBurpCollaboratorClientContext ccc;

    	public fetchInfiltratorInteractionsFor(IBurpCollaboratorClientContext ccc) {
    		this.ccc = ccc;
    	}
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
    	private final IBurpCollaboratorClientContext ccc;

    	public getCollaboratorServerLocation(IBurpCollaboratorClientContext ccc) {
    		this.ccc = ccc;
    	}
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "No Need to use this method";
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
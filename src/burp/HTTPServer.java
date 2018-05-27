package burp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HTTPServer extends Thread{
	private final IBurpCollaboratorClientContext ccc;
	private final IExtensionHelpers helpers;
	private final PrintWriter stdout;
	private final BurpExtender BE;
	HttpServer server;

	public HTTPServer(BurpExtender BE) {
		this.ccc = BE.ccc;
		this.helpers = BE.helpers;
		this.stdout = BE.stdout;
		this.BE = BE;
		try {
			server = HttpServer.create(new InetSocketAddress(8000), 0);
			//String ip_port = server.getAddress().toString();
			try {
				String ip = getLocalHostLANAddress().getHostAddress();
				this.stdout.println("Http server started at http://"+ip+":8000");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				String ip_port = server.getAddress().toString();
				this.stdout.println("Http server started at http://"+ip_port);
			}
			
			
		} catch (IOException e) {
			this.stdout.println(e);
		}
	}
	public void exit() {
		server.stop(0);
		this.stdout.println("Http server stopped!");
	}
    
    public void run(){
		server.createContext("/generatePayload", new generatePayload(this.ccc));
    	//http://127.0.0.1:8000/fetchFor?payload=xxxxx
		server.createContext("/fetchFor", new fetchCollaboratorInteractionsFor(this.BE));
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
    
    static class fetchCollaboratorInteractionsFor implements HttpHandler {
    	private final IBurpCollaboratorClientContext ccc;
    	private final IExtensionHelpers helpers;
    	private final PrintWriter stdout;
    	private List<String> typeList = new ArrayList<>();
    	
    	public fetchCollaboratorInteractionsFor(BurpExtender BE) {
    		this.ccc = BE.ccc;
    		this.helpers = BE.helpers;
    		this.stdout = BE.stdout;
        	typeList.add("http");
        	typeList.add("https");
        	typeList.add("dns");
        	typeList.add("smtp");
        	typeList.add("smtps");
        	typeList.add("ftp");
    	}
    	
        public Map<String,String> dealResponse(Map<String,String> props){
        	props.remove("interaction_type");
    		props.remove("interaction_id");
    		for (Entry<String, String> entry : props.entrySet()) {
    			String k = entry.getKey();
    			String v = entry.getValue();
    			if (k.equals("request") || k.equals("response") || k.equals("raw_query")) {
    				final byte[] buf = helpers.base64Decode(v);
    				props.put(k, new String(buf));
    			}
    		}
    		return props;
        }
    	
        @Override
        public void handle(HttpExchange t) throws IOException {
        	String response ="";
        	Map<String, String> params = queryToMap(t.getRequestURI().getQuery()); 
        	String payload =  params.get("payload");
        	String type = params.get("type");
        	stdout.println("your query type is: "+type+"\n");
        	
        	
    		final List<IBurpCollaboratorInteraction> bci = ccc.fetchCollaboratorInteractionsFor(payload);
    		stdout.println(bci.size()+" record(s) found in total\n");
    		
        	if (type.toLowerCase().equals("all") || type==null) {//获取所有记录
        		stdout.println("Fetching all records...\n");
        		for (IBurpCollaboratorInteraction interaction : bci) {
        			Map<String, String> props = interaction.getProperties();
        			props = dealResponse(props);
        			response += props.toString();
        			stdout.println(props);
        			stdout.print("\n");
        		}
        	}
        	else if(typeList.contains(type.toLowerCase())) {//获取指定类型的记录
        		stdout.println("Fetching "+type+" records...\n");
        		for (IBurpCollaboratorInteraction interaction : bci) {
        			if (interaction.getProperty("type").toLowerCase().equals(type.toLowerCase())) {
            			Map<String, String> props = interaction.getProperties();
            			props = dealResponse(props);
            			response += props.toString();
            			stdout.println(props);
            			stdout.print("\n");
        			}
        		}
        	}else {//类型错误
        		response = "Error, wrong type";
        	}
    		

            //t.sendResponseHeaders(200, response.length());//长度如果不匹配，输出将没有内容；大概是时间中文编码导致的获取长度不一致。
    		//https://docs.oracle.com/javase/7/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/HttpExchange.html
            t.sendResponseHeaders(200,0);
            
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
    

    public static InetAddress getLocalHostLANAddress() throws Exception {
	    try {
	        InetAddress candidateAddress = null;
	        // 遍历所有的网络接口
	        for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
	            NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
	            // 在所有的接口下再遍历IP
	            for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
	                InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
	                if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
	                    if (inetAddr.isSiteLocalAddress()) {
	                        // 如果是site-local地址，就是它了
	                        return inetAddr;
	                    } else if (candidateAddress == null) {
	                        // site-local类型的地址未被发现，先记录候选地址
	                        candidateAddress = inetAddr;
	                    }
	                }
	            }
	        }
	        if (candidateAddress != null) {
	            return candidateAddress;
	        }
	        // 如果没有发现 non-loopback地址.只能用最次选的方案
	        InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
	        return jdkSuppliedAddress;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}
}
package test;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class test1 {
	public static void main(String args[]){
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(8011), 0);
			server.start();
			InetSocketAddress x = server.getAddress();
			String ip_port = server.getAddress().toString();
			System.out.println("Http server started at "+x.getHostName());
			System.out.println("Http server started at "+x.getPort());
			System.out.println("Http server started at "+x.toString());
			System.out.println("Http server started at "+x.getPort());
			System.out.println("Http server started at "+x.getPort());
			System.out.println("Http server started at "+x.getPort());
		} catch (IOException e) {
			//
		}
	}
}

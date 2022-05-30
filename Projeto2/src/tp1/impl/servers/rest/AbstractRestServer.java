package tp1.impl.servers.rest;

import java.net.URI;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import tp1.impl.discovery.Discovery;
import tp1.impl.servers.common.AbstractServer;
import tp1.impl.tls.InsecureHostnameVerifier;
import util.IP;

public abstract class AbstractRestServer extends AbstractServer {
	
	protected static String SERVER_BASE_URI = "http://%s:%s/rest";
	
	protected AbstractRestServer(Logger log, String service, int port) {
		super(log, service, port);
	}


	protected void start() {
		try {
			String ip = IP.hostAddress();
			String serverURI = String.format(SERVER_BASE_URI, ip, port);

			ResourceConfig config = new ResourceConfig();
			registerResources(config);

			JdkHttpServerFactory.createHttpServer(URI.create(serverURI.replace(ip, INETADDR_ANY)), config, SSLContext.getDefault());

			Discovery.getInstance().announce(service, serverURI);

			Log.info(String.format("%s Server ready @ %s\n", service, serverURI));

		} catch (Exception e){
			Log.severe(e.getMessage());
		}

	}
	
	abstract void registerResources( ResourceConfig config );
}

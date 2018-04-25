package launch;

import java.io.File;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import utils.campus.Logger;

public class Main {

	public static void main(String[] args) throws Exception {

		String webappDirLocation = "src/main/webapp/";
		Tomcat tomcat = new Tomcat();

		// The port that we should run on can be set into an environment
		// variable
		// Look for that variable and default to 8080 if it isn't there.
		String webPort = System.getenv("PORT");
		if (webPort == null || webPort.isEmpty()) {
			webPort = "8080";
		}
		tomcat.setPort(Integer.valueOf(webPort));
		
		Logger.log("Setting port: " + Integer.valueOf(webPort));
		
		logConnector(tomcat);
		
		tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());

		System.out.println("configuring app with basedir: "
				+ new File("./" + webappDirLocation).getAbsolutePath());

		tomcat.start();
		tomcat.getServer().await();
	}

	private static void logConnector(Tomcat tomcat) {
		Connector c = tomcat.getConnector();
		Logger.log("ToString: " + c.toString());
		Logger.log("Domain: " + c.getDomain());
		Logger.log("Protocol: " + c.getProtocol());
		Logger.log("Info: " + c.getInfo());
		Logger.log("Proxy name: " + c.getProxyName());
		Logger.log("Proxy port: " + c.getProxyPort());
		Logger.log("Scheme: " + c.getScheme());
		Logger.log("Port: " + c.getPort());
		Logger.log("Local Port: " +  c.getLocalPort());
		Logger.log("Redirect Port: " + c.getRedirectPort());
		Logger.log("Is secure: " + c.getSecure());
		Logger.log("");
		Logger.log("");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

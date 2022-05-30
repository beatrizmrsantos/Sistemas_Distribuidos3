package tp1.impl.servers.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.jersey.server.ResourceConfig;
import tp1.impl.servers.rest.AbstractRestServer;
import tp1.impl.servers.rest.FilesDropBoxResources;
import tp1.impl.servers.rest.FilesRestServer;
import tp1.api.service.java.Files;
import tp1.impl.servers.rest.util.GenericExceptionMapper;
import util.Debug;
import util.Token;

public class FilesDropBoxRestServer extends AbstractRestServer {
    public static final int PORT = 5678;

    private static Logger Log = Logger.getLogger(FilesDropBoxRestServer.class.getName());


    FilesDropBoxRestServer() {
        super(Log, Files.SERVICE_NAME, PORT);
    }

    @Override
    void registerResources(ResourceConfig config) {
        config.register( FilesDropBoxResources.class );
        config.register( GenericExceptionMapper.class );
//		config.register( CustomLoggingFilter.class);
    }

    public static void main(String[] args) throws Exception {

        Debug.setLogLevel( Level.INFO, Debug.TP1);

        Token.set( args.length == 0 ? "" : args[0] );

        new FilesRestServer().start();
    }
}

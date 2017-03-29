package pmackowski.grpc.server.starter;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import static io.grpc.ServerInterceptors.intercept;

@Slf4j
public class GrpcServerRunner implements DisposableBean {

    private static final String GRPC_DEFAULT_PORT = "6565";

    private static final String GRPC_SERVER_STARTED_MESSAGE = "Grpc server started, listening on port {}. Activated services {}";
    private static final String GRPC_SERVER_NOT_STARTED_MESSAGE = "Grcp server has not been started";
    private static final String GRPC_SERVER_INCORRECT_PORT_MESSAGE = "Incorrect port {}";
    private static final String GRPC_SERVER_STOPPED_MESSAGE = "Grcp server has been stopped";
    private static final String GRPC_SERVER_SHUTTING_DOWN_MESSAGE = "Shutting down Grpc server ...";

    private List<BindableService> grpcEndpoints = new ArrayList<>();
    private List<Server> servers = new ArrayList<>();
    @Autowired
    private Environment environment;

    public GrpcServerRunner(List<BindableService> grpcEndpoints) {
        this.grpcEndpoints = grpcEndpoints;
    }

    @PostConstruct
    public void startServers() {
        Map<Integer, List<BindableService>> enpointsByPort = groupEnpointsByPort();
        enpointsByPort.forEach((port, grpcEndpoints) -> {
            Server server = startServer(port, grpcEndpoints);
            log.info(GRPC_SERVER_STARTED_MESSAGE, port, getServiceNames(grpcEndpoints));
            servers.add(server);
            startDaemonAwaitThread(server);
        });
    }


    private Map<Integer, List<BindableService>> groupEnpointsByPort() {
        return grpcEndpoints.stream()
                .collect(Collectors.groupingBy(this::getPort));
    }

    private int getPort(BindableService grpcEndpoints) {
        String portProperty = grpcEndpoints.getClass().getAnnotation(GrpcEndpoint.class).portProperty();
        String portValue = environment.getProperty(portProperty, GRPC_DEFAULT_PORT);
        try {
            return Integer.parseInt(portValue);
        } catch (NumberFormatException e) {
            log.error(GRPC_SERVER_INCORRECT_PORT_MESSAGE, portValue);
            throw new GrpcException(GRPC_SERVER_NOT_STARTED_MESSAGE, e);
        }
    }

    private List<String> getServiceNames(List<BindableService> grpcEndpoints) {
        return grpcEndpoints.stream()
                    .map(grpcEndpoint -> grpcEndpoint.getClass().getName())
                    .collect(Collectors.toList());
    }

    private Server startServer(int port, List<BindableService> grpcEndpoints) {

        try {
            NettyServerBuilder builder = NettyServerBuilder.forPort(port);
            grpcEndpoints.forEach(grpcEndpoint ->
                builder.addService(intercept(grpcEndpoint.bindService()))
            );
            return builder.build().start();
        } catch (IOException e) {
            throw new GrpcException(GRPC_SERVER_NOT_STARTED_MESSAGE, e);
        }
    }

    private void startDaemonAwaitThread(Server server) {
        Thread awaitThread = new Thread(() -> {
            try {
                server.awaitTermination();
            } catch (InterruptedException e) {
                log.error(GRPC_SERVER_STOPPED_MESSAGE, e);
            }
        });
        awaitThread.setDaemon(false);
        awaitThread.start();
    }

    @Override
    public void destroy() throws Exception {
        log.info(GRPC_SERVER_SHUTTING_DOWN_MESSAGE);
        servers.forEach(Server::shutdown);
        log.info(GRPC_SERVER_STOPPED_MESSAGE);
    }
}

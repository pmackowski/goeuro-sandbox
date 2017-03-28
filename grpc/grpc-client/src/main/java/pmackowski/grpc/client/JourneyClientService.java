package pmackowski.grpc.client;

import io.grpc.ManagedChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pmackowski.grpc.model.JourneyOuterClass.Journey;
import pmackowski.grpc.model.JourneyOuterClass.JourneyQuery;
import pmackowski.grpc.model.JourneyServiceGrpc;

@Component
public class JourneyClientService {

    private JourneyServiceGrpc.JourneyServiceBlockingStub server;

    @Autowired
    public JourneyClientService(ManagedChannel messageChannel) {
        server = JourneyServiceGrpc.newBlockingStub(messageChannel);
    }

    public Journey query(JourneyQuery journeyQuery) {
        return server.query(journeyQuery);
    }

}

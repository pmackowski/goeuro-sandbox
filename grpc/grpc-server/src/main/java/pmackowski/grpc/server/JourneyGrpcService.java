package pmackowski.grpc.server;

import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import pmackowski.grpc.model.JourneyOuterClass.Journey;
import pmackowski.grpc.model.JourneyOuterClass.JourneyQuery;
import pmackowski.grpc.model.JourneyServiceGrpc.JourneyServiceImplBase;

@GRpcService
public class JourneyGrpcService extends JourneyServiceImplBase {

    @Override
    public void query(JourneyQuery request, StreamObserver<Journey> responseObserver) {
        responseObserver.onNext(exampleJourney(request));
        responseObserver.onCompleted();
    }

    private Journey exampleJourney(JourneyQuery journeyQuery) {
        return Journey.newBuilder()
                .setDeparture(journeyQuery.getDeparture())
                .setArrival(journeyQuery.getArrival())
                .setPrice(11111)
                .build();
    }

}

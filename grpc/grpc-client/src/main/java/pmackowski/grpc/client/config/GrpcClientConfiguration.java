package pmackowski.grpc.client.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfiguration {

    @Bean
    public ManagedChannel grpcManagedChannel() {
        return ManagedChannelBuilder
                .forAddress("localhost", 7777)
                .usePlaintext(true)
                .build();
    }

}

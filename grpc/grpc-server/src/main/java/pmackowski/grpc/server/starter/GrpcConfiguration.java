package pmackowski.grpc.server.starter;

import io.grpc.BindableService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GrpcConfiguration {

    @Bean
    @ConditionalOnBean(annotation = GrpcEndpoint.class)
    public GrpcServerRunner grpcServerRunner(List<BindableService> grpcEndpoints){
        return new GrpcServerRunner(grpcEndpoints);
    }

}

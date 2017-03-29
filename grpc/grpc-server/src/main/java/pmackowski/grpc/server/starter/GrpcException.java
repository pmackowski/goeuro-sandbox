package pmackowski.grpc.server.starter;

public class GrpcException extends RuntimeException {

    public GrpcException(String message, Throwable cause) {
        super(message, cause);
    }
}

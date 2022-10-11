package pe.edu.unmsm.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pe.edu.unmsm.shared.RequestValidator;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderHandler {
    private final OrderService orderService;
    private final RequestValidator requestValidator;

    public Mono<ServerResponse> getAllOrders() {
        log.info("Get all orders invoked.");
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(orderService.getOrders(), Order.class);
    }

    public Mono<ServerResponse> createOrder(ServerRequest request) {
        return request.bodyToMono(Order.class)
                .switchIfEmpty(Mono.error(new RuntimeException("Request must not be null")))
                .doOnNext(order -> log.info("Create order invoked with: {}", order))
                .doOnNext(requestValidator::validate)
                .flatMap(orderService::createOrder)
                .flatMap(order -> OrderStatus.FAILURE.equals(order.getOrderStatus())
                        ? Mono.error(new RuntimeException("Order processing failed, please try again later. " + order.getResponseMessage()))
                        : Mono.just(order))
                .flatMap(order -> ServerResponse.created(URI.create(request.uri().toString().concat("/").concat(order.getId()))).bodyValue(order));
    }
}

package pe.edu.unmsm.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepo orderRepo;
    private final OrderProducer orderProducer;

    public Mono<Order> createOrder(Order order) {
        log.info("Create order invoked with: {}", order);
        return Mono.just(order)
                .map(o -> o.toBuilder().lineItems(o.getLineItems().stream().filter(l -> l.getQuantity() > 0).toList()).build())
                .flatMap(orderRepo::save)
                .map(o -> o.toBuilder().orderStatus(OrderStatus.INITIATION_SUCCESS).build())
                .doOnNext(orderProducer::sendMessage)
                .onErrorResume(err -> Mono.just(order.toBuilder().orderStatus(OrderStatus.FAILURE).responseMessage(err.getMessage()).build()))
                .flatMap(orderRepo::save);
    }

    public Flux<Order> getOrders() {
        log.info("Get all orders invoked.");
        return orderRepo.findAll();
    }
}

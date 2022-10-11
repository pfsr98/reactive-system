package pe.edu.unmsm.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderConsumer {
    private final ProductService productService;
    private final OrderProducer orderProducer;

    @KafkaListener(topics = "orders", containerFactory = "orderKafkaListenerContainerFactory")
    public void consume(Order order) {
        log.info("Order received to process: {}", order);
        if (OrderStatus.RESERVE_INVENTORY.equals(order.getOrderStatus())) {
            productService.handleOrder(order)
                    .doOnSuccess(o -> {
                        log.info("Order processed successfully.");
                        orderProducer.sendMessage(order.toBuilder().orderStatus(OrderStatus.INVENTORY_SUCCESS).build());
                    })
                    .doOnError(e -> {
                        if (log.isDebugEnabled()) log.error("Order failed to process: " + e);
                        orderProducer.sendMessage(order.toBuilder().orderStatus(OrderStatus.INVENTORY_FAILURE).responseMessage(e.getMessage()).build());
                    })
                    .subscribe();
        } else if (OrderStatus.REVERT_INVENTORY.equals(order.getOrderStatus())) {
            productService.revertOrder(order)
                    .doOnSuccess(o -> {
                        log.info("Order reverted successfully.");
                        orderProducer.sendMessage(order.toBuilder().orderStatus(OrderStatus.INVENTORY_REVERT_SUCCESS).build());
                    })
                    .doOnError(e -> {
                        if (log.isDebugEnabled()) log.info("Order failed to revert: " + e);
                        orderProducer.sendMessage(order.toBuilder().orderStatus(OrderStatus.INVENTORY_REVERT_FAILURE).responseMessage(e.getMessage()).build());
                    })
                    .subscribe();
        }
    }
}

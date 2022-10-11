package pe.edu.unmsm.shipment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderConsumer {
    private final ShippingService shippingService;
    private final OrderProducer orderProducer;

    @KafkaListener(topics = "orders", containerFactory = "orderKafkaListenerContainerFactory")
    public void consume(Order order) {
        log.info("Order received to process: {}", order);
        if (OrderStatus.PREPARE_SHIPPING.equals(order.getOrderStatus())) {
            shippingService.handleOrder(order)
                    .doOnSuccess(o -> {
                        log.info("Order processed successfully.");
                        orderProducer.sendMessage(order.toBuilder().orderStatus(OrderStatus.SHIPPING_SUCCESS).shippingDate(o.getShippingDate()).build());
                    })
                    .doOnError(e -> {
                        if (log.isErrorEnabled()) log.error("Order failed to process: " + e);
                        orderProducer.sendMessage(order.toBuilder().orderStatus(OrderStatus.SHIPPING_FAILURE).responseMessage(e.getMessage()).build());
                    })
                    .subscribe();
        }
    }
}

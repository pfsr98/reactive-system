package pe.edu.unmsm.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderConsumer {
    private final OrderRepo orderRepo;
    private final OrderProducer orderProducer;

    @KafkaListener(topics = "orders", containerFactory = "orderKafkaListenerContainerFactory")
    public void consume(Order order) {
        log.info("Order received to process: {}", order);
        if (OrderStatus.INITIATION_SUCCESS.equals(order.getOrderStatus())) {
            orderRepo.findById(order.getId())
                    .map(o -> o.toBuilder().orderStatus(OrderStatus.RESERVE_INVENTORY).build())
                    .doOnNext(orderProducer::sendMessage)
                    .map(o -> o.toBuilder().orderStatus(order.getOrderStatus()).responseMessage(order.getResponseMessage()).build())
                    .flatMap(orderRepo::save)
                    .subscribe();
        } else if (OrderStatus.INVENTORY_SUCCESS.equals(order.getOrderStatus())) {
            orderRepo.findById(order.getId())
                    .map(o -> o.toBuilder().orderStatus(OrderStatus.PREPARE_SHIPPING).build())
                    .doOnNext(orderProducer::sendMessage)
                    .map(o -> o.toBuilder().orderStatus(order.getOrderStatus()).responseMessage(order.getResponseMessage()).build())
                    .flatMap(orderRepo::save)
                    .subscribe();
        } else if (OrderStatus.SHIPPING_FAILURE.equals(order.getOrderStatus())) {
            orderRepo.findById(order.getId())
                    .map(o -> o.toBuilder().orderStatus(OrderStatus.REVERT_INVENTORY).responseMessage(order.getResponseMessage()).build())
                    .doOnNext(orderProducer::sendMessage)
                    .map(o -> o.toBuilder().orderStatus(order.getOrderStatus()).responseMessage(order.getResponseMessage()).build())
                    .flatMap(orderRepo::save)
                    .subscribe();
        } else {
            orderRepo.findById(order.getId())
                    .map(o -> o.toBuilder().orderStatus(order.getOrderStatus()).responseMessage(order.getResponseMessage()).build())
                    .flatMap(orderRepo::save)
                    .subscribe();
        }
    }
}

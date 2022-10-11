package pe.edu.unmsm.shipment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingService {
    private final ShipmentRepo shipmentRepo;

    @Transactional
    public Mono<Order> handleOrder(Order order) {
        log.info("Handle order invoked with: {}", order);
        return Mono.just(order)
                .flatMap(o -> {
                    LocalDate shippingDate;
                    if (LocalTime.now().isAfter(LocalTime.parse("10:00")) && LocalTime.now().isBefore(LocalTime.parse("18:00"))) {
                        shippingDate = LocalDate.now().plusDays(1);
                    } else {
                        return Mono.error(new RuntimeException("The current time is off the limits to place order."));
                    }
                    return shipmentRepo.save(Shipment.builder().shippingDate(shippingDate).address(order.getShippingAddress()).build());
                })
                .map(shipment -> order.toBuilder().shippingDate(shipment.getShippingDate()).orderStatus(OrderStatus.SUCCESS).build());
    }
}

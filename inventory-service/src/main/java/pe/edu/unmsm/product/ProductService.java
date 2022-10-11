package pe.edu.unmsm.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepo productRepo;

    @Transactional
    public Mono<Order> handleOrder(Order order) {
        log.info("Handle order invoked with: {}", order);
        return Flux.fromIterable(order.getLineItems())
                .flatMap(lineItem -> productRepo.findById(lineItem.getProductId()))
                .flatMap(product -> {
                    int quantity = order.getLineItems().stream()
                            .filter(lineItem -> lineItem.getProductId().equals(product.getId()))
                            .findAny().get().getQuantity();
                    if (product.getStock() >= quantity) {
                        Product updatedProduct = product.toBuilder().stock(product.getStock() - quantity).build();
                        return productRepo.save(updatedProduct);
                    } else {
                        return Mono.error(new RuntimeException("Product is out of stock: " + product.getId()));
                    }
                })
                .then(Mono.just(order.toBuilder().orderStatus(OrderStatus.SUCCESS).build()));
    }

    @Transactional
    public Mono<Order> revertOrder(Order order) {
        log.info("Revert order invoked with: {}", order);
        return Flux.fromIterable(order.getLineItems())
                .flatMap(lineItem -> productRepo.findById(lineItem.getProductId()))
                .flatMap(product -> {
                    int quantity = order.getLineItems().stream()
                            .filter(lineItem -> lineItem.getProductId().equals(product.getId()))
                            .toList().get(0).getQuantity();
                    Product updatedProduct = product.toBuilder().stock(product.getStock() + quantity).build();
                    return productRepo.save(updatedProduct);
                })
                .then(Mono.just(order.toBuilder().orderStatus(OrderStatus.SUCCESS).build()));
    }

    public Flux<Product> getProducts() {
        return productRepo.findAll();
    }

    public Mono<Product> createProduct(Product product) {
        return productRepo.save(product);
    }
}

package pe.edu.unmsm.product;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pe.edu.unmsm.shared.RequestValidator;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class ProductHandler {
    private final ProductService productService;
    private final RequestValidator requestValidator;

    public Mono<ServerResponse> getAllProducts() {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productService.getProducts(), Product.class);
    }

    public Mono<ServerResponse> createProduct(ServerRequest request) {
        return request.bodyToMono(Product.class)
                .switchIfEmpty(Mono.error(new RuntimeException("Request must not be null")))
                .doOnNext(requestValidator::validate)
                .flatMap(productService::createProduct)
                .flatMap(product -> ServerResponse.created(URI.create(request.uri().toString().concat("/").concat(product.getId()))).bodyValue(product));
    }
}

package pe.edu.unmsm.order;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface OrderRepo extends ReactiveMongoRepository<Order, String> {
}

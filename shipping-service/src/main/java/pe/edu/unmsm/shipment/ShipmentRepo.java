package pe.edu.unmsm.shipment;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ShipmentRepo extends ReactiveMongoRepository<Shipment, String> {
}

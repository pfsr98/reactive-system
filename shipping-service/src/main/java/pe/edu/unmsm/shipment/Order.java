package pe.edu.unmsm.shipment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Order {
    private String id;
    private String userId;
    private Long total;
    private String paymentMode;
    private Address shippingAddress;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate shippingDate;
    private OrderStatus orderStatus;
    private String responseMessage;
}

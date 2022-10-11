package pe.edu.unmsm.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    @NotNull(message = "Must not be null")
    private String userId;
    private List<LineItem> lineItems;
    private Long total;
    private String paymentMode;
    private Address shippingAddress;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate shippingDate;
    private OrderStatus orderStatus;
    private String responseMessage;
}

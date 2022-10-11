package pe.edu.unmsm.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Address {
    private String name;
    private String house;
    private String street;
    private String city;
    private String zip;
}

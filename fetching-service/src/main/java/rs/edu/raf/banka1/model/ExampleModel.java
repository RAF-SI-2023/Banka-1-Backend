package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ExampleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exampleId;

    @Column(unique = true)
    @NotBlank(message = "Value is mandatory")
    private String value;

}

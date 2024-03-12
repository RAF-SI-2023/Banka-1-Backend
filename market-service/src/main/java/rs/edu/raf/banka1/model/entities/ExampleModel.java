package rs.edu.raf.banka1.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
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

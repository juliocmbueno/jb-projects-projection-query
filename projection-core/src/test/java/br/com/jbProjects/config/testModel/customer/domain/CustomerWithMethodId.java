package br.com.jbProjects.config.testModel.customer.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by julio.bueno on 07/01/2026.
 */
@Getter
@Setter
@Entity
@Table(name = "customers_with_method_id")
public class CustomerWithMethodId {

    private Long code;

    @Column
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getCode() {
        return code;
    }
}

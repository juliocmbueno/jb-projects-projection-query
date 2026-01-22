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
@Table(name = "customers_with_code_id")
public class CustomerWithCodeId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long code;

    @Column
    private String name;

}

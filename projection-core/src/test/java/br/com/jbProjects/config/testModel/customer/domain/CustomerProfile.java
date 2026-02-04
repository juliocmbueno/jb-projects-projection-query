package br.com.jbProjects.config.testModel.customer.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by julio.bueno on 04/02/2026.
 */
@Getter
@Setter
@Entity
public class CustomerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Customer customer;

    @Column
    private String name;
}

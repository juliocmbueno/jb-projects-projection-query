package br.com.jbProjects.config.testModel.address.domain;

import br.com.jbProjects.config.testModel.city.domain.City;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by julio.bueno on 24/11/2025.
 */
@Getter
@Setter
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private City city;

}

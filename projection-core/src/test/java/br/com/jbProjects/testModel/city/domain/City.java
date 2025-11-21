package br.com.jbProjects.testModel.city.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by julio.bueno on 21/11/2025.
 */
@Getter
@Setter
@Entity
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

}

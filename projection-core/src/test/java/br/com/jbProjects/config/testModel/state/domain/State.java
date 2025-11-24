package br.com.jbProjects.config.testModel.state.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by julio.bueno on 24/11/2025.
 */
@Getter
@Setter
@Entity
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

}

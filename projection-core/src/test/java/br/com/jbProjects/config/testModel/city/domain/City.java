package br.com.jbProjects.config.testModel.city.domain;

import br.com.jbProjects.config.testModel.state.domain.State;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private State state;

}

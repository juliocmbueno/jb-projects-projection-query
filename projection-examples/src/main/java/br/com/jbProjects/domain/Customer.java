package br.com.jbProjects.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by julio.bueno on 27/11/2025.
 */
@Getter
@Setter
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private Integer age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Address mainAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Address secondaryAddress;

}

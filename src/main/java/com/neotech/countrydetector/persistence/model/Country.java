package com.neotech.countrydetector.persistence.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "countries", uniqueConstraints = {
        @UniqueConstraint(name = "country_code_uni", columnNames = {"code"}),
        @UniqueConstraint(name = "country_name_uni", columnNames = {"name"})})
public class Country {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "code", nullable = false, length = 2)
    private String code;

    @Column(name= "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "countries")
    private Set<PhonePrefixCode> phonePrefixCodes = new HashSet<>();
}

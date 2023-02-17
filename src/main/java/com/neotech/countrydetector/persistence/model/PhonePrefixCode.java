package com.neotech.countrydetector.persistence.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "prefix_codes", uniqueConstraints = {
        @UniqueConstraint(name = "prefix_code_uni", columnNames = {"prefix_code"})})
public class PhonePrefixCode {
    
    @Id
    @GeneratedValue
    private Integer id;
    
    @Column(name = "prefix_code", nullable = false)
    private String prefixCode;

    @ManyToMany
    @JoinTable(
            name = "prefix_codes_to_countries",
            joinColumns = @JoinColumn(name = "counry_id"),
            inverseJoinColumns = @JoinColumn(name = "prefix_code_id"))
    private Set<Country> countries = new HashSet<>();
}

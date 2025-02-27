package com.sina_reidenbach.insurancePremium.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 *************************************************************************************************************
 * Repräsentiert die Entität einer Region.                                                                   *
 * Wird verwendet, um Regionen mit ihren Städten und einem Berechnungsfaktor zu speichern.                   *
 *************************************************************************************************************
 */
@Getter
@Setter
@Entity
@Table(name = "region", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Region {

    /**
     *********************************************************************************************************
     * Die eindeutige ID der Region, die für die Identifikation des Datensatzes in der DB verwendet wird.    *
     *********************************************************************************************************
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double factor;

    /**
     *********************************************************************************************************
     * Eine Sammlung von Städten, die der Region zugeordnet sind. Diese Beziehung ist eins-zu-viele.         *
     * @see City                                                                                             *
     *********************************************************************************************************
     */
    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<City> cities;

    /**
     *********************************************************************************************************
     * Konstruktor, um eine Region mit dem angegebenen Namen und Faktor zu erstellen.                        *
     *                                                                                                       *
     * @param name Der Name der Region.                                                                      *
     * @param factor Der Berechnungsfaktor der Region.                                                       *
     *********************************************************************************************************
     */
    public Region(String name, double factor) {
        this.name = name;
        this.factor = factor;
    }

    public Region() {}

    /**
     *********************************************************************************************************
     * Konstruktor, um eine Region mit einer ID, Namen und Faktor zu erstellen.                              *
     *
     * @param id Die eindeutige ID der Region.                                                                *
     * @param name Der Name der Region.                                                                      *
     * @param factor Der Berechnungsfaktor der Region.                                                       *
     *********************************************************************************************************
     */
    public Region(long id, String name, double factor) {
        this.id = id;
        this.name = name;
        this.factor = factor;
    }
}

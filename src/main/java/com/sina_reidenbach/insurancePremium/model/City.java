package com.sina_reidenbach.insurancePremium.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 *************************************************************************************************************
 * Repräsentiert die Entität einer Stadt.                                                                    *
 * Wird verwendet, um Städte innerhalb von Regionen zu speichern und deren Postleitzahlen zu verwalten.      *
 *************************************************************************************************************
 */
@Getter
@Setter
@Entity
public class City {

    /**
     *********************************************************************************************************
     * Die eindeutige ID der Stadt, die für die Identifikation des Datensatzes in der DB verwendet wird.     *
     *********************************************************************************************************
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    /**
     *********************************************************************************************************
     * Die Region, zu der die Stadt gehört. Diese Beziehung ist viele-zu-eins.                               *
     * @see Region                                                                                           *
     *********************************************************************************************************
     */
    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    /**
     *********************************************************************************************************
     * Eine Sammlung von Postleitzahlen, die zu der Stadt gehören.                                            *
     * Diese Beziehung ist eins-zu-viele und wird mit der Stadt in Verbindung gebracht.                       *
     * @see Postcode                                                                                        *
     *********************************************************************************************************
     */
    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Postcode> postcodes;

    public City() {}

    /**
     *********************************************************************************************************
     * Konstruktor, um eine City-Instanz mit dem angegebenen Namen zu erstellen.                              *
     *
     * @param name Der Name der Stadt.                                                                         *
     *********************************************************************************************************
     */
    public City(String name) {
        this.name = name;
    }

    /**
     *********************************************************************************************************
     * Konstruktor, um eine City-Instanz mit dem angegebenen Namen und der Region zu erstellen.              *
     *
     * @param name Der Name der Stadt.                                                                         *
     * @param region Die Region, zu der die Stadt gehört.                                                      *
     *********************************************************************************************************
     */
    public City(String name, Region region) {
        this.name = name;
        this.region = region;
    }
}

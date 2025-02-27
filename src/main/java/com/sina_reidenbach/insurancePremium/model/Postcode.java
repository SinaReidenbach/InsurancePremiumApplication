package com.sina_reidenbach.insurancePremium.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 *************************************************************************************************************
 * Repräsentiert die Entität einer Postleitzahl.                                                             *
 * Wird verwendet, um Postleitzahlen einer Stadt und Region zuzuordnen und deren Details zu speichern.       *
 *************************************************************************************************************
 */
@Getter
@Setter
@Entity
public class Postcode {

    /**
     *********************************************************************************************************
     * Die eindeutige ID der Postleitzahl, die für die Identifikation des Datensatzes in der Datenbank verwendet wird. *
     *********************************************************************************************************
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String postcodeValue;

    /**
     *********************************************************************************************************
     * Die Stadt, zu der die Postleitzahl gehört. Diese Beziehung ist viele-zu-eins.                         *
     * @see City                                                                                             *
     *********************************************************************************************************
     */
    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    /**
     *********************************************************************************************************
     * Die Region, zu der die Postleitzahl gehört. Diese Beziehung ist viele-zu-eins.                        *
     * @see Region                                                                                           *
     *********************************************************************************************************
     */
    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;

    /**
     *********************************************************************************************************
     * Konstruktor, um eine Postcode-Instanz mit dem angegebenen Postleitzahlwert zu erstellen.              *
     *                                                                                                       *
     * @param postcodeValue Der Wert der Postleitzahl.                                                       *
     *********************************************************************************************************
     */
    public Postcode(String postcodeValue) {
        this.postcodeValue = postcodeValue;
    }

    public Postcode() {}
}

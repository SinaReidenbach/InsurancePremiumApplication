package com.sina_reidenbach.insurancePremium.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 *************************************************************************************************************
 * Repräsentiert die Entität für ein Fahrzeugmodell und dessen Berechnungsfaktor.                            *
 * Speichert Name und Berechnungsfaktor eines Fahrzeuges.                                                    *
 *************************************************************************************************************
 */
@Getter
@Setter
@Entity
public class Vehicle {

    /**
     *********************************************************************************************************
     * Die eindeutige ID des Fahrzeugmodells. Wird in der Datenbank zur Identifikation des DB verwendet.     *
     *********************************************************************************************************
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double factor;

    /**
     *********************************************************************************************************
     * Konstruktor, um ein Fahrzeug mit einem Namen und einem Berechnungsfaktor zu erstellen.                *
     *                                                                                                       *
     * @param name Der Name des Fahrzeugmodells.                                                             *
     * @param factor Der Berechnungsfaktor für das Fahrzeugmodell.                                           *
     *********************************************************************************************************
     */
    public Vehicle(String name, double factor){
        this.name = name;
        this.factor = factor;
    }

    public Vehicle(){}

    /**
     *********************************************************************************************************
     * Konstruktor, um ein Fahrzeug mit einer ID, einem Namen und einem Berechnungsfaktor zu erstellen.      *
     *                                                                                                       *
     * @param id Die eindeutige ID des Fahrzeugmodells.                                                      *
     * @param name Der Name des Fahrzeugmodells.                                                             *
     * @param factor Der Berechnungsfaktor für das Fahrzeugmodell.                                           *
     *********************************************************************************************************
     */
    public Vehicle(long id, String name, double factor) {
        this.id = id;
        this.name = name;
        this.factor = factor;
    }

}

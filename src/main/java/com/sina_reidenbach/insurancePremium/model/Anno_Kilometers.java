package com.sina_reidenbach.insurancePremium.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 *************************************************************************************************************
 * Repräsentiert die Entität für jährliche Kilometerbereiche und deren Berechnungsfaktor.                    *
 * Wird zur Berechnung der Versicherungsprämie basierend auf den gefahrenen Kilometern verwendet.            *
 *************************************************************************************************************
 */
@Getter
@Setter
@Entity
public class Anno_Kilometers {

    /**
     *********************************************************************************************************
     * Die eindeutige ID der Entität, wird für die Identifikation des Datensatzes in der DB verwendet      . *
     *********************************************************************************************************
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int min;
    private int max;
    private double factor;

    /**
     *********************************************************************************************************
     * Konstruktor, um eine Anno_Kilometers-Instanz mit den angegebenen Werten zu erstellen.                 *
     *                                                                                                       *
     * @param min Der minimale Wert für die jährlichen Kilometer.                                            *
     * @param max Der maximale Wert für die jährlichen Kilometer.                                            *
     * @param factor Der Berechnungsfaktor für die Versicherungsprämie basierend auf den Kilometern.         *
     *********************************************************************************************************
     */
    public Anno_Kilometers(int min, int max, double factor) {
        this.min = min;
        this.max = max;
        this.factor = factor;
    }

    public Anno_Kilometers() {}

    /**
     *********************************************************************************************************
     * Konstruktor, um Anno_Kilometers-Instanz mit den angegebenen Werten zu erstellen, inklusive einer ID.  *
     *                                                                                                       *
     * @param id Die ID der Entität, die zur Identifikation in der Datenbank verwendet wird.                 *
     * @param min Der minimale Wert für die jährlichen Kilometer.                                            *
     * @param max Der maximale Wert für die jährlichen Kilometer.                                            *
     * @param factor Der Berechnungsfaktor für die Versicherungsprämie basierend auf den Kilometern.         *
     *********************************************************************************************************
     */
    public Anno_Kilometers(long id, int min, int max, double factor) {
        this.id = id;
        this.min = min;
        this.max = max;
        this.factor = factor;
    }
}

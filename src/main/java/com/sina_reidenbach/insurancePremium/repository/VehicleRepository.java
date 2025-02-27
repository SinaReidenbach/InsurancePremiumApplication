package com.sina_reidenbach.insurancePremium.repository;

import com.sina_reidenbach.insurancePremium.model.Vehicle;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *************************************************************************************************************
 * Repository-Interface für den Zugriff auf {@link Vehicle}-Entitäten.                                       *
 * Ermöglicht das Speichern und Abrufen von Fahrzeugdaten für die Prämienberechnung.                         *
 *************************************************************************************************************
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    /**
     *********************************************************************************************************
     * Findet ein {@link Vehicle} anhand des Fahrzeugnamens.                                                 *
     * Wird genutzt, um Fahrzeuginformationen für die Prämienberechnung abzurufen.                           *
     *                                                                                                       *
     * @param name Der Name des Fahrzeugs, nach dem gesucht werden soll.                                     *
     * @return Das gefundene {@link Vehicle} oder {@code null}, wenn kein Fahrzeug mit diesem Namen existiert.*
     *********************************************************************************************************
     */
    Vehicle findByName(String name);

    /**
     *********************************************************************************************************
     * Findet ein {@link Vehicle} anhand der Fahrzeug-ID.                                                    *
     * Ermöglicht den gezielten Zugriff auf ein Fahrzeug über dessen eindeutige ID.                          *
     *                                                                                                       *
     * @param id Die eindeutige ID des Fahrzeugs.                                                             *
     * @return Ein {@link Optional} mit dem gefundenen {@link Vehicle} oder leer, wenn keines vorhanden ist.  *
     *********************************************************************************************************
     */
    Optional<Vehicle> findById(Long id);
}

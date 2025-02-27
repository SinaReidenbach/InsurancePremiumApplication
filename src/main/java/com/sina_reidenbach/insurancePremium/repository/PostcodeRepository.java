package com.sina_reidenbach.insurancePremium.repository;

import com.sina_reidenbach.insurancePremium.model.Postcode;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *************************************************************************************************************
 * Repository-Interface für den Zugriff auf {@link Postcode}-Entitäten.                                      *
 * Bietet Methoden zur Suche von Postleitzahlen anhand ihres Wertes oder des Anfangs eines Wertes.           *
 *************************************************************************************************************
 */
public interface PostcodeRepository extends JpaRepository<Postcode, Long> {

    /**
     *********************************************************************************************************
     * Sucht die erste {@link Postcode}-Entität mit dem angegebenen Postleitzahlwert.                        *
     *                                                                                                       *
     * @param postcodeValue Der Postleitzahlwert, nach dem gesucht werden soll.                              *
     * @return Ein {@link Optional} mit der gefundenen Postcode-Entität oder leer, wenn keine gefunden wurde.*
     *********************************************************************************************************
     */
    Optional<Postcode> findFirstByPostcodeValue(String postcodeValue);

    /**
     *********************************************************************************************************
     * Findet alle {@link Postcode}-Entitäten, deren Postleitzahlwert mit dem angegebenen Wert beginnt.      *
     *                                                                                                       *
     * @param postcodeValue Der Anfangswert der Postleitzahl, nach dem gefiltert werden soll.                *
     * @return Eine Liste von {@link Postcode}-Entitäten, die dem Kriterium entsprechen.                     *
     *********************************************************************************************************
     */
    List<Postcode> findByPostcodeValueStartingWith(String postcodeValue);

    /**
     *********************************************************************************************************
     * Prüft, ob eine {@link Postcode}-Entität mit dem angegebenen Postleitzahlwert existiert.               *
     *                                                                                                       *
     * @param postcodeValue Der Postleitzahlwert, dessen Existenz überprüft werden soll.                     *
     * @return {@code true}, wenn die Postleitzahl existiert, andernfalls {@code false}.                     *
     *********************************************************************************************************
     */
    boolean existsByPostcodeValue(String postcodeValue);
}

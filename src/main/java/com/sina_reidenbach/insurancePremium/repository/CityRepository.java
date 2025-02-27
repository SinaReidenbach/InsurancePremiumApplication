package com.sina_reidenbach.insurancePremium.repository;

import com.sina_reidenbach.insurancePremium.model.City;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *************************************************************************************************************
 * Repository-Interface für den Zugriff auf {@link City}-Entitäten.                                          *
 * Bietet Methoden zur Suche von Städten anhand des Namens oder der zugehörigen Postleitzahl.                *
 *************************************************************************************************************
 */
public interface CityRepository extends JpaRepository<City, Long> {

    /**
     *********************************************************************************************************
     * Sucht die erste {@link City}-Entität mit dem angegebenen Stadtnamen.                                  *
     *                                                                                                       *
     * @param name Der Name der Stadt, nach der gesucht werden soll.                                         *
     * @return Ein {@link Optional} mit der gefundenen Stadt oder leer, wenn keine Stadt gefunden wurde.     *
     *********************************************************************************************************
     */
    Optional<City> findFirstByName(String name);

    /**
     *********************************************************************************************************
     * Findet eine {@link City}-Entität anhand eines zugehörigen Postleitzahlwertes.                         *
     *                                                                                                       *
     * @param postcodeValue Der Postleitzahlwert, der der gesuchten Stadt zugeordnet ist.                    *
     * @return Die gefundene {@link City}-Entität oder {@code null}, wenn keine entsprechende Stadt existiert.*
     *********************************************************************************************************
     */
    City findByPostcodes_PostcodeValue(String postcodeValue);
}

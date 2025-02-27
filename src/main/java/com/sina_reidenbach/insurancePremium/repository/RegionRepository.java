package com.sina_reidenbach.insurancePremium.repository;

import com.sina_reidenbach.insurancePremium.model.Region;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *************************************************************************************************************
 * Repository-Interface für den Zugriff auf {@link Region}-Entitäten.                                        *
 * Bietet Methoden zur Suche von Regionen anhand ihres Namens, einer Stadt oder einer Postleitzahl.          *
 *************************************************************************************************************
 */
public interface RegionRepository extends JpaRepository<Region, Long> {

    /**
     *********************************************************************************************************
     * Sucht eine {@link Region}-Entität anhand des angegebenen Regionsnamens.                               *
     *                                                                                                       *
     * @param name Der Name der Region, nach der gesucht werden soll.                                        *
     * @return Ein {@link Optional} mit der gefundenen Region-Entität oder leer, wenn keine gefunden wurde.   *
     *********************************************************************************************************
     */
    Optional<Region> findByName(String name);

    /**
     *********************************************************************************************************
     * Findet eine {@link Region}-Entität basierend auf dem Namen einer zugehörigen Stadt.                   *
     *                                                                                                       *
     * @param cityName Der Name der Stadt, deren Region ermittelt werden soll.                               *
     * @return Die gefundene {@link Region}-Entität oder {@code null}, wenn keine passende Region existiert. *
     *********************************************************************************************************
     */
    Region findByCities_Name(String cityName);

    /**
     *********************************************************************************************************
     * Sucht eine {@link Region}-Entität, deren Postleitzahlwert mit dem angegebenen Wert beginnt.           *
     * Nutzt eine benutzerdefinierte JPQL-Query, um Regionen über Cities und deren Postcodes zu ermitteln.   *
     *                                                                                                       *
     * @param postcodeValue Der Anfangswert der Postleitzahl, nach dem gefiltert werden soll.                *
     * @return Ein {@link Optional} mit der gefundenen Region-Entität oder leer, wenn keine übereinstimmt.    *
     *********************************************************************************************************
     */
    @Query("SELECT r FROM Region r " +
            "JOIN r.cities c " +
            "JOIN c.postcodes p " +
            "WHERE p.postcodeValue LIKE :postcodeValue%")
    Optional<Region> findByPostcodeValueStartingWith(@Param("postcodeValue") String postcodeValue);
}

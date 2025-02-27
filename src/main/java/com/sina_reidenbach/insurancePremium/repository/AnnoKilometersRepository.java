package com.sina_reidenbach.insurancePremium.repository;

import com.sina_reidenbach.insurancePremium.model.Anno_Kilometers;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 *************************************************************************************************************
 * Repository-Interface für den Zugriff auf {@link Anno_Kilometers}-Entitäten.                               *
 * Bietet Methoden zur Abfrage von Kilometerbereichen und deren IDs in der Datenbank.                        *
 *************************************************************************************************************
 */
public interface AnnoKilometersRepository extends JpaRepository<Anno_Kilometers, Long> {

    /**
     *********************************************************************************************************
     * Findet alle Anno_Kilometers-Entitäten, deren Wertebereich den angegebenen KM-Bereich einschließt.     *
     *                                                                                                       *
     * @param min Das minimale Kilometerlimit.                                                               *
     * @param max Das maximale Kilometerlimit.                                                               *
     * @return Eine Liste von passenden {@link Anno_Kilometers}-Einträgen.                                   *
     *********************************************************************************************************
     */
    List<Anno_Kilometers> findByMinLessThanEqualAndMaxGreaterThanEqual(int min, int max);

    /**
     *********************************************************************************************************
     * Sucht eine Anno_Kilometers-Entität anhand ihrer eindeutigen ID.                                       *
     *                                                                                                       *
     * @param id Die eindeutige ID der Entität.                                                              *
     * @return Ein {@link Optional} mit der gefundenen Entität oder leer, wenn keine Entität gefunden wurde. *
     *********************************************************************************************************
     */
    Optional<Anno_Kilometers> findById(Long id);
}

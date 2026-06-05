package com.co.eatupapi.repositories.inventory.categories;

import com.co.eatupapi.domain.inventory.categories.CategoryDomain;
import com.co.eatupapi.domain.inventory.categories.CategoryStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryDomain, UUID> {

    Optional<CategoryDomain> findByName(String name);

    List<CategoryDomain> findByStatus(CategoryStatus status);

    Optional<CategoryDomain> findTopByOrderByCnsDesc();

    /**
     * Consulta nativa que retorna el MAX(cns) actual en la tabla.
     * Se usa en CategorySaveHelper.findNextCns() para garantizar una lectura
     * fresca sin depender del mapeo JPQL de Hibernate, evitando problemas
     * de caché o invisibilidad de entidades insertadas por otros servicios
     * (ej. inventory-consumer) que no tienen el campo location_id.
     */
    @Query(value = "SELECT MAX(cns) FROM categories", nativeQuery = true)
    Long findMaxCns();

    @Query(value = "select pg_advisory_xact_lock(8202401)", nativeQuery = true)
    void lockCategoryCnsCounter();

    // Búsqueda flexible por nombre (LIKE %name%)
    List<CategoryDomain> findByNameContainingIgnoreCase(String name);

    // Búsqueda flexible por tipo (LIKE %type%) - CAMBIADO A CONTAINING
    List<CategoryDomain> findByTypeContainingIgnoreCase(String type);

    List<CategoryDomain> findBySubtypeIgnoreCase(String subtype);
}

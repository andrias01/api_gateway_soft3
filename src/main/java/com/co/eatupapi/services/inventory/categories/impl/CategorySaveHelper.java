package com.co.eatupapi.services.inventory.categories.impl;

import com.co.eatupapi.domain.inventory.categories.CategoryDomain;
import com.co.eatupapi.repositories.inventory.categories.CategoryRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Helper de persistencia para categorías.
 *
 * Usa REQUIRES_NEW para que cada intento de INSERT se ejecute en su propia
 * sub-transacción. Esto es necesario porque PostgreSQL aborta la transacción
 * completa al producirse una violación de unicidad; sin este helper, todos los
 * reintentos del loop en CategoryServiceImpl fallarían con "transacción abortada".
 *
 * El advisory lock sigue siendo mantenido por la transacción externa de
 * CategoryServiceImpl, garantizando serialización entre solicitudes concurrentes.
 */
@Component
public class CategorySaveHelper {

    private final CategoryRepository categoryRepository;

    public CategorySaveHelper(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long findNextCns() {
        Long maxCns = categoryRepository.findMaxCns();
        return maxCns != null ? maxCns + 1 : 1L;
    }

    /**
     * Intenta guardar la categoría en una sub-transacción independiente.
     * Si falla (p.ej. por CNS duplicado), solo se hace rollback de esta
     * sub-transacción; la transacción externa permanece intacta y puede reintentar.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CategoryDomain trySave(CategoryDomain categoryDomain) {
        return categoryRepository.saveAndFlush(categoryDomain);
    }
}

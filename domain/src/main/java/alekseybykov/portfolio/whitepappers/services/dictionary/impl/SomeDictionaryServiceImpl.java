package alekseybykov.portfolio.whitepappers.services.dictionary.impl;

import alekseybykov.portfolio.whitepappers.entities.SomeDictionary;
import alekseybykov.portfolio.whitepappers.enums.Errors;
import alekseybykov.portfolio.whitepappers.exceptions.EntityNotFoundException;
import alekseybykov.portfolio.whitepappers.registries.SomeDictionaryRegistry;
import alekseybykov.portfolio.whitepappers.services.dictionary.SomeDictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Aleksey Bykov
 * @since 05.10.2019
 */
@Service
@RequiredArgsConstructor
public class SomeDictionaryServiceImpl implements SomeDictionaryService {

    private final SomeDictionaryRegistry registry;

    @Override
    @Transactional(readOnly = true)
    public List<SomeDictionary> findAll() {
        return registry.findAll(sortByIdAsc());
    }

    @Override
    @Transactional(readOnly = true)
    public SomeDictionary findById(Long id) {
        return registry.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Errors.DICTIONARY_ITEM_NOT_FOUND.getName()));
    }

    private Sort sortByIdAsc() {
        return new Sort(Sort.Direction.ASC, "id");
    }
}

//
// Feel free to use these solutions in your work.
//
package alekseybykov.portfolio.component.registries;

import alekseybykov.portfolio.component.entities.WhitePapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

/**
 * @author  aleksey.n.bykov@gmail.com
 * @version 1.0
 * @since   2019-09-08
 */
public interface WhitePapperRegistry {

    WhitePapper save(WhitePapper whitePapper);

    Page<WhitePapper> findAllWhitepappers(Pageable pageable);

    void deleteByIds(Collection<Long> ids);
}

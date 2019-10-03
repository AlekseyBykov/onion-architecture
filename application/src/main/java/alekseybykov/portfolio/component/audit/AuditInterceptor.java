//
// Feel free to use these solutions in your work.
//
package alekseybykov.portfolio.component.audit;

import alekseybykov.portfolio.component.entities.User;
import alekseybykov.portfolio.component.service.user.SecurityService;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author  aleksey.n.bykov@gmail.com
 * @version 1.0
 * @since   2019-10-03
 */
@Component
public class AuditInterceptor  extends EmptyInterceptor {

    private SecurityService securityService;
    private Auditor auditor;

    private ThreadLocal<User> localUser = new ThreadLocal<>();

    public AuditInterceptor(SecurityService securityService, Auditor auditor) {
        super();
        this.securityService = securityService;
        this.auditor = auditor;
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        auditor.embedAuditBeforeCreating(entity, id, state, propertyNames, localUser.get());
        return super.onSave(entity, id, state, propertyNames, types);
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        auditor.embedAuditBeforeChanging(entity, id, currentState, previousState, propertyNames, localUser.get());
        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }

    @Override
    public void afterTransactionBegin(Transaction tx) {
        localUser.set(securityService.getCurrentUser());
        super.afterTransactionBegin(tx);
    }
}
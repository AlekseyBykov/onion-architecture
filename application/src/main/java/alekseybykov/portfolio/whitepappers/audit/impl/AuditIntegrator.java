package alekseybykov.portfolio.whitepappers.audit.impl;

import alekseybykov.portfolio.whitepappers.audit.Auditor;
import alekseybykov.portfolio.whitepappers.audit.helper.AuditHelper;
import alekseybykov.portfolio.whitepappers.entities.Audit;
import alekseybykov.portfolio.whitepappers.entities.Auditable;
import alekseybykov.portfolio.whitepappers.entities.User;
import alekseybykov.portfolio.whitepappers.utils.UUIDValidator;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.Serializable;

import static java.time.ZonedDateTime.now;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

/**
 * @author Aleksey Bykov
 * @since 03.10.2019
 */
@Component
@ConditionalOnProperty(name = "audit.enabled", havingValue = "true", matchIfMissing = true)
public class AuditIntegrator implements Auditor {

    @Override
    public void embedAuditBeforeCreating(Object entity,
                                         Serializable id,
                                         Object[] state,
                                         String[] propertyNames,
                                         User currentEmployee) {

        if (AuditHelper.isNotAuditor(entity) || (nonNull(id) && !UUIDValidator.isValidUuid(id))) {
            return;
        }

        Audit audit = Audit.builder()
                .dateCreate(now())
                .userCreate(currentEmployee)
                .build();

        int auditIndex = AuditHelper.getAuditPropertyIndexByName(propertyNames);

        Auditable auditable = (Auditable) entity;
        if (auditIndex >= NumberUtils.INTEGER_ZERO) {
            state[auditIndex] = audit;
            auditable.setAudit(audit);
        }
    }

    @Override
    public void embedAuditBeforeChanging(Object entity,
                                         Serializable id,
                                         Object[] currentState,
                                         Object[] previousState,
                                         String[] propertyNames,
                                         User currentEmployee) {

        if (AuditHelper.isNotAuditor(entity) || isNull(id)) {
            return;
        }

        int auditPropertyIndex = AuditHelper.getAuditPropertyIndexByName(propertyNames);

        Auditable auditable = (Auditable) entity;
        if (auditPropertyIndex >= NumberUtils.INTEGER_ZERO) {
            Audit audit = ofNullable((Audit) previousState[auditPropertyIndex]).orElse(new Audit());
            audit.setUserUpdate(currentEmployee);
            audit.setDateUpdate(now());

            AuditHelper.embedAudit(auditable, audit, currentState, auditPropertyIndex);
        }
    }
}

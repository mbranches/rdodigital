package com.branches.shared.config.envers;

import com.branches.security.model.UserDetailsImpl;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditingRevisionListener implements RevisionListener {

    @Override
    public void newRevision(final Object revisionEntity) {

        AuditedRevisionEntity auditedRevisionEntity = (AuditedRevisionEntity) revisionEntity;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName().equals("anonymousUser")) {
            return;
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        auditedRevisionEntity.setUserId(userDetails.getUser().id());
    }
}

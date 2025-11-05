package com.branches.obra.port;

import com.branches.obra.domain.ObraEntity;

public interface LoadObraPort {
    Integer getQuantidadeObrasAtivasByTenantId(Long tenantId);

    ObraEntity getObraByIdExternoAndTenantId(String idExterno, Long tenantId);
}

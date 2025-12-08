package com.branches.obra.service;

import com.branches.external.aws.S3UploadFile;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.dto.request.UpdateCapaDeObraRequest;
import com.branches.obra.repository.ObraRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.CompressImage;
import com.branches.utils.FileContentType;
import com.branches.utils.ImageOutPutFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UpdateCapaDeObraService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;
    private final CheckIfUserCanEditObraService checkIfUserCanEditObraService;
    private final CompressImage compressImage;
    private final S3UploadFile s3UploadFile;
    private final ObraRepository obraRepository;

    public void execute(UpdateCapaDeObraRequest request, String obraExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        ObraEntity obra = getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId);
        checkIfUserCanEditObraService.execute(currentUserTenant, obra.getId());

        byte[] compressedImage = compressImage.execute(request.base64Image(), 800, 800, 0.8, ImageOutPutFormat.JPEG);

        String fileName = "capa.jpeg";
        String path = "tenants/%s/obras/%s".formatted(tenantId, obra.getIdExterno());

        String url = s3UploadFile.execute(fileName, path, compressedImage, FileContentType.JPEG);

        obra.setCapaUrl(url);

        obraRepository.save(obra);
    }
}

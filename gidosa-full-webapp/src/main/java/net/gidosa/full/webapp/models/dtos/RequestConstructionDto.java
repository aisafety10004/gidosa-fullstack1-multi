package net.gidosa.full.webapp.models.dtos;

import org.springframework.web.multipart.MultipartFile;

public record RequestConstructionDto(
        String name,
        String phone,
        String constructionLocation,
        String position,
        String message,
        boolean agreement,
        MultipartFile businessCard,
        MultipartFile businessLicense,
        MultipartFile insuranceCertificate
) {}

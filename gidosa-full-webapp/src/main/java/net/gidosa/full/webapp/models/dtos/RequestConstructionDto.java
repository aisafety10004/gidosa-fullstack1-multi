package net.gidosa.full.webapp.models.dtos;

public record RequestConstructionDto(
        String name,
        String phone,
        String constructionLocation,
        String position,
        String message,
        boolean agreement
) {}

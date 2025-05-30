package lp.boble.aubos.dto.apikey;

import jakarta.persistence.criteria.CriteriaBuilder;
import lp.boble.aubos.model.apikey.ApiKeyStatusesModel;

import java.time.Instant;
import java.util.UUID;

public record ApiKeyResponse(
        UUID id,
        String publicId,
        String owner,
        String label,
        Instant createdAt,
        Instant lastUsedAt,
        Instant expiresAt,
        Integer rateLimit,
        Integer requestCount,
        Instant resetAt,
        String status
) {
}

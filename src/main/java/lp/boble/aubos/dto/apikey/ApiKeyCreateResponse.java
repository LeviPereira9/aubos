package lp.boble.aubos.dto.apikey;

import java.util.UUID;

public record ApiKeyCreateResponse(
        UUID id,
        String key
) {
}

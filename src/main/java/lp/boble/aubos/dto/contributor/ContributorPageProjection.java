package lp.boble.aubos.dto.contributor;

import java.time.Instant;
import java.util.UUID;

public interface ContributorPageProjection {
    UUID getId();
    String getName();
    Instant getLastUpdate();
}

package lp.boble.aubos.dto.book;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface BookPageProjection  {
    UUID getId();
    String getCoverUrl();
    String getTitle();
    String getSubtitle();
    String getStatus();
    Instant getLastUpdated();
}

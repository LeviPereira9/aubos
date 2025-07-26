package lp.boble.aubos.config.cache;

import org.springframework.http.CacheControl;

import java.util.concurrent.TimeUnit;

public final class CacheProfiles {

    public static CacheControl userPrivate(){
        return CacheControl.maxAge(15, TimeUnit.MINUTES).cachePrivate();
    }

    public static CacheControl userPublic(){
        return CacheControl.maxAge(3, TimeUnit.HOURS).cachePublic();
    }

    public static CacheControl apiKeyPrivate(){
        return CacheControl.maxAge(5, TimeUnit.MINUTES).cachePrivate();
    }

    public static CacheControl book(){
        return CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic();
    }
}

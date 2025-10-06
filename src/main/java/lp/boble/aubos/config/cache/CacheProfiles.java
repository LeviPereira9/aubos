package lp.boble.aubos.config.cache;


import org.springframework.http.CacheControl;

import java.util.concurrent.TimeUnit;

public final class CacheProfiles {

    // Raramente mudam.
    public static CacheControl staticData(){
        return CacheControl.maxAge(30, TimeUnit.DAYS)
                .cachePublic()
                .mustRevalidate();
    }

    // Mudam de vez em quando.
    public static CacheControl catalogData(){
        return CacheControl.maxAge(7, TimeUnit.DAYS)
                .cachePublic()
                .staleWhileRevalidate(1, TimeUnit.DAYS);
    }

    // Pro conteúdo principal, muda com boa frequência.
    public static CacheControl contentData(){
        return CacheControl.maxAge(1, TimeUnit.HOURS)
                .cachePublic()
                .staleWhileRevalidate(15, TimeUnit.MINUTES);
    }

    // Muda toda hora.
    public static CacheControl dynamicData(){
        return CacheControl.maxAge(5, TimeUnit.MINUTES)
                .cachePublic()
                .staleIfError(1, TimeUnit.MINUTES);
    }

    // Mais especifico.

    //Dados sensíveis
    public static CacheControl userPrivate(){
        return CacheControl.maxAge(15, TimeUnit.MINUTES)
                .cachePrivate()
                .mustRevalidate();
    }

    // Dados de sessão
    public static CacheControl sessionData(){
        return CacheControl
                .noStore();
    }

    public static CacheControl searchResults(){
        return CacheControl.maxAge(2, TimeUnit.MINUTES)
                .cachePublic()
                .staleWhileRevalidate(30, TimeUnit.SECONDS);
    }


    // Para relações
    public static CacheControl relationships(){
        return CacheControl.maxAge(1, TimeUnit.DAYS)
                .cachePublic()
                .mustRevalidate();
    }

}

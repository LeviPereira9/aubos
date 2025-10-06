package lp.boble.aubos.config.documentation.book.dependencies.tag;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "",
        description = ""
)
public @interface DocFindAllTags {}

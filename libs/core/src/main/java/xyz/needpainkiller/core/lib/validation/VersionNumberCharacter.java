package xyz.needpainkiller.core.lib.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

@Constraint(validatedBy = {VersionNumberCharValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface VersionNumberCharacter {
    Pattern pattern = Pattern.compile("[^0-9 \\-._]");

    String message() default "Allow Only Version Number Character (Number & '\\', '.', '_')";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

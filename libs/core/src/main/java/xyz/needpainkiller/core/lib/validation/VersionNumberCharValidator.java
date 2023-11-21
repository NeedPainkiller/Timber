package xyz.needpainkiller.core.lib.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class VersionNumberCharValidator implements ConstraintValidator<VersionNumberCharacter, String> {

    private Pattern pattern;

    @Override
    public void initialize(VersionNumberCharacter constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Matcher m = pattern.matcher(value);
        boolean isValid = m.find();
        return !isValid;
    }

}
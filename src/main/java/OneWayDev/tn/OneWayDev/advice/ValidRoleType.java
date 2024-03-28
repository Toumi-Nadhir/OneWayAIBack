package OneWayDev.tn.OneWayDev.advice;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RoleTypeValidator.class)
public @interface ValidRoleType {
    String message() default "Invalid role type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

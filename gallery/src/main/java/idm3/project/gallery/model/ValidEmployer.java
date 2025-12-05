package idm3.project.gallery.model;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmployerValidator.class)
@Documented
public @interface ValidEmployer {

    String message() default "Organization is required for employers";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

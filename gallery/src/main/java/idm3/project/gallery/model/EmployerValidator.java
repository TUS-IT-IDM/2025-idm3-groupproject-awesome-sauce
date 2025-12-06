package idm3.project.gallery.model;

import idm3.project.gallery.model.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmployerValidator implements ConstraintValidator<ValidEmployer, User> {

    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {

        if (user == null) return true;

        // Only validate if employer
        if ("EMPLOYER".equalsIgnoreCase(user.getUserType())) {

            boolean valid = user.getOrganization() != null &&
                    !user.getOrganization().trim().isEmpty();

            if (!valid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "Organization is required for employer accounts"
                ).addPropertyNode("organization").addConstraintViolation();
            }

            return valid;
        }

        // If not employer → always valid
        return true;
    }
}

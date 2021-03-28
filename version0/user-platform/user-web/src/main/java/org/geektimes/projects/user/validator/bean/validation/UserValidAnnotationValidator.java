package org.geektimes.projects.user.validator.bean.validation;

import org.geektimes.projects.user.domain.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

public class UserValidAnnotationValidator implements ConstraintValidator<UserValid, User> {
    private int idRange;

    @Override
    public void initialize(UserValid constraintAnnotation) {
        this.idRange = constraintAnnotation.idRange();
    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext validatorContext) {
        //获取模板信息
        validatorContext.getDefaultConstraintMessageTemplate();

        return false;
    }
}

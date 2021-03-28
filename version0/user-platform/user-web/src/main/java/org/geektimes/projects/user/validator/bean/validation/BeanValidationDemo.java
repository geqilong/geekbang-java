package org.geektimes.projects.user.validator.bean.validation;

import org.geektimes.projects.user.domain.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.function.Consumer;

public class BeanValidationDemo {
    public static void main(String[] args) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        User user = new User();
        user.setId(5L);
        user.setPassword("*******");

        //校验结果
        Set<ConstraintViolation<User>> violations =  validator.validate(user);
        violations.forEach(new Consumer<ConstraintViolation<User>>() {
            @Override
            public void accept(ConstraintViolation<User> cv) {
                System.out.println(cv);
            }
        });
    }
}

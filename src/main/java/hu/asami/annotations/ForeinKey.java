package hu.asami.annotations;

import hu.asami.dao.DataTransferObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForeinKey {
    Class<DataTransferObject> value();
}

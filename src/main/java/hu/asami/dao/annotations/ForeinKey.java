package hu.asami.dao.annotations;

import hu.asami.dao.DataTransferObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForeinKey {
    Class<DataTransferObject> value();
}

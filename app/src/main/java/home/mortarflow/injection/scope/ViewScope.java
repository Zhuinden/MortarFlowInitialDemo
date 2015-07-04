package home.mortarflow.injection.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by Zhuinden on 2015.07.04..
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewScope {
}

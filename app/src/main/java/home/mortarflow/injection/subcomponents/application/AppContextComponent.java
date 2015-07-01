package home.mortarflow.injection.subcomponents.application;

import android.content.Context;

import dagger.Component;
import dagger.Provides;
import home.mortarflow.application.CustomApplication;
import home.mortarflow.injection.modules.application.AppContextModule;

/**
 * Created by Zhuinden on 2015.07.01..
 */
@Component(modules = {AppContextModule.class})
public interface AppContextComponent {
    CustomApplication customApplication();
    Context applicationContext();
}

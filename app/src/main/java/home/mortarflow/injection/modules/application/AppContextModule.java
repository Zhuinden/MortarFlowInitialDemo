package home.mortarflow.injection.modules.application;

import android.content.Context;
import android.content.pm.PackageManager;

import dagger.Module;
import dagger.Provides;
import home.mortarflow.application.CustomApplication;

/**
 * Created by Zhuinden on 2015.07.01..
 */
@Module
public class AppContextModule {
    private CustomApplication customApplication;

    public AppContextModule(CustomApplication customApplication) {
        this.customApplication = customApplication;
    }

    @Provides
    public CustomApplication customApplication() {
        return customApplication;
    }

    @Provides
    public Context applicationContext() {
        return customApplication;
    }
}

package home.mortarflow.application;

import android.content.Context;

import home.mortarflow.injection.components.ApplicationComponent;
import home.mortarflow.injection.components.DaggerApplicationComponent;
import home.mortarflow.injection.modules.application.AppContextModule;
import home.mortarflow.injection.subcomponents.application.DaggerAppContextComponent;
import home.mortarflow.injection.subcomponents.application.DaggerAppDataComponent;
import home.mortarflow.injection.subcomponents.application.DaggerAppDomainComponent;
import home.mortarflow.injection.subcomponents.application.DaggerAppPresentationComponent;
import home.mortarflow.injection.subcomponents.application.DaggerAppUtilsComponent;

/**
 * Created by Zhuinden on 2015.07.01..
 */
public class InjectorService {
    public static final String TAG = InjectorService.class.getSimpleName();

    private ApplicationComponent applicationComponent; //dagger2 app level component

    InjectorService(CustomApplication customApplication) {
        AppContextModule appContextModule = new AppContextModule(customApplication);
        applicationComponent = DaggerApplicationComponent.builder()
                .appContextComponent(DaggerAppContextComponent.builder()
                        .appContextModule(appContextModule)
                        .build())
                .appDataComponent(DaggerAppDataComponent.create())
                .appDomainComponent(DaggerAppDomainComponent.create())
                .appPresentationComponent(DaggerAppPresentationComponent.create())
                .appUtilsComponent(DaggerAppUtilsComponent.create())
                .build();
    }

    public ApplicationComponent getInjector() { //return the app component to inject `this` with it
        return applicationComponent;
    }

    public static InjectorService get(Context context) {
        //this is needed otherwise the compiler is whining. -_-
        //noinspection ResourceType
        return (InjectorService)context.getSystemService(TAG);
    }
}

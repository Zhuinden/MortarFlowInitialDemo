package home.mortarflow.application;

import android.content.Context;

import home.mortarflow.injection.components.ApplicationComponent;
import home.mortarflow.injection.components.DaggerApplicationComponent;
import home.mortarflow.injection.modules.application.AppContextModule;
import home.mortarflow.injection.subcomponents.application.AppContextComponent;
import home.mortarflow.injection.subcomponents.application.AppDataComponent;
import home.mortarflow.injection.subcomponents.application.AppDomainComponent;
import home.mortarflow.injection.subcomponents.application.AppPresentationComponent;
import home.mortarflow.injection.subcomponents.application.AppUtilsComponent;
import home.mortarflow.injection.subcomponents.application.DaggerAppContextComponent;
import home.mortarflow.injection.subcomponents.application.DaggerAppDataComponent;
import home.mortarflow.injection.subcomponents.application.DaggerAppDomainComponent;
import home.mortarflow.injection.subcomponents.application.DaggerAppPresentationComponent;
import home.mortarflow.injection.subcomponents.application.DaggerAppUtilsComponent;
import mortar.MortarScope;

/**
 * Created by Zhuinden on 2015.07.01..
 */
public class InjectorService {
    public static final String TAG = InjectorService.class.getSimpleName();

    private ApplicationComponent applicationComponent; //dagger2 app level component

    InjectorService(CustomApplication customApplication) {
        AppContextModule appContextModule = new AppContextModule(customApplication);
        AppContextComponent appContextComponent = DaggerAppContextComponent.builder()
                .appContextModule(appContextModule)
                .build();
        AppDataComponent appDataComponent = DaggerAppDataComponent.builder().build();
        AppDomainComponent appDomainComponent = DaggerAppDomainComponent.builder().build();
        AppPresentationComponent appPresentationComponent = DaggerAppPresentationComponent.builder()
                .build();
        AppUtilsComponent appUtilsComponent = DaggerAppUtilsComponent.builder().build();
        applicationComponent = DaggerApplicationComponent.builder()
                .appContextComponent(appContextComponent)
                .appDataComponent(appDataComponent)
                .appDomainComponent(appDomainComponent)
                .appPresentationComponent(appPresentationComponent)
                .appUtilsComponent(appUtilsComponent)
                .build();
    }

    public ApplicationComponent getInjector() { //return the app component to inject `this` with it
        return applicationComponent;
    }

    public static InjectorService get(Context context) {
        //this is needed otherwise the compiler is whining. -_-
        //noinspection ResourceType
        return (InjectorService) context.getSystemService(TAG);
    }

    public static ApplicationComponent obtain() {
        return ((InjectorService) MortarScope.getScope(ApplicationHolder.INSTANCE.getApplication())
                .getService(TAG)).getInjector();
    }
}

package home.mortarflow.injection.components;

import dagger.Component;
import home.mortarflow.injection.modules.application.AppContextModule;
import home.mortarflow.injection.scope.ApplicationScope;
import home.mortarflow.injection.subcomponents.application.AppContextComponent;
import home.mortarflow.injection.subcomponents.application.AppDataComponent;
import home.mortarflow.injection.subcomponents.application.AppDomainComponent;
import home.mortarflow.injection.subcomponents.application.AppPresentationComponent;
import home.mortarflow.injection.subcomponents.application.AppUtilsComponent;
import home.mortarflow.presentation.view.activity.MainActivity;

/**
 * Created by Zhuinden on 2015.07.01..
 */
@ApplicationScope
@Component(modules = {AppContextModule.class})
public interface ApplicationComponent
        extends AppContextComponent, AppDataComponent, AppDomainComponent, AppPresentationComponent, AppUtilsComponent {
    void inject(MainActivity mainActivity);

    //void inject(FirstView firstView); //no longer needed, has its own component
}

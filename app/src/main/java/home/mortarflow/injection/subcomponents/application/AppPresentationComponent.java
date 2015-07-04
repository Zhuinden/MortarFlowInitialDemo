package home.mortarflow.injection.subcomponents.application;

import dagger.Component;
import home.mortarflow.injection.subcomponents.application.presentation.PresenterComponent;

/**
 * Created by Zhuinden on 2015.07.01..
 */
@Component(dependencies = {PresenterComponent.class})
public interface AppPresentationComponent
        extends PresenterComponent {
}

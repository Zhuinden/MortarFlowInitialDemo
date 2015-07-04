package home.mortarflow.injection.subcomponents.application.presentation;

import dagger.Component;
import home.mortarflow.injection.modules.application.presentation.PresenterModule;
import home.mortarflow.presentation.view.paths.FirstPath;

/**
 * Created by Zhuinden on 2015.07.04..
 */
@Component(modules = {PresenterModule.class})
public interface PresenterComponent {
    FirstPath.FirstViewPresenter firstViewPresenter();
}

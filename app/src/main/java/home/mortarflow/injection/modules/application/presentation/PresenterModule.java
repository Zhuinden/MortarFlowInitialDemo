package home.mortarflow.injection.modules.application.presentation;

import dagger.Module;
import dagger.Provides;
import home.mortarflow.presentation.view.paths.FirstPath;

/**
 * Created by Zhuinden on 2015.07.04..
 */
@Module
public class PresenterModule {
    private FirstPath.FirstViewPresenter firstViewPresenter; //TODO: the @ApplicationScope should make sure there is only one, but apparently it doesn't.

    @Provides
    public FirstPath.FirstViewPresenter firstViewPresenter() {
        if(firstViewPresenter == null) {
            firstViewPresenter = new FirstPath.FirstViewPresenter();
        }
        return firstViewPresenter;
    }
}

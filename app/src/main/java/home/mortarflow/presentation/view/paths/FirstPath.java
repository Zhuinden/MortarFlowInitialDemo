package home.mortarflow.presentation.view.paths;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import flow.Flow;
import home.mortarflow.R;
import home.mortarflow.application.InjectorService;
import home.mortarflow.injection.components.ApplicationComponent;
import home.mortarflow.injection.scope.ViewScope;
import home.mortarflow.presentation.view.views.FirstView;
import home.mortarflow.utils.custom_path.BasePath;
import mortar.ViewPresenter;

/**
 * Created by Zhuinden on 2015.07.01..
 */
public class FirstPath
        extends BasePath {
    public final int parameter;

    public FirstPath(int parameter) {
        this.parameter = parameter;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        FirstPath firstPath = (FirstPath) o;

        return parameter == firstPath.parameter;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + parameter;
        return result;
    }

    @Override
    public int getLayout() {
        return R.layout.path_first;
    }

    @Override
    public FirstViewComponent createComponent() {
        FirstPath.FirstViewComponent firstViewComponent = DaggerFirstPath_FirstViewComponent.builder()
                .applicationComponent(InjectorService.obtain())
                .firstViewModule(new FirstPath.FirstViewModule(parameter))
                .build();
        return firstViewComponent;
    }

    @ViewScope //needed
    @Component(dependencies = {ApplicationComponent.class}, modules = {FirstViewModule.class})
    public interface FirstViewComponent
            extends ApplicationComponent {
        String data();

        FirstViewPresenter firstViewPresenter();

        void inject(FirstView firstView);
    }

    @Module
    public static class FirstViewModule {
        private int parameter;

        private FirstViewPresenter firstViewPresenter;

        public FirstViewModule(int parameter) {
            this.parameter = parameter;
        }

        @Provides
        public String data(Context context) {
            return context.getString(parameter);
        }

        @Provides
        public FirstViewPresenter firstViewPresenter() {
            if(firstViewPresenter == null) { //TODO: I still don't know why this is necessary, lol.
                this.firstViewPresenter = new FirstViewPresenter();
            }
            return firstViewPresenter;
        }
    }

    public static class FirstViewPresenter
            extends ViewPresenter<FirstView> {
        public static final String TAG = FirstViewPresenter.class.getSimpleName();

        public FirstViewPresenter() {
            Log.d(TAG, "First View Presenter created: " + toString());
        }

        @Override
        protected void onSave(Bundle outState) {
            super.onSave(outState);
            Log.d(TAG, "On Save called: " + toString());
            FirstView firstView = getView();
            outState.putString("input", firstView.getInput());
            Log.d(TAG, "onSave called");
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            Log.d(TAG, "On Load called: " + toString());
            if(!hasView()) {
                return;
            }
            FirstView firstView = getView();
            if(savedInstanceState != null) { //needed check
                firstView.setInput(savedInstanceState.getString("input"));
            }
        }

        public void goToNextActivity() {
            Flow.get(getView()).set(new SecondPath());
        }
    }
}

package home.mortarflow.presentation.view.paths;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import javax.inject.Inject;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import flow.Flow;
import flow.path.Path;
import home.mortarflow.R;
import home.mortarflow.application.InjectorService;
import home.mortarflow.injection.components.ApplicationComponent;
import home.mortarflow.injection.scope.ViewScope;
import home.mortarflow.presentation.view.views.FirstView;
import home.mortarflow.utils.custom_path.BasePath;
import home.mortarflow.utils.custom_path.DaggerService;
import mortar.MortarScope;
import mortar.ViewPresenter;

/**
 * Created by Zhuinden on 2015.07.01..
 */
public class FirstPath
        extends BasePath {
    public static final String TAG = FirstPath.class.getSimpleName();

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

    @Override
    public String getScopeName() {
        return TAG + "_" + parameter;
    }

    @ViewScope //needed
    @Component(dependencies = {ApplicationComponent.class}, modules = {FirstViewModule.class})
    public interface FirstViewComponent
            extends ApplicationComponent {
        String data();

        FirstViewPresenter firstViewPresenter();

        void inject(FirstView firstView);

        void inject(FirstViewPresenter firstViewPresenter);
    }

    @Module
    public static class FirstViewModule {
        private int parameter;

        public FirstViewModule(int parameter) {
            this.parameter = parameter;
        }

        @Provides
        public String data(Context context) {
            return context.getString(parameter);
        }

        @Provides
        @ViewScope
        public FirstViewPresenter firstViewPresenter() {
            return new FirstViewPresenter();
        }
    }

    public static class FirstViewPresenter
            extends ViewPresenter<FirstView> {
        public static final String TAG = FirstViewPresenter.class.getSimpleName();

        @Inject
        String data;

        public FirstViewPresenter() {
            Log.d(TAG, "First View Presenter created: " + toString());
        }

        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);
            Log.d(TAG, this.toString() + ": " + "On Enter Scope: " + scope.toString());
            FirstViewComponent firstViewComponent = scope.getService(DaggerService.TAG);
            firstViewComponent.inject(this);
            Log.wtf(TAG, "Data [" + data + "] and other objects injected to first presenter.");
        }

        @Override
        protected void onExitScope() {
            super.onExitScope();
            Log.d(TAG, this.toString() + ": On Exit Scope.");
        }

        @Override
        protected void onSave(Bundle outState) {
            super.onSave(outState);
            Log.d(TAG, "On Save called: " + toString());
            FirstView firstView = getView();
            outState.putString("input", firstView.getInput());
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
            FirstPath firstPath = Path.get(getView().getContext());
            if(firstPath.parameter != R.string.hello_world) {
                Flow.get(getView()).set(new FirstPath(R.string.hello_world));
            } else {
                Flow.get(getView()).set(new SecondPath());
            }
        }
    }
}

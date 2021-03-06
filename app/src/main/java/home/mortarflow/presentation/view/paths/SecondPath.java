package home.mortarflow.presentation.view.paths;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import javax.inject.Inject;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import home.mortarflow.R;
import home.mortarflow.application.InjectorService;
import home.mortarflow.injection.components.ApplicationComponent;
import home.mortarflow.injection.scope.ViewScope;
import home.mortarflow.presentation.view.views.SecondView;
import home.mortarflow.utils.custom_path.BasePath;
import home.mortarflow.utils.custom_path.DaggerService;
import mortar.MortarScope;
import mortar.ViewPresenter;

/**
 * Created by Zhuinden on 2015.07.01..
 */
public class SecondPath
        extends BasePath {
    public static final String TAG = SecondPath.class.getSimpleName();

    @Override
    public int getLayout() {
        return R.layout.path_second;
    }

    protected SecondViewComponent secondViewComponent;

    @Inject
    SecondViewPresenter secondViewPresenter;

    public SecondPath() {
        createAndStoreComponentAndInjectSelf();
    }

    @Override
    public SecondPath.SecondViewComponent createAndStoreComponentAndInjectSelf() {
        if(secondViewComponent == null) {
            secondViewComponent = DaggerSecondPath_SecondViewComponent.builder()
                    .applicationComponent(InjectorService.obtain())
                    .secondViewModule(new SecondViewModule())
                    .build();
            secondViewComponent.inject(this);
        }
        return secondViewComponent;
    }

    @Override
    public String getScopeName() {
        return TAG;
    }

    protected SecondPath(Parcel in) {
        super(in);
        secondViewPresenter.onLoad(in.readBundle());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle presenterState = new Bundle();
        secondViewPresenter.onSave(presenterState);
        dest.writeBundle(presenterState);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SecondPath> CREATOR = new Parcelable.Creator<SecondPath>() {
        @Override
        public SecondPath createFromParcel(Parcel in) {
            return new SecondPath(in);
        }

        @Override
        public SecondPath[] newArray(int size) {
            return new SecondPath[size];
        }
    };

    @ViewScope
    @Component(dependencies = {ApplicationComponent.class}, modules = {SecondPath.SecondViewModule.class})
    public interface SecondViewComponent
            extends ApplicationComponent {
        SecondViewPresenter secondViewPresenter();

        void inject(SecondView secondView);

        void inject(SecondViewPresenter secondViewPresenter);

        void inject(SecondPath secondPath);
    }

    @Module
    public static class SecondViewModule {
        @Provides
        @ViewScope
        public SecondViewPresenter secondViewPresenter() {
            return new SecondViewPresenter();
        }
    }

    public static class SecondViewPresenter
            extends ViewPresenter<SecondView> {
        public static final String TAG = SecondViewPresenter.class.getSimpleName();

        public SecondViewPresenter() {
            Log.d(TAG, "Second View Presenter created: " + toString());
        }

        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);
            Log.d(TAG, this.toString() + ": " + "On Enter Scope: " + scope.toString());
            SecondViewComponent secondViewComponent = scope.getService(DaggerService.TAG);
            secondViewComponent.inject(this);
            Log.wtf(TAG, "Data and other objects injected to second presenter.");
        }

        @Override
        protected void onExitScope() {
            super.onExitScope();
            Log.d(TAG, this.toString() + ": On Exit Scope.");
        }

        @Override
        protected void onSave(Bundle outState) {
            super.onSave(outState);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
        }
    }
}

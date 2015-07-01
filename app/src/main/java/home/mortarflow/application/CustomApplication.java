package home.mortarflow.application;

import android.app.Application;

import mortar.MortarScope;

/**
 * Created by Zhuinden on 2015.07.01..
 */
public class CustomApplication
        extends Application {
    public static final String TAG = CustomApplication.class.getSimpleName();

    private MortarScope rootScope;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public Object getSystemService(String name) {
        if(rootScope == null) {
            rootScope = MortarScope.buildRootScope()
                    .withService(InjectorService.TAG, new InjectorService(this))
                    .build("Root");
        }
        if(rootScope.hasService(name)) { // if the additional "Context" service is within Mortar
            return rootScope.getService(name);
        }
        return super.getSystemService(name); // otherwise return application level context system service
    }
}

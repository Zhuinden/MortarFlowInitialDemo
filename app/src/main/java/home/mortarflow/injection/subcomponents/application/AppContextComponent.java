package home.mortarflow.injection.subcomponents.application;

import android.content.Context;

import home.mortarflow.application.CustomApplication;

/**
 * Created by Zhuinden on 2015.07.01..
 */
public interface AppContextComponent {
    CustomApplication customApplication();
    Context applicationContext();
}

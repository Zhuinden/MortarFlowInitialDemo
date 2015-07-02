package home.mortarflow.application;

/**
 * Created by Zhuinden on 2015.07.02..
 */
public enum ApplicationHolder {
    INSTANCE;

    private CustomApplication customApplication;

    private ApplicationHolder() {
    }

    void setApplication(CustomApplication customApplication) {
        this.customApplication = customApplication;
    }

    public CustomApplication getApplication() {
        return customApplication;
    }
}

package home.mortarflow.utils.custom_path;

import android.os.Parcel;
import android.os.Parcelable;

import flow.path.Path;

/**
 * Created by Zhuinden on 2015.07.04..
 */
public abstract class BasePath
        extends Path
        implements Parcelable {
    public BasePath() {
    }

    protected BasePath(Parcel in) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public abstract int getLayout();

    public abstract Object createAndStoreComponentAndInjectSelf();

    public abstract String getScopeName();

    @Override
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        } else if(o == null) {
            return false;
        } else if(o instanceof BasePath) {
            BasePath basePath = (BasePath) o;
            return basePath.getScopeName().equals(getScopeName());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getScopeName().hashCode();
    }
}

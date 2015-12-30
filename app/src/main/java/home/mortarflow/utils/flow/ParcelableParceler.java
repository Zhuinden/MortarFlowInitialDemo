package home.mortarflow.utils.flow;

import android.os.Parcelable;

import flow.StateParceler;

/**
 * Created by Zhuinden on 2015.12.30..
 */
public class ParcelableParceler implements StateParceler {
    @Override
    public Parcelable wrap(Object instance) {
        return (Parcelable)instance;
    }

    @Override
    public Object unwrap(Parcelable parcelable) {
        return parcelable;
    }
}

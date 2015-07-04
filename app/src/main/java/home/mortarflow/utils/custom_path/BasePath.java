package home.mortarflow.utils.custom_path;

import flow.path.Path;

/**
 * Created by Zhuinden on 2015.07.04..
 */
public abstract class BasePath
        extends Path {
    public abstract int getLayout();

    public abstract Object createComponent();
}

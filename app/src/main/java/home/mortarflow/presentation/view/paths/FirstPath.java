package home.mortarflow.presentation.view.paths;

import android.view.View;

import flow.path.Path;
import home.mortarflow.R;
import home.mortarflow.utils.flow.Layout;

/**
 * Created by Zhuinden on 2015.07.01..
 */
@Layout(R.layout.path_first)
public class FirstPath extends Path {
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
}

package home.mortarflow.utils.mortarflow;

import android.content.Context;

import home.mortarflow.utils.custom_path.BasePath;
import home.mortarflow.utils.custom_path.DaggerService;
import mortar.MortarScope;

/**
 * Creates {@link MortarScope}s for screens.
 */
public class ScreenScoper {
    public MortarScope getScreenScope(Context context, String name, Object screen) {
        MortarScope parentScope = MortarScope.getScope(context);
        return getScreenScope(parentScope, name, screen);
    }

    /**
     * Finds or creates the scope for the given screen.
     */
    public MortarScope getScreenScope(MortarScope parentScope, final String name, final Object screen) {
        MortarScope childScope = parentScope.findChild(name);
        if (childScope == null) {
            BasePath basePath = (BasePath) screen;
            childScope = parentScope.buildChild()
                    .withService(DaggerService.TAG, basePath.createAndStoreComponentAndInjectSelf())
                    .build(name);
        }
        return childScope;
    }
}
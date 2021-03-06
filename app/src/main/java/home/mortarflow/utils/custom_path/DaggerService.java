package home.mortarflow.utils.custom_path;

import android.content.Context;

/**
 * Created by Zhuinden on 2015.07.04..
 */
public class DaggerService {
    public static final String TAG = DaggerService.class.getSimpleName();

    @SuppressWarnings("unchecked")
    public static <T> T getComponent(Context context) {
        //noinspection ResourceType
        return (T) context.getSystemService(TAG);
    }
}

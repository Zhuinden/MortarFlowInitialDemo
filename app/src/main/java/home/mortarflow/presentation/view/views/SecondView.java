package home.mortarflow.presentation.view.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import flow.path.Path;
import home.mortarflow.application.InjectorService;
import home.mortarflow.presentation.view.paths.SecondPath;

/**
 * Created by Zhuinden on 2015.07.02..
 */
public class SecondView extends LinearLayout {
    public static final String TAG = SecondView.class.getSimpleName();

    public SecondView(Context context) {
        super(context);
        init(context);
    }

    public SecondView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SecondView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public SecondView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        InjectorService.get(context).getInjector().inject(this);
        SecondPath secondPath = Path.get(context);
        Log.d(TAG, "SECOND PATH: " + secondPath);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d(TAG, "SECOND VIEW CONTEXT IS: " + getContext().toString());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //presenter.takeView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        //presenter.dropView(this);
        super.onDetachedFromWindow();
    }
}

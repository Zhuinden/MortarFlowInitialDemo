package home.mortarflow.presentation.view.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import javax.inject.Inject;

import flow.path.Path;
import home.mortarflow.presentation.view.paths.SecondPath;
import home.mortarflow.utils.custom_path.DaggerService;

/**
 * Created by Zhuinden on 2015.07.02..
 */
public class SecondView
        extends LinearLayout {
    public static final String TAG = SecondView.class.getSimpleName();

    @Inject
    public SecondPath.SecondViewPresenter secondViewPresenter;

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
        if(!isInEditMode()) {
            SecondPath.SecondViewComponent secondViewComponent = DaggerService.getComponent(context);
            secondViewComponent.inject(this);
        
            SecondPath secondPath = Path.get(context);
            Log.d(TAG, "SECOND PATH: " + secondPath);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d(TAG, "SECOND VIEW CONTEXT IS: " + getContext().toString());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(secondViewPresenter != null) {
            secondViewPresenter.takeView(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if(secondViewPresenter != null) {
            secondViewPresenter.dropView(this);
        }
        super.onDetachedFromWindow();
    }
}

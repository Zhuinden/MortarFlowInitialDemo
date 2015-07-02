package home.mortarflow.presentation.view.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import javax.inject.Inject;

import butterknife.ButterKnife;
import flow.path.Path;
import home.mortarflow.application.InjectorService;
import home.mortarflow.presentation.view.paths.SecondPath;

/**
 * Created by Zhuinden on 2015.07.02..
 */
public class SecondView extends LinearLayout {
    public SecondView(Context context) {
        super(context);
    }

    public SecondView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SecondView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public SecondView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        System.out.println("SECOND VIEW CONTEXT IS: " + getContext().toString() + " " + getContext()
                .hashCode()) ;
        InjectorService.get(getContext()).getInjector().inject(this);
        SecondPath secondPath = Path.get(getContext());
        System.out.println("SECOND PATH: " + secondPath);
    }
}

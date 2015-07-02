package home.mortarflow.presentation.view.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import flow.Flow;
import flow.path.Path;
import home.mortarflow.R;
import home.mortarflow.application.InjectorService;
import home.mortarflow.presentation.view.paths.FirstPath;
import home.mortarflow.presentation.view.paths.SecondPath;

/**
 * Created by Zhuinden on 2015.07.01..
 */
public class FirstView extends LinearLayout {
    @OnClick(R.id.path_first_button)
    public void onClickButton() {
        Flow.get(this).set(new SecondPath());
    }

    public FirstView(Context context) {
        super(context);
    }

    public FirstView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FirstView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public FirstView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        InjectorService.get(getContext()).getInjector().inject(this);
        System.out.println("CONTEXT: " + this.getContext() + " " + this.getContext().hashCode());
        //FirstPath firstPath = Path.get(this.getContext()); //BROKEN, MUST FIX
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ButterKnife.unbind(this);
    }
}

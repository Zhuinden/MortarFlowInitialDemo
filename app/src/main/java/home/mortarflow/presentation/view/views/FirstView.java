package home.mortarflow.presentation.view.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import home.mortarflow.R;
import home.mortarflow.presentation.view.paths.FirstPath;
import home.mortarflow.utils.custom_path.DaggerService;

/**
 * Created by Zhuinden on 2015.07.01..
 */
public class FirstView
        extends LinearLayout {
    public static final String TAG = FirstView.class.getSimpleName();

    @Inject
    FirstPath.FirstViewPresenter firstViewPresenter;

    @Inject
    String data;

    @Bind(R.id.path_first_data)
    TextView dataDisplay;

    @Bind(R.id.path_first_input)
    EditText input;

    @OnClick(R.id.path_first_button)
    public void onClickButton() {
        //Flow.get(this).set(new SecondPath());
        firstViewPresenter.goToNextActivity();
    }

    public FirstView(Context context) {
        super(context);
        init(context);
    }

    public FirstView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FirstView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public FirstView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        try { //TODO: fix rendering preview
            FirstPath.FirstViewComponent firstViewComponent = DaggerService.getComponent(context);
            firstViewComponent.inject(this);
        } catch(java.lang.UnsupportedOperationException e) {
            Log.wtf(TAG, "This happens only in rendering.");
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d(TAG, "On Finished Inflate: " + this.toString());
        ButterKnife.bind(this);
        dataDisplay.setText(data);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "On Attached to Window: " + this.toString());
        if(firstViewPresenter != null) { //TODO: fix rendering
            firstViewPresenter.takeView(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.d(TAG, "On Detached from Window: " + this.toString());
        if(firstViewPresenter != null) { //TODO: fix rendering
            firstViewPresenter.dropView(this);
        }
        ButterKnife.unbind(this);
        super.onDetachedFromWindow();
    }

    public String getInput() {
        Log.d(TAG, "Get Input: " + this.toString());
        return input.getText().toString();
    }

    public void setInput(String inputText) {
        Log.d(TAG, "Set Input: " + inputText + " " + this.toString());
        this.input.setText(inputText);
    }
}

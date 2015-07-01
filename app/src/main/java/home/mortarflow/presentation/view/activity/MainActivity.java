package home.mortarflow.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import flow.Flow;
import flow.FlowDelegate;
import flow.History;
import flow.path.Path;
import flow.path.PathContext;
import home.mortarflow.R;
import home.mortarflow.application.InjectorService;
import home.mortarflow.presentation.view.paths.FirstPath;
import home.mortarflow.utils.flow.FramePathContainerView;
import home.mortarflow.utils.flow.GsonParceler;
import home.mortarflow.utils.flow.HandlesBack;
import home.mortarflow.utils.mortarflow.MortarScreenSwitcherFrame;
import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;

public class MainActivity
        extends AppCompatActivity
        implements Flow.Dispatcher { //needed for FLOW SUPPORT
    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.main_path_container)
    public MortarScreenSwitcherFrame framePathContainerView;

    private HandlesBack handlesBack;
    private FlowDelegate flowSupport;

    private MortarScope activityScope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //FLOW PATH INIT
        PathContext pathContext = PathContext.root(this);
        framePathContainerView = (MortarScreenSwitcherFrame) LayoutInflater.from(this)
                .cloneInContext(pathContext)
                .inflate(R.layout.activity_main, null);
        setContentView(framePathContainerView);

        //MORTAR INIT
        MortarScope parentScope = MortarScope.getScope(getApplication());

        activityScope = parentScope.findChild(TAG);
        if(activityScope == null) {
            activityScope = parentScope.buildChild()
                    .withService(BundleServiceRunner.SERVICE_NAME, new BundleServiceRunner())
                    .build(TAG);
        }

        InjectorService.get(this).getInjector().inject(this); // MORTAR + DAGGER
        BundleServiceRunner.getBundleServiceRunner(this).onCreate(savedInstanceState); // MORTAR

        //FLOW INIT
        GsonParceler parceler = new GsonParceler(new Gson());
        FlowDelegate.NonConfigurationInstance nonConfig = (FlowDelegate.NonConfigurationInstance) getLastCustomNonConfigurationInstance();
        handlesBack = (HandlesBack) framePathContainerView;
        flowSupport = FlowDelegate.onCreate(nonConfig, getIntent(), savedInstanceState, parceler, History
                .emptyBuilder()
                .push(new FirstPath(5))
                .build(), this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BundleServiceRunner.getBundleServiceRunner(this).onSaveInstanceState(outState); // MORTAR
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return flowSupport.onRetainNonConfigurationInstance(); // FLOW
    }

    @Override
    protected void onPause() {
        flowSupport.onPause(); // FLOW
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        flowSupport.onResume(); // FLOW
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        flowSupport.onNewIntent(intent); // FLOW
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);

        // activityScope may be null in case isWrongInstance() returned true in onCreate()
        if(isFinishing() && activityScope != null) {
            activityScope.destroy();
            activityScope = null;
        }
        super.onDestroy();
    }

    @Override
    public Object getSystemService(String name) {
        // MORTAR
        if(activityScope != null && activityScope.hasService(name)) {
            return activityScope.getService(name);
        }

        //FLOW
        if(flowSupport != null) {
            Object flowService = flowSupport.getSystemService(name);
            if(flowService != null) {
                return flowService;
            }
        }

        //DEFAULT
        return super.getSystemService(name);
    }

    @Override
    public void onBackPressed() {
        if(handlesBack.onBackPressed()) {
            return;
        }
        if(flowSupport.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void dispatch(final Flow.Traversal traversal, final Flow.TraversalCallback callback) {
        Path path = traversal.destination.top();
        setTitle(path.getClass().getSimpleName());

        //ACTION BAR OR TOOL BAR UP NAVIGATION SET, UP NAVIGATION IS UP TO DEV FOR "PRACTICE" (thanks flow u so nice pls)
        //boolean canGoBack = traversal.destination.size() > 1;
        //actionBar.setDisplayHomeAsUpEnabled(canGoBack);
        //actionBar.setHomeButtonEnabled(canGoBack);
        framePathContainerView.dispatch(traversal, new Flow.TraversalCallback() {
            @Override
            public void onTraversalCompleted() {
                invalidateOptionsMenu();
                callback.onTraversalCompleted();
            }
        });
    }
}

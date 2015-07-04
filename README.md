# MortarFlowInitialDemo
This is the v0.3 version of setting up Flow and Mortar and Flow-Path. It took a few hours to figure out why the hell path context wasn't working right, but now it does. Doesn't use ViewPresenters and Module/Components yet. That is the next step.

v0.2 - What is done:

- It literally has just two Paths, so it's not confusing as heck

- movement from one path to the other path within two corresponding Custom Views (click event in FirstView)

- the custom views are bound to the Path Context (yeah, that was bugged in the sample and I had to fix it, go figure)

- Mortar scopes are set up, and Flow is set up

- It has all the classes from the samples to support Mortar scopes within the Path Context

- Some basic Dagger2 stuff is set up for example, nothing special really

v0.3 - What is done:

- Using modules/components to provide data for the custom views

- ViewPresenters are used now, and as such registration to the BundleServiceRunner is done (onLoad / onSave from Mortar)

What is NOT DONE:

- The component must be bound to the Mortar Scope, because currently the component is stored in the PresenterModule to survive configuration change. It must be within the Mortar Scope as a service provided by the ScreenScoper.

NOTE:

 - There's a bunch of weird try-catch null-checks in the FirstView to make previewing the layout work, because otherwise the IDE falls apart with exceptions due to the `context.getSystemService()` call. Seriously.

Okay, so the steps are pretty much the following:

1.) you need all the classes that are in the `utils/flow` and `utils/mortarflow` folder. 

The ones in `flow` are needed to make the basics of Flow work, and the ones in `mortarflow` are required to create a version of the `PathContext` that provides the `MortarScope`. Considering you'll **need** Mortar's `BundleServiceRunner` for preserving states (yes, I know that's not done yet in the example!) 

Please note that you also have to take the `/res/values/ids.xml` file as well, and you need an `activity_main.xml` that has a `MortarScreenSwitcherFrame` class within. It's a fancy frame layout that can create a path container that creates mortar-scope-compatible pathcontexts. Luckily, all of that is provided by the Sample.

2.) [MORTAR] Set up the Root Scope in your Application. Don't forget to add the custom application class to the manifest when you do. 
    
    public class CustomApplication
            extends Application {
        public static final String TAG = CustomApplication.class.getSimpleName();
    
        private MortarScope rootScope;
    
        @Override
        public void onCreate() {
            super.onCreate();
        }
    
        @Override
        public Object getSystemService(String name) {
            if(rootScope == null) {
                rootScope = MortarScope.buildRootScope()
                        .withService(InjectorService.TAG, new InjectorService(this))
                        .build("Root");
            }
            if(rootScope.hasService(name)) { // if the additional "Context" service is within Mortar
                return rootScope.getService(name);
            }
            return super.getSystemService(name); // otherwise return application level context system service
        }
    }

Now as you can see, you can bind "services" to the scope by a string tag. If you override `getSystemService()` on context to first seek out classes from the mortar scope, you'll be able to provide them to your Activities and other contexts, and most importantly to every View directly that has a PathContext.
    
3.) [MORTAR] Now set up the activity mortar scope.

    public class MainActivity
            extends AppCompatActivity 
            // ...
    {
    
        private MortarScope activityScope;
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ....
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
             ....
        }
    
        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            BundleServiceRunner.getBundleServiceRunner(this).onSaveInstanceState(outState); // MORTAR
        }
    
        ...
    
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
    
            ...
    
            //DEFAULT
            return super.getSystemService(name);
        }
        ...
    }

3.1.) [MORTAR + DAGGER] You might be wondering, InjectorService is just an `ApplicationComponent` that's made available through Mortar.

    public class InjectorService {
        public static final String TAG = InjectorService.class.getSimpleName();
    
        private ApplicationComponent applicationComponent; //dagger2 app level component
    
        InjectorService(CustomApplication customApplication) {
            AppContextModule appContextModule = new AppContextModule(customApplication);
            applicationComponent = DaggerApplicationComponent.builder()
                    .appContextComponent(DaggerAppContextComponent.builder()
                            .appContextModule(appContextModule)
                            .build())
                    .appDataComponent(DaggerAppDataComponent.create())
                    .appDomainComponent(DaggerAppDomainComponent.create())
                    .appPresentationComponent(DaggerAppPresentationComponent.create())
                    .appUtilsComponent(DaggerAppUtilsComponent.create())
                    .build();
        }
    
        public ApplicationComponent getInjector() { //return the app component to inject `this` with it
            return applicationComponent;
        }
    
        public static InjectorService get(Context context) {
            //this is needed otherwise the compiler is whining. -_-
            //noinspection ResourceType
            return (InjectorService)context.getSystemService(TAG);
        }
    }
    
4.) [FLOW] Once you've set up Mortar, you're ready to rumble. You need to set up all the **...**ed areas for Flow to function properly. Most of this is based on Samples.

    public class MainActivity
            extends AppCompatActivity
            implements Flow.Dispatcher { //needed for FLOW SUPPORT
    
        @Bind(R.id.main_path_container)
        public MortarScreenSwitcherFrame framePathContainerView;
    
        private HandlesBack handlesBack;
        private FlowDelegate flowSupport;
    
        // ...
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
    
            ////NOTE: FLOW-PATH DID NOT WORK DIRECTLY FROM SAMPLE.
            ////So I forced the ROOT to be the container itself, 
            ////and everything within gets a PathContext of their own. It works.
    
            //FLOW PATH INIT
            PathContext pathContext = PathContext.root(this);
            framePathContainerView = (MortarScreenSwitcherFrame) LayoutInflater.from(this)
                    .cloneInContext(pathContext)
                    .inflate(R.layout.activity_main, null);
            setContentView(framePathContainerView);
    
            // ...
    
            //FLOW INIT
            GsonParceler parceler = new GsonParceler(new Gson());
            FlowDelegate.NonConfigurationInstance nonConfig = (FlowDelegate.NonConfigurationInstance) getLastCustomNonConfigurationInstance();
            handlesBack = (HandlesBack) framePathContainerView;
            flowSupport = FlowDelegate.onCreate(nonConfig, getIntent(), savedInstanceState, parceler, History.single(new FirstPath(5)), this);
        }
    
        // ...
    
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
    
        // ...
    
        @Override
        public Object getSystemService(String name) {
            // ...
    
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
            setTitle(path.getClass().getSimpleName()); //ACTIVITY TITLE
    
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
    
5.) [FLOW-PATH] Create some `Path` subclasses that represent your History within your application. Paths also have a `Context` associated with them which is bound to the View that is inflated based on the resource identifier specified in the annotation. This annotation magic is done within `SimplePathContainerView`, so if you want to replace it with an interface in a `BasePath` of sorts, then you can if you want.

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

So this is just a Path subclass with its own HashCode and Equals implementation, and it can get parameters if you want.

6.) [FLOW+MORTAR] Use the Flow Path to manipulate Flow to reach a particular state of the app, while use Mortar to access the Services in a given scope.
    
    public class FirstView extends LinearLayout {
        public static final String TAG = FirstView.class.getSimpleName();
    
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
            Log.d(TAG, "FIRST VIEW CONTEXT: " + this.getContext() + " " + this.getContext().hashCode());
            FirstPath firstPath = Path.get(this.getContext());
            Log.d(TAG, "First Path Parameter: " + firstPath.parameter);
        }
    
        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            ButterKnife.unbind(this);
        }
    }
    
and

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
            InjectorService.get(getContext()).getInjector().inject(this); // MORTAR!
            SecondPath secondPath = Path.get(getContext());
            System.out.println("SECOND PATH: " + secondPath); //SecondPath :)
        }
    }

and

    <?xml version="1.0" encoding="utf-8"?>
    <home.mortarflow.presentation.view.views.SecondView xmlns:android="http://schemas.android.com/apk/res/android"
        android:orientation="vertical" android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:gravity="center">
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Hello from SECOND path!"/>
    </home.mortarflow.presentation.view.views.SecondView>
    
So that's the first part for today. To-do list:

- ViewPresenters

- Module + Components for providing data

- Make new guide once those are actually done

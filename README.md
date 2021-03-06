# [DEPRECATED] in favor of Mortar Flow Setup.

https://github.com/Zhuinden/MortarFlowSetup

But the guide here is totally fine to read just so you get a better idea of what's going on.

# MortarFlowInitialDemo
This is the v0.7 version of setting up Flow and Mortar and Flow-Path. It took a few hours to figure out why the hell path context wasn't working right, but now it does. Doesn't use ViewPresenters and Module/Components yet. That is the next step.

v0.1 - What is done:

- It literally has just two Paths, so it's not confusing as heck

- movement from one path to the other path within two corresponding Custom Views (click event in FirstView)

- the custom views are bound to the Path Context

- Mortar scopes are set up, and Flow is set up

- It has all the classes from the samples to support Mortar scopes within the Path Context

- Some basic Dagger2 stuff is set up for example, nothing special really

v0.2 - What is done:

 - Properly bind the first PATH context to the first view, it was a bug in `AppCompatActivity` as per the Flow github issue (using App context's inflater service instead)

v0.3 - What is done:

- Using modules/components to provide data for the custom views

- ViewPresenters are used now, and as such registration to the BundleServiceRunner is done (onLoad / onSave from Mortar)

v0.4 - What is done:

- The component is now bound to the Mortar Scope (thanks to "BasePath" that contains the component factory method)

- Path is now responsible for component creation
 
v0.5 - What is done:

- Removed unnecessary code from screen scoper and some other stuff.
 
v0.6 - What is done:

- Added `getScopeName()` method to `BasePath` (and the necessary changes to `MortarContextFactory`). to allow multiple instances of the same Path with different parameters.

v0.7 - What is done:

- Changed `GsonParceler` to `ParcelableParceler` because GsonParceler actually doesn't work.
- Fixed a bug regarding `flowSupport.onSaveInstanceState()` not being called, and history not being preserved through process death.
- Changed Paths to Parcelable.
- Paths now save the presenter state into bundle for process death.
 
WHAT TO DO NEXT:

- There must be a way to make the Path classes a bit less monolithic.
- Add fix described in https://github.com/square/flow/issues/116

Okay, so the steps are pretty much the following:

1.) you need all the classes that are in the `utils/flow` and `utils/mortarflow` folder. 

The ones in `flow` are needed to make the basics of Flow work, and the ones in `mortarflow` are required to create a version of the `PathContext` that provides the `MortarScope`. Considering you'll **need** Mortar's `BundleServiceRunner` for preserving states (yes, I know that's not done yet in the example!) 

Please note that you also have to take the `/res/values/ids.xml` file as well, and you need an `activity_main.xml` that has a `MortarScreenSwitcherFrame` class within. It's a fancy frame layout that can create a path container that creates mortar-scope-compatible pathcontexts. Luckily, all of that is provided by the Sample.

2.) [MORTAR] Set up the Root Scope in your Application. Don't forget to add the custom application class to the manifest when you do. 
    
```java
    public class CustomApplication
            extends Application {
        public static final String TAG = CustomApplication.class.getSimpleName();
    
        private MortarScope rootScope;
    
        @Override
        public void onCreate() {
            super.onCreate();
        }
    
        @Override
        public Object getSystemService(String name) { //this is called BEFORE onCreate() on some Samsung devices
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
```

Now as you can see, you can bind "services" to the scope by a string tag. If you override `getSystemService()` on context to first seek out classes from the mortar scope, you'll be able to provide them to your Activities and other contexts, and most importantly to every View directly that has a PathContext.
    
3.) [MORTAR] Now set up the activity mortar scope.

```java
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
```

3.1.) [MORTAR + DAGGER] You might be wondering, InjectorService is just an `ApplicationComponent` that's made available through Mortar.

```java
    public class InjectorService {
        public static final String TAG = InjectorService.class.getSimpleName();
    
        private ApplicationComponent applicationComponent; //dagger2 app level component
    
        InjectorService(CustomApplication customApplication) {
            AppContextModule appContextModule = new AppContextModule(customApplication);
            applicationComponent = DaggerApplicationComponent.builder()
                            .appContextModule(appContextModule)
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
```
    
4.) [FLOW] Once you've set up Mortar, you're ready to rumble. You need to set up all the **...**ed areas for Flow to function properly. Most of this is based on Samples.

```java
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
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            flowSupport.onSaveInstanceState(outState); // FLOW
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
```
    
5.) [FLOW-PATH] Create some `Path` subclasses that represent your History within your application. Paths also have a `Context` associated with them which is bound to the View that is inflated based on the resource identifier specified in the annotation. This annotation magic is done within `SimplePathContainerView`, so if you want to replace it with an interface in a `BasePath` of sorts, then you can if you want.

```java
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
```

So this is just a Path subclass with its own HashCode and Equals implementation, and it can get parameters if you want.

6.) [FLOW+MORTAR] Use the Flow Path to manipulate Flow to reach a particular state of the app, while use Mortar to access the Services in a given scope.
    
```java
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
```
    
and

```java
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
```

and

```java
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
```
    
So that's the first part for today. To-do list:

- ViewPresenters

- Module + Components for providing data

- Make new guide once those are actually done


GUIDE UPDATE:

This is a continuation of [Basics of experimenting with setting up Mortar + Flow (Part 1)](https://www.reddit.com/r/androiddev/comments/3bte7p/basics_of_experimenting_with_setting_up_mortar/)

If you don't remember, the previous state had the following:

- Two paths, going from first path to second path

- set up root and activity Mortar scopes

- set up Flow and FlowDelegate in MainActivity

- set up PathContext in a working fashion, also including a fix for the `AppCompatActivity cloneInContext()` bug

- Custom Views that can access their Path from the context as such

It had the following missing:

- ViewPresenters for the Paths/Views

- Components/Modules to inject data into view

- Providing the view-scoped component from the MortarScope to survive configuration change

So what's up?

1.) After looking a lot at viewpresenters and components, you just need to provide an implementation of the view presenter, and specify your component and module. It's pretty straightforward at first.

```java
    public class FirstPath
            extends BasePath {
        public final int parameter;
    
        public FirstPath(int parameter) {
            this.parameter = parameter;
        }
    
        //hashcode, equals 

        @Override
        public int getLayout() {
            return R.layout.path_first;
        }
    
        @Override
        public FirstViewComponent createComponent() {
            FirstPath.FirstViewComponent firstViewComponent = DaggerFirstPath_FirstViewComponent.builder()
                    .applicationComponent(InjectorService.obtain())
                    .firstViewModule(new FirstPath.FirstViewModule(parameter))
                    .build();
            return firstViewComponent;
        }
    
        @ViewScope //needed
        @Component(dependencies = {ApplicationComponent.class}, modules = {FirstViewModule.class})
        public interface FirstViewComponent
                extends ApplicationComponent {
            String data();
    
            FirstViewPresenter firstViewPresenter();
    
            void inject(FirstView firstView);
        }
    
        @Module
        public static class FirstViewModule {
            private int parameter;
   
            public FirstViewModule(int parameter) {
                this.parameter = parameter;
            }
    
            @Provides
            public String data(Context context) {
                return context.getString(parameter);
            }
    
            @Provides
            @ViewScope //this makes it get a viewscoped-provider
            public FirstViewPresenter firstViewPresenter() {
                return new FirstViewPresenter();
            }
        }
    
        public static class FirstViewPresenter
                extends ViewPresenter<FirstView> {
            public static final String TAG = FirstViewPresenter.class.getSimpleName();
    
            @Override
            protected void onSave(Bundle outState) {
                super.onSave(outState);
                FirstView firstView = getView();
                outState.putString("input", firstView.getInput());
            }
    
            @Override
            protected void onLoad(Bundle savedInstanceState) {
                super.onLoad(savedInstanceState);
                if(!hasView()) {
                    return;
                }
                FirstView firstView = getView();
                if(savedInstanceState != null) { //needed check
                    firstView.setInput(savedInstanceState.getString("input"));
                }
            }
    
            public void goToNextActivity() {
                Flow.get(getView()).set(new SecondPath());
            }
        }
    }
```

The `@ViewScope` allows the presenter to be created only once per that particular component. Please note that you have to specify the scope on the `@Provides` annotated method too, otherwise it will be unscoped and you'll get a new instance every time you inject, which ruins the whole point of the mortar scope.
    
The magic method calls are actually the fact that I have removed the `@Layout` annotation because to be frank, this runtime annotation processing bugs me to death - it makes the code very lengthy because you have to cache it, otherwise it's slow. And that I made an `InjectorService.obtain()` call to get the `ApplicationComponent` (component dependency) **without** a context, but I'm getting it directly from the root scope.

```java
    public static ApplicationComponent obtain() {
        return ((InjectorService) MortarScope.getScope(ApplicationHolder.INSTANCE.getApplication())
                .getService(TAG)).getInjector();
    }
```

and

```java
    public abstract class BasePath
            extends Path {
        public abstract int getLayout();
    
        public abstract Object createComponent();
    }
```
    
Wait, what? How do you even use that? Apparently the `ScreenScoper` was responsible for parsing the `@WithModule` and `@WithModuleFactory` annotations to bind the `ObjectGraphService` into the Mortar Scope, so that's where you need to set your component too. If you check the original `ScreenScoper`, I removed a bunch of Dagger1 and annotation processing related stuff and made it much shorter (and readable).

```java
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
                        .withService(DaggerService.TAG, basePath.createComponent())
                        .build(name);
            }
            return childScope;
        }
    }
```

The other change is in `SimplePathContainer`, as instead of ripping out the value of the `@Layout`annotation, it just calls the `getLayout()` method.

To make the `Component` accessible, I added a `DaggerService` method that essentially just returns the component, and casts it to `T` aka whatever you specify the return value to be. Not type-safe? Well, the `ScreenScoper` cannot really parametrize the modules, so the Path needs to know, but I didn't bother with `T` binding the BasePath. It still works fine.

```java
    public class DaggerService {
        public static final String TAG = DaggerService.class.getSimpleName();
    
        @SuppressWarnings("unchecked")
        public static <T> T getComponent(Context context) {
            //noinspection ResourceType
            return (T) context.getSystemService(TAG);
        }
    }
```

2.) Now let's accomodate the Custom View with the new setup of our view scoped components from within the `FirstView` custom view, and inject data into it!

```java
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
            if(!isInEditMode()) {
                FirstPath.FirstViewComponent firstViewComponent = DaggerService.getComponent(context);
                firstViewComponent.inject(this);
            }
        }
    
        @Override
        protected void onFinishInflate() {
            super.onFinishInflate();
            if(!isInEditMode()) {
                ButterKnife.bind(this);
                dataDisplay.setText(data);
            }
        }
    
        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if(!isInEditMode()) {
                firstViewPresenter.takeView(this);
            }
        }
    
        @Override
        protected void onDetachedFromWindow() {
            if(!isInEditMode()) {
                firstViewPresenter.dropView(this);
                ButterKnife.unbind(this);
            }
            super.onDetachedFromWindow();
        }
    
        public String getInput() {
            return input.getText().toString();
        }
    
        public void setInput(String inputText) {
            this.input.setText(inputText);
        }
    }
```
    
That `UnsupportedOperationException` and the `presenter` being `null` happens when Android Studio's preview renderer attempts to inflate the layout, then crashes out because of the `context.getSystemService()` call, and then as the dagger injector component isn't available, the `presenters` won't be displayed either. Whatev', right? At least it works once you catch the exceptions and add two null-checks.

I'll show the other Path and other View because it's more concise.

```java
    public class SecondPath
            extends BasePath {
        @Override
        public int getLayout() {
            return R.layout.path_second;
        }
    
        @Override
        public SecondPath.SecondViewComponent createComponent() {
            return DaggerSecondPath_SecondViewComponent.builder()
                    .applicationComponent(InjectorService.obtain())
                    .secondViewModule(new SecondViewModule())
                    .build();
        }
    
        @ViewScope
        @Component(dependencies = {ApplicationComponent.class}, modules = {SecondPath.SecondViewModule.class})
        public interface SecondViewComponent
                extends ApplicationComponent {
            SecondViewPresenter secondViewPresenter();
    
            void inject(SecondView secondView);
        }
    
        @Module
        public static class SecondViewModule {
            @Provides
            @ViewScope
            public SecondViewPresenter secondViewPresenter() {
                return new SecondViewPresenter();
            }
        }
    
        public static class SecondViewPresenter
                extends ViewPresenter<SecondView> {
            public static final String TAG = SecondViewPresenter.class.getSimpleName();
    
            @Override
            protected void onSave(Bundle outState) {
                super.onSave(outState);
            }
    
            @Override
            protected void onLoad(Bundle savedInstanceState) {
                super.onLoad(savedInstanceState);
            }
        }
    }
```
    
and

```java    
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
            }
        }
    
        @Override
        protected void onFinishInflate() {
            super.onFinishInflate();
        }
    
        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if(!isInEditMode()) {
                secondViewPresenter.takeView(this);
            }
        }
    
        @Override
        protected void onDetachedFromWindow() {
            if(!isInEditMode()) {
                secondViewPresenter.dropView(this);
            }
            super.onDetachedFromWindow();
        }
    }
```
    
And with that, it works!

TO-DO list:

- Create an example for "subflows", which are essentially supposedly a  `MortarSwitcherFrame`inside a new type of `MortarSwitcherFrame` that redefines the `HandlesBack onBackPressed()` method to check in its child first. The subcontainer probably also needs to be inflated manually with its own path.

- Maaaybe figure out the `MasterDetailContainer` example, because that one is magic. The ones that was deleted in the following gist: https://github.com/square/flow/commit/3ffb3f9f3cd1649d431146530f19394e9d0850bf

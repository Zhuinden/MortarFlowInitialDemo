package home.mortarflow.utils.flow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import flow.Flow;
import flow.path.Path;
import flow.path.PathContainer;
import flow.path.PathContext;
import flow.path.PathContextFactory;
import home.mortarflow.utils.custom_path.BasePath;

import static flow.Flow.Direction.REPLACE;

/**
 * Provides basic right-to-left transitions. Saves and restores view state.
 * Uses {@link PathContext} to allow customized sub-containers.
 */
public class SimplePathContainer
        extends PathContainer {
    private static final String TAG = SimplePathContainer.class.getSimpleName();

    private final PathContextFactory contextFactory;

    public SimplePathContainer(int tagKey, PathContextFactory contextFactory) {
        super(tagKey);
        this.contextFactory = contextFactory;
    }

    @Override
    protected void performTraversal(final ViewGroup containerView, final TraversalState traversalState, final Flow.Direction direction, final Flow.TraversalCallback callback) {
        final PathContext context;
        final PathContext oldPath;
        if(containerView.getChildCount() > 0) {
            Log.d(TAG, "Container View Child count was > 0");
            oldPath = PathContext.get(containerView.getChildAt(0).getContext());
        } else {
            Log.d(TAG, "Container View Child Count was == 0");
            oldPath = PathContext.root(containerView.getContext());
        }
        Log.d(TAG, "Old Path is: " + oldPath);
        Path to = traversalState.toPath();
        Log.d(TAG, "TO path is: " + to);

        View newView;
        context = PathContext.create(oldPath, to, contextFactory);
        Log.d(TAG, "TO path layout pathcontext is " + context);

        int layout = ((BasePath) to).getLayout(); //removed annotation
        newView = LayoutInflater.from(context.getApplicationContext()) //fixed first path error
                .cloneInContext(context)
                .inflate(layout, containerView, false);
        Log.d(TAG, "NEW VIEW context: " + newView.getContext());

        View fromView = null;
        if(traversalState.fromPath() != null) {
            fromView = containerView.getChildAt(0);
            traversalState.saveViewState(fromView);
        }
        traversalState.restoreViewState(newView);

        if(fromView == null || direction == REPLACE) {
            containerView.removeAllViews();
            containerView.addView(newView);
            oldPath.destroyNotIn(context, contextFactory);
            callback.onTraversalCompleted();
        } else {
            containerView.addView(newView);
            final View finalFromView = fromView;
            ViewUtils.waitForMeasure(newView, new ViewUtils.OnMeasuredCallback() {
                @Override
                public void onMeasured(View view, int width, int height) {
                    runAnimation(containerView, finalFromView, view, direction, new Flow.TraversalCallback() {
                        @Override
                        public void onTraversalCompleted() {
                            containerView.removeView(finalFromView);
                            oldPath.destroyNotIn(context, contextFactory);
                            callback.onTraversalCompleted();
                        }
                    });
                }
            });
        }
    }

    private void runAnimation(final ViewGroup container, final View from, final View to, Flow.Direction direction, final Flow.TraversalCallback callback) {
        Animator animator = createSegue(from, to, direction);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                container.removeView(from);
                callback.onTraversalCompleted();
            }
        });
        animator.start();
    }

    private Animator createSegue(View from, View to, Flow.Direction direction) {
        boolean backward = direction == Flow.Direction.BACKWARD;
        int fromTranslation = backward ? from.getWidth() : -from.getWidth();
        int toTranslation = backward ? -to.getWidth() : to.getWidth();

        AnimatorSet set = new AnimatorSet();

        set.play(ObjectAnimator.ofFloat(from, View.TRANSLATION_X, fromTranslation));
        set.play(ObjectAnimator.ofFloat(to, View.TRANSLATION_X, toTranslation, 0));

        return set;
    }
}
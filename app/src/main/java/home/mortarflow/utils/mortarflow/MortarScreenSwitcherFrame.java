package home.mortarflow.utils.mortarflow;

import android.content.Context;
import android.util.AttributeSet;

import flow.path.Path;
import home.mortarflow.R;
import home.mortarflow.utils.flow.FramePathContainerView;
import home.mortarflow.utils.flow.SimplePathContainer;

public class MortarScreenSwitcherFrame extends FramePathContainerView {
    public MortarScreenSwitcherFrame(Context context, AttributeSet attrs) {
        super(context, attrs, new SimplePathContainer(R.id.screen_switcher_traversal_state_holder_tagkey,
                Path.contextFactory(new MortarContextFactory())));
    }
}
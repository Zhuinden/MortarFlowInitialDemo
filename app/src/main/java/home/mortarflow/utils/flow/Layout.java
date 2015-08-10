package home.mortarflow.utils.flow;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a class that designates a screen and specifies its layout. A screen is a distinct part of
 * an application containing all information that describes this state.
 *
 * <p>For example, <pre><code>
 * {@literal@}Layout(R.layout.conversation_screen_layout)
 * public class ConversationScreen { ... }
 * </code></pre>
 */
@Deprecated //using BasePath instead
@Retention(RUNTIME) @Target(TYPE) public @interface Layout {
    int value();
}

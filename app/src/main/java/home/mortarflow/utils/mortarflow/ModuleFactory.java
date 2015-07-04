package home.mortarflow.utils.mortarflow;

/** @see WithModuleFactory */
public abstract class ModuleFactory<T> {
    protected abstract Object createDaggerModule(T screen);
}
package core.scenes;

public abstract class Scene {
    public abstract void init();
    public abstract void update(float dt);
    public abstract void cleanup();
}

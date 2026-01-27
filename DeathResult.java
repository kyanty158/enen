class DeathResult {
    public enum Type {
        ALIVE, DEAD
    }

    public Type type;
    public String title;
    public String message;
    public int age;

    public DeathResult(Type type, String title, String message, int age) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.age = age;
    }
}

enum Chapter {
    CHILD("少年期", 0, 12),
    STUDENT("学生期", 13, 22),
    ADULT("社会人期", 23, 59),
    SENIOR("老後", 60, 120);

    private final String title;
    private final int start;
    private final int end;

    Chapter(String title, int start, int end) {
        this.title = title;
        this.start = start;
        this.end = end;
    }

    public String getTitle() {
        return title;
    }

    public boolean inRange(int age) {
        return age >= start && age <= end;
    }

    public static Chapter fromAge(int age) {
        for (Chapter c : values()) {
            if (c.inRange(age)) {
                return c;
            }
        }
        return ADULT;
    }
}

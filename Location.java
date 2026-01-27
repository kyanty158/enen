enum Location {
    HOME("家"),
    SCHOOL("学校"),
    WORK("職場"),
    CITY("街");

    private final String label;

    Location(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Location fromLabel(String label) {
        for (Location loc : values()) {
            if (loc.label.equals(label)) {
                return loc;
            }
        }
        return HOME;
    }
}

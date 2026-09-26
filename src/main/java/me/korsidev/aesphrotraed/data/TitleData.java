package me.korsidev.aesphrotraed.data;

public class TitleData {

    private final String id;
    private final String display;private final String format;
    private final boolean bold;
    private final boolean italic;

    public TitleData(String id, String display, String format, boolean bold, boolean italic) {
        this.id = id;
        this.display = display;
        this.format = format;
        this.bold = bold;
        this.italic = italic;
    }

    public String getId() {
        return id;
    }

    public String getDisplay() {
        return display;
    }

    public String getFormat() {
        return format;
    }

    public boolean isBold() {
        return bold;
    }

    public boolean isItalic() {
        return italic;
    }
}

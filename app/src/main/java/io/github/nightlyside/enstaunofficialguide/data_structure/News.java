package io.github.nightlyside.enstaunofficialguide.data_structure;

public class News {
    public int id;
    public String title;
    public String date;
    public String author;
    public String asso;
    public String text;

    public News(int id, String title, String date, String author, String asso, String text) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.author = author;
        this.asso = asso;
        this.text = text;
    }
}

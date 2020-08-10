package io.github.nightlyside.enstaunofficialguide.data_structure;

public class Association {

    public int id;
    public String name;
    public boolean isOpenToRegister;
    public String description;

    public Association(int id, String name, boolean isOpen, String description) {
        this.id = id;
        this.name = name;
        this.isOpenToRegister = isOpen;
        this.description = description;
    }
}

package io.github.nightlyside.enstaunofficialguide.data_structure;

import com.google.android.gms.maps.model.LatLng;

public class Colloc {
    public int id;
    public String name;
    public String adresse;
    public String description;

    public Colloc(int id, String name, String adresse, String description) {
        this.id = id;
        this.name = name;
        this.adresse = adresse;
        this.description = description;
    }
}

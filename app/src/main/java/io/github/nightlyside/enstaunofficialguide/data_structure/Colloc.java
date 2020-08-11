package io.github.nightlyside.enstaunofficialguide.data_structure;

import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

public class Colloc {
    public int id;
    public String name;
    public String adresse;
    public String description;
    public String bar_ou_colloc;

    public Colloc(int id, String name, String adresse, String description, String bar_ou_colloc) {
        this.id = id;
        this.name = name;
        this.adresse = adresse;
        this.description = description;
        this.bar_ou_colloc = bar_ou_colloc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Colloc colloc = (Colloc) o;
        return id == colloc.id &&
                name.equals(colloc.name) &&
                adresse.equals(colloc.adresse) &&
                description.equals(colloc.description) &&
                bar_ou_colloc.equals(colloc.bar_ou_colloc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, adresse, description, bar_ou_colloc);
    }
}

package io.github.nightlyside.enstaunofficialguide.network;

import org.json.JSONException;

public interface NetworkResponseListener<T> {
    public void getResult(T object) throws JSONException;
}

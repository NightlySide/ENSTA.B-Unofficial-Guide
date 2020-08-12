package io.github.nightlyside.enstaunofficialguide.data_structure;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssoEvent {

    public int id;
    public Association asso;
    public String title;
    public DateHelper startDate;
    public DateHelper endDate;
    public String location;
    public String description;

    public AssoEvent(int id, int asso_id, String title, String str_startDate, String str_endDate, String location, String description) {
        this.id = id;
        this.asso = Association.getAssociationById(asso_id);
        this.title = title;
        this.location = location;
        this.description = description;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
        try {
            startDate = new DateHelper(format.parse(str_startDate));
            endDate = new DateHelper(format.parse(str_endDate));
        } catch (ParseException e) {
            Log.e("AssoEventDebug", e.toString());
        }
    }

    public AssoEvent(int id, int asso_id, String title, DateHelper helper_startDate, DateHelper helper_endDate, String location, String description) {
        this.id = id;
        this.asso = Association.getAssociationById(asso_id);
        this.title = title;
        this.location = location;
        startDate = helper_startDate;
        endDate = helper_endDate;
        this.description = description;
    }

    static public List<AssoEvent> getEventsOnDate(DateHelper date, List<AssoEvent> list)
    {
        List<AssoEvent> res = new ArrayList<>();
        for (AssoEvent ae : list) {
            if ((ae.startDate.isSameDayAs(date) || ae.endDate.isSameDayAs(date)) ||
                    (ae.startDate.isBefore(date) && ae.endDate.isAfter(date))) {
                res.add(ae);
            }
        }
        return res;
    }
}

package org.jlab.hco.persistence.model;

import java.util.Date;

public class SignoffTrendInfo {
    private final Date date;
    private final long count;

    public SignoffTrendInfo(Date date, Number count) {
        this.date = date;
        this.count = count.longValue();
    }

    public Date getDate() {
        return date;
    }

    public long getCount() {
        return count;
    }
}

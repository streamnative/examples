package io.streamnative.examples.flink;

import lombok.Data;

@Data
public class NasaMission {

    private int id;
    private String missionName;
    private int startYear;
    private int endYear;

    public NasaMission(int id, String missionName, int startYear, int endYear) {
        this.id = id;
        this.missionName = missionName;
        this.startYear = startYear;
        this.endYear = endYear;
    }

}

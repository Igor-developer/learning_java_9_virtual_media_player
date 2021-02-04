package com.company;

import java.time.LocalTime;

public class Media {
    final private String songName;
    final private String singer;
    final int duration;

    public Media(String songName, int duration, String singer) {
        this.songName = songName;
        this.singer = singer;
        this.duration = duration;
    }

    @Override
    public String toString() {
        LocalTime time = LocalTime.of(0, 0, 0).plusSeconds(duration);
        return "«" + songName + "» (" + singer + ", " +
                (time.getHour() != 0 ? time.getHour() + " ч " : "") +
                (time.getMinute() != 0 ? time.getMinute() + " м " : "") +
                time.getSecond() + " с)";
    }
}

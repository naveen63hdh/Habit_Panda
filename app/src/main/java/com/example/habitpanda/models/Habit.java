package com.example.habitpanda.models;

public class Habit {

    String id,habitName, habitDesc, time;
    int habitType, days;
    boolean mon,tue,wed,thur,fri,sat,sun,is_completed;

    public Habit(String id, String habitName, String habitDesc, String time, int habitType, int days, boolean mon, boolean tue, boolean wed, boolean thur, boolean fri, boolean sat, boolean sun) {
        this.id = id;
        this.habitName = habitName;
        this.habitDesc = habitDesc;
        this.time = time;
        this.habitType = habitType;
        this.days = days;
        this.mon = mon;
        this.tue = tue;
        this.wed = wed;
        this.thur = thur;
        this.fri = fri;
        this.sat = sat;
        this.sun = sun;
    }

    public Habit() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public String getHabitDesc() {
        return habitDesc;
    }

    public void setHabitDesc(String habitDesc) {
        this.habitDesc = habitDesc;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getHabitType() {
        return habitType;
    }

    public void setHabitType(int habitType) {
        this.habitType = habitType;
    }

    public int getDays() {
        return days;
    }

    public boolean isCompleted() {
        return is_completed;
    }

    public void setIsCompleted(boolean is_completed) {
        this.is_completed = is_completed;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public boolean isMon() {
        return mon;
    }

    public void setMon(boolean mon) {
        this.mon = mon;
    }

    public boolean isTue() {
        return tue;
    }

    public void setTue(boolean tue) {
        this.tue = tue;
    }

    public boolean isWed() {
        return wed;
    }

    public void setWed(boolean wed) {
        this.wed = wed;
    }

    public boolean isThur() {
        return thur;
    }

    public void setThur(boolean thur) {
        this.thur = thur;
    }

    public boolean isFri() {
        return fri;
    }

    public void setFri(boolean fri) {
        this.fri = fri;
    }

    public boolean isSat() {
        return sat;
    }

    public void setSat(boolean sat) {
        this.sat = sat;
    }

    public boolean isSun() {
        return sun;
    }

    public void setSun(boolean sun) {
        this.sun = sun;
    }

    @Override
    public String toString() {
        return "Habit{" +
                "id='" + id + '\'' +
                ", habitName='" + habitName + '\'' +
                ", habitDesc='" + habitDesc + '\'' +
                ", time='" + time + '\'' +
                ", habitType=" + habitType +
                ", days=" + days +
                ", mon=" + mon +
                ", tue=" + tue +
                ", wed=" + wed +
                ", thur=" + thur +
                ", fri=" + fri +
                ", sat=" + sat +
                ", sun=" + sun +
                '}';
    }
}

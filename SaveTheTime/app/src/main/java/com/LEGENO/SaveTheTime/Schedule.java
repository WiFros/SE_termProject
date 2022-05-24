package com.LEGENO.SaveTheTime;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Schedule implements Serializable {
    private String ID = "";//firebase db의 값

    private String title = "";
    private String content = "";

    //시간 사실 스트링이지만 입출력을 LocalDateTime으로 바꿔 LocalDataTime으로 사용하는 듯한 캡슐화
    private String startTime = "";
    private String endTime = "";

    public Schedule() {
    }

    public Schedule(String ID) {
        this.ID = ID;
    }

    public Schedule(String ID, String title, String content, LocalDateTime start, LocalDateTime end) {
        this.ID = ID;
        this.title = title;
        this.content = content;
        this.startTime = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.endTime = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ;
    }

    public Schedule(String title, String content, LocalDateTime start, LocalDateTime end) {
        this.title = title;
        this.content = content;
        this.startTime = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.endTime = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public Schedule(Schedule schedule){
        this.ID = schedule.getID();
        this.title = schedule.getTitle();
        this.content = schedule.getContent();
        this.startTime = schedule.getStartTime();
        this.endTime = schedule.getEndTime();
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStartDate(LocalDateTime start) {
        this.startTime = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void setEndDate(LocalDateTime end) {
        this.endTime = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getID() {
        return ID;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() { return endTime; }

    // Time 값 이용할떄는 이 함수로 출력 바랍니다...
    public LocalDateTime startTimeByClass() {
        LocalDateTime time = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return time;
    }

    public LocalDateTime endTimeByClass() {
        LocalDateTime time = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (super.equals(o))
            return true;

        if (o instanceof Schedule) {
            if (this.ID.equals(((Schedule) o).getID()))
                return true;
        }

        return false;
    }

    /*뭐뭐 필요한지 보고 추가해야 할듯 싶습니다.*/
    public String toString() {
        String str = title;
        str += " " + content;
        str += " " + startTime;
        str += " " + endTime;
        return str;
    }

    // 주어진 날짜가 포함되 있으면 true
    public boolean containsDate(LocalDate date) {
        LocalDate start = this.startTimeByClass().toLocalDate();
        LocalDate end = this.endTimeByClass().toLocalDate();

        if ((start.isBefore(date) || start.isEqual(date)) && (end.isAfter(date) || end.isEqual(date)))
            return true;
        return false;
    }

    public String startDateToString(){
        String[] strings = this.startTime.split("[ ]");
        return strings[0].replace('-','.');
    }

    public String endDateToString(){
        String[] strings = this.endTime.split("[ ]");
        return strings[0].replace('-','.');
    }
}
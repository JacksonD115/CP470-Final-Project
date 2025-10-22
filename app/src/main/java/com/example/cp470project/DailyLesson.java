package com.example.cp470project;

import java.util.List;
import java.io.Serializable;

public class DailyLesson  implements Serializable {

    private static final long serialVersionUID = 1L;
    private int day;
    private String focus;
    private String description;
    private List<Resource> resources;
    private List<PracticeQuestion> practiceQuestions;

    public DailyLesson(int day, String focus, String description, List<Resource> resources, List<PracticeQuestion> practiceQuestions) {
        this.day = day;
        this.focus = focus;
        this.description = description;
        this.resources = resources;
        this.practiceQuestions = practiceQuestions;
    }

    public int getDay() {
        return day;
    }

    public String getFocus() {
        return focus;
    }

    public String getDescription() {
        return description;
    }

    public List<Resource> getResources() { return resources; }
    public List<PracticeQuestion> getPracticeQuestions() { return practiceQuestions; }
}

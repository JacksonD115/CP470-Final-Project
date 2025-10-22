package com.example.cp470project;

public class LearningProfile {
    private String expertiseLevel;
    private int numberOfDays;
    private String endGoal;

    private String topic;


    public LearningProfile(String expertiseLevel, int numberOfDays, String endGoal, String topic) {
        this.expertiseLevel = expertiseLevel;
        this.numberOfDays = numberOfDays;
        this.endGoal = endGoal;
        this.topic = topic;
    }

    public String getExpertiseLevel() {
        return expertiseLevel;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }

    public String getEndGoal() {
        return endGoal;
    }

    public String getTopic() {
        return topic;
    }

    public void setExpertiseLevel(String expertiseLevel) {
        this.expertiseLevel = expertiseLevel;
    }

    public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public void setEndGoal(String endGoal) {
        this.endGoal = endGoal;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public boolean isValid() {
        return expertiseLevel != null && !expertiseLevel.isEmpty()
                && numberOfDays > 0 && endGoal != null && !endGoal.isEmpty() && !topic.isEmpty();
    }

    @Override
    public String toString() {
        return "LearningProfile{" +
                "expertiseLevel='" + expertiseLevel + '\'' +
                ", numberOfDays=" + numberOfDays +
                ", endGoal='" + endGoal + '\'' +
                ", topic='" + topic + '\'' +
                '}';
    }
}

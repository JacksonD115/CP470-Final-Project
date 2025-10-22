package com.example.cp470project;
import java.util.List;
import java.io.Serializable;
public class UltimateQuiz implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<PracticeQuestion> questions;

    public UltimateQuiz(List<PracticeQuestion> questions) {
        this.questions = questions;
    }

    public List<PracticeQuestion> getQuestions() { return questions; }
}

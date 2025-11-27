package com.example.cp470project.data;

import android.content.Context;
import android.util.Log;

import com.example.cp470project.DailyLesson;
import com.example.cp470project.PracticeQuestion;
import com.example.cp470project.Resource;
import com.example.cp470project.data.models.PlanWithLessons;

import java.util.ArrayList;
import java.util.List;

public class LearningRepository {
    private static final String TAG = "LearningRepository";

    private final LearningDatabase database;
    private final DailyLessonDao lessonDao;
    private final ResourceDao resourceDao;
    private final PracticeQuestionDao questionDao;

    public LearningRepository(Context context) {
        database = LearningDatabase.getInstance(context);
        lessonDao = database.lessonDao();
        resourceDao = database.resourceDao();
        questionDao = database.questionDao();
    }

    public interface DataCallback<T> {
        void onComplete(T result);
    }

    public interface VoidCallback {
        void onComplete();
    }

    // Get all plans with their lessons
    public void getAllPlans(DataCallback<List<PlanWithLessons>> callback) {
        LearningDatabase.databaseExecutor.execute(() -> {
            try {
                List<PlanWithLessons> plans = database.planDao().getAllPlansWithLessons();
                callback.onComplete(plans != null ? plans : new ArrayList<>());
            } catch (Exception e) {
                Log.e(TAG, "Error getting all plans", e);
                callback.onComplete(new ArrayList<>());
            }
        });
    }

    // Get a specific plan by ID
    public void getPlanById(long planId, DataCallback<PlanWithLessons> callback) {
        LearningDatabase.databaseExecutor.execute(() -> {
            try {
                PlanWithLessons plan = database.planDao().getPlanWithLessons(planId);
                callback.onComplete(plan);
            } catch (Exception e) {
                Log.e(TAG, "Error getting plan by ID", e);
                callback.onComplete(null);
            }
        });
    }

    // Save a plan with its lessons
    public void savePlan(LearningPlanEntity plan, List<DailyLesson> lessons, VoidCallback callback) {
        LearningDatabase.databaseExecutor.execute(() -> {
            try {
                long planId = database.planDao().insert(plan);

                for (DailyLesson lesson : lessons) {
                    DailyLessonEntity lessonEntity = new DailyLessonEntity(
                            planId,
                            lesson.getDay(),
                            lesson.getFocus(),
                            lesson.getDescription()
                    );
                    long lessonId = lessonDao.insert(lessonEntity);

                    // Save resources
                    for (Resource resource : lesson.getResources()) {
                        ResourceEntity resourceEntity = new ResourceEntity(
                                lessonId,
                                resource.getTitle(),
                                resource.getDescription(),
                                resource.getUrl()
                        );
                        resourceDao.insert(resourceEntity);
                    }

                    // Save practice questions
                    for (PracticeQuestion question : lesson.getPracticeQuestions()) {
                        PracticeQuestionEntity questionEntity = new PracticeQuestionEntity(
                                lessonId,
                                question.getQuestion(),
                                question.getOptionA(),
                                question.getOptionB(),
                                question.getOptionC(),
                                question.getOptionD(),
                                question.getCorrectAnswer(),
                                question.getExplanation()
                        );
                        questionDao.insert(questionEntity);
                    }
                }

                callback.onComplete();
            } catch (Exception e) {
                Log.e(TAG, "Error saving plan", e);
            }
        });
    }

    // Delete a plan
    public void deletePlan(long planId, VoidCallback callback) {
        LearningDatabase.databaseExecutor.execute(() -> {
            try {
                database.planDao().deletePlanById(planId);
                callback.onComplete();
            } catch (Exception e) {
                Log.e(TAG, "Error deleting plan", e);
            }
        });
    }
}
package com.example.cp470project.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.cp470project.data.models.PlanWithLessons;

import java.util.List;

@Dao
public interface LearningPlanDao {

    @Insert
    long insert(LearningPlanEntity plan);

    @Query("SELECT * FROM learning_plans ORDER BY createdAt DESC")
    @Transaction
    List<PlanWithLessons> getAllPlansWithLessons();

    @Query("SELECT * FROM learning_plans WHERE id = :planId")
    @Transaction
    PlanWithLessons getPlanWithLessons(long planId);

    @Delete
    void delete(LearningPlanEntity plan);

    @Query("DELETE FROM learning_plans WHERE id = :planId")
    void deletePlanById(long planId);
}
package com.kidsapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.kidsapp.data.model.Task;
import com.kidsapp.data.repository.TaskRepository;
import java.util.List;

/**
 * ViewModel for Tasks
 */
public class TaskViewModel extends AndroidViewModel {
    private TaskRepository taskRepository;
    private MutableLiveData<List<Task>> tasksLiveData;
    private MutableLiveData<Task> taskLiveData;
    private MutableLiveData<String> errorLiveData;
    private MutableLiveData<Boolean> isLoadingLiveData;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository(application);
        tasksLiveData = new MutableLiveData<>();
        taskLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
        isLoadingLiveData = new MutableLiveData<>();
    }

    public void getTasks(String childId) {
        isLoadingLiveData.setValue(true);
        taskRepository.getTasks(childId, new TaskRepository.TasksCallback() {
            @Override
            public void onSuccess(List<Task> tasks) {
                isLoadingLiveData.setValue(false);
                tasksLiveData.setValue(tasks);
            }

            @Override
            public void onError(String error) {
                isLoadingLiveData.setValue(false);
                errorLiveData.setValue(error);
            }
        });
    }

    public void createTask(Task task) {
        isLoadingLiveData.setValue(true);
        taskRepository.createTask(task, new TaskRepository.TaskCallback() {
            @Override
            public void onSuccess(Task createdTask) {
                isLoadingLiveData.setValue(false);
                taskLiveData.setValue(createdTask);
            }

            @Override
            public void onError(String error) {
                isLoadingLiveData.setValue(false);
                errorLiveData.setValue(error);
            }
        });
    }

    public LiveData<List<Task>> getTasks() {
        return tasksLiveData;
    }

    public LiveData<Task> getTask() {
        return taskLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }
}


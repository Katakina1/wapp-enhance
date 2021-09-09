package com.xforceplus.wapp.modules.job.entity;

public class HostTaskEntity {
    private  Long id;
    private  String taskName;
    private  String taskTime;
    private  String params1;
    private  String params2;
    private  String params3;
    private  String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

    public String getParams1() {
        return params1;
    }

    public void setParams1(String params1) {
        this.params1 = params1;
    }

    public String getParams2() {
        return params2;
    }

    public void setParams2(String params2) {
        this.params2 = params2;
    }

    public String getParams3() {
        return params3;
    }

    public void setParams3(String params3) {
        this.params3 = params3;
    }
}

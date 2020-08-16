package com.example.roomie;

/**
 * This class allows us to communicate with the UI (Activities) via LiveData when performing
 * asynchronous jobs with firestore.
 *
 * It functions as a container of the job status and the job related data.
 * A LiveData observer can observe the job status and get the relevant data upon updates performed
 * by the firestore callback function..
 */
public class FirestoreJob {

    public enum JobStatus {
        IN_PROGRESS,
        SUCCESS,
        ERROR
    }

    public enum JobErrorCode {
        GENERAL,
        USER_NOT_SIGNED_IN,
        DOCUMENT_NOT_FOUND,
        INVITATION_EXPIRED,
    }

    private JobStatus jobStatus;

    private JobErrorCode jobErrorCode;


    public FirestoreJob() {

    }

    public FirestoreJob(JobStatus status) {
        this.jobStatus = status;
    }

    public FirestoreJob(JobStatus jobStatus, JobErrorCode jobErrorCode) {
        this.jobStatus = jobStatus;
        this.jobErrorCode = jobErrorCode;
    }


    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public JobErrorCode getJobErrorCode() {
        return jobErrorCode;
    }


    public void setJobErrorCode(JobErrorCode jobErrorCode) {
        this.jobErrorCode = jobErrorCode;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

}
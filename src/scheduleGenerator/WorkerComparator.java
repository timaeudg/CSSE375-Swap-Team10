package scheduleGenerator;

import java.io.Serializable;
import java.util.Comparator;

public class WorkerComparator implements Serializable, Comparator<Worker> {
    String jobID;

    public WorkerComparator(String jobID) {
        this.jobID = jobID;
    }

    @Override
    public int compare(Worker o1, Worker o2) {
        return o1.numWorkedForJob(this.jobID) - o2.numWorkedForJob(this.jobID);
    }

}

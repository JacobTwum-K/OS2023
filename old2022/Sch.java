// Importing all the classes in the java.util package.
import java.util.*;

// Importing the exit method from the System class.
import static java.lang.System.exit;

/**
 * It takes a list of jobs and a policy and executes the jobs according to the policy
 */
public class Sch {

// A method that takes a hashmap of options and executes the jobs according to the policy.
    public static void runJobs(HashMap<String, String> opts) {
        int executionStartTime = 0;
        int executionEndTime = 0;


// Checking if the policy is null and if it is it prints a message and exits the program.
        if (opts.get("policy") == null) {
            System.out.println("Please pass a policy ");
            System.out.println("for example: java Sch -j <job1,job2,job3> -p <FIFO|SJF|RR|STC>");
            exit(0);
        }
// Checking if the jobs string is null and if it is it prints a message and exits the program.
        if (opts.get("jobs") == null) {
            System.out.println("Please pass a jobs string ");
            System.out.println("for example: java Sch -j <job1,job2,job3> -p <FIFO|SJF|RR|STC>");
            exit(1);
        }

// Converting the jobs string into an arraylist of integers.
        ArrayList<Integer> jobs = new ArrayList<>();
        for (String job : opts.get("jobs").split(","))
            jobs.add(Integer.valueOf(job));

        jobs.forEach(System.out::println);

// Executing the jobs according to the FIFO policy.
        if (opts.get("policy").equals("FIFO")) {
            for (int i = 1; i <= jobs.size(); i++) {
                executionEndTime += jobs.get(i - 1);
                String info = "Execution Time: " + executionStartTime + " Executing Job " + (i - 1) + " Time span: " + jobs.get(i - 1) + " ended @ " + executionEndTime + " secs";
                executionStartTime += jobs.get(i - 1);
                System.out.println(info);
            }
        }
// Sorting the jobs according to their execution time and executing them in the order of their
// execution time.
        if (opts.get("policy").equals("SJF")) {
            jobs.sort(Comparator.naturalOrder());
            System.out.println(jobs);
            for (int i = 1; i <= jobs.size(); i++) {
                executionEndTime += jobs.get(i - 1);
                String info = "Execution Time: " + executionStartTime + " Executing Job " + " Time span: " + executionEndTime;
                executionStartTime += jobs.get(i - 1);
                System.out.println(info);
            }
            exit(0);
        }

// Checking if the policy is RR and if it is it executes the jobs according to the RR policy.
        if (opts.get("policy").equals("RR")) {
//            Double timeSlice = jobs.stream().reduce(0,(x,y)->x+y)*0.1;

//            System.out.println(timeSlice.intValue());

//            System.out.println("Hello: "+jobs.stream().reduce(0,(x,y)->x+y));
// Checking if the quantum time is null and if it is it calculates the quantum time by multiplying the
// sum of the jobs by 0.1 and if it is not null it parses the quantum time from the hashmap.
            Integer timeSlice = opts.get("quantum") == null ? (int) (jobs.stream().reduce(0, (x, y) -> x + y) * 0.1) : Integer.parseInt(opts.get("quantum"));
            System.out.println("Here it is: " + timeSlice);
            Queue<Integer> jobQueue = new LinkedList<>();
            HashMap<Integer, Integer> jobsMap = new HashMap<>();

            for (int i = 0; i < jobs.size(); i++) {
                jobQueue.add(i);
                jobsMap.put(i, jobs.get((i)));

            }

// Executing the jobs according to the RR policy.
            System.out.println("----------Jobs Executions----------");
            System.out.println(jobsMap);
            while (!jobQueue.isEmpty()) {
                int jobIndex = jobQueue.poll();
                if (jobsMap.get(jobIndex) > timeSlice) {
                    executionEndTime += timeSlice;
                    jobsMap.put(jobIndex, jobsMap.get(jobIndex) - timeSlice);
                    String info = "Execution Time: " + executionStartTime + " Executing Job " + jobIndex + " Time span: " + timeSlice;
                    executionStartTime += timeSlice;
                    System.out.println(info);
                    jobQueue.add(jobIndex);

                } // Executing the job and printing the execution information.
                else {
                    executionEndTime += jobsMap.get(jobIndex);
                    String info = "Execution Time: " + executionStartTime + " Executing Job " + jobIndex + " Time span: " + jobsMap.get(jobIndex) + " Job Ended at: " + executionEndTime;
                    executionStartTime += jobsMap.get(jobIndex);
                    System.out.println(info);
                }

            }
            exit(0);

        }
        if (opts.get("policy").equals("STC")) {
            List<Integer> incomingJobs = jobs.subList(1, jobs.size());
            int firstJob = jobs.get(0);
            incomingJobs.sort(Comparator.naturalOrder());
            System.out.println(incomingJobs);


            Integer timeSlice = (int) (jobs.get(0) * 0.1);
            System.out.println("This is the number: " + firstJob);
            System.out.println("Here it is: " + timeSlice);
            Queue<Integer> jobQueue = new LinkedList<>();
            HashMap<Integer, Integer> jobsMap = new HashMap<>();

            for (int i = 0; i < incomingJobs.size(); i++) {
                jobQueue.add(i);
                jobsMap.put(i, incomingJobs.get((i)));

            }
            System.out.println("----------Jobs Executions----------");
            System.out.println(jobsMap);
            boolean preemptLock = false;

            while (!jobQueue.isEmpty() || firstJob != 0) {

                if (firstJob != 0 && !preemptLock) {
                    executionEndTime += jobQueue.isEmpty() ? firstJob : timeSlice;
                    firstJob -= jobQueue.isEmpty() ? firstJob : timeSlice;
                    if (firstJob <= 0) firstJob = 0;
                    System.out.println(">>" + firstJob);
                    String info = "Execution Time: " + executionStartTime + " Executing Job 0 Time span: " + timeSlice;
                    info += jobQueue.isEmpty() || firstJob == 0 ? " Job Ended at: " + executionEndTime : "";
                    executionStartTime += timeSlice;
                    preemptLock = true;
                    System.out.println(info);

                    continue;
                }

                if (!jobQueue.isEmpty()) {


                    int jobIndex = jobQueue.peek();
                    if (firstJob != 0 && jobsMap.get(jobIndex).compareTo(firstJob) > 0) {
                        preemptLock = false;
                        continue;
                    }
                    executionEndTime += jobsMap.get(jobIndex);
//                    jobsMap.put(jobIndex,jobsMap.get(jobIndex)-timeSlice);
                    String info = "Execution Time: " + executionStartTime + " Executing Job " + (jobIndex + 1) + " Time span: " + jobsMap.get(jobIndex) + " Job Ended at: " + executionEndTime;
//                    String info = "Execution Time: " + executionStartTime + " Executing Job " + jobIndex + " Time span: " + timeSlice;
                    executionStartTime += jobsMap.get(jobIndex);
                    System.out.println(info);
                    jobQueue.poll();

                }
                if (jobQueue.isEmpty())
                    preemptLock = false;


            }


        }

// Checking if the jobs string is null and if it is it prints a message and exits the program.
        if (opts.get("jobs") == null) {
            System.out.println("Please pass a jobs string ");
            System.out.println("for example: java Sch -j <job1,job2,job3> -p <FIFO|SJF|RR|STC>");
            exit(0);
        }


    }

   // Parsing the command line arguments and storing them in a hashmap.
    public static void main(String[] args) {
        HashMap<String, String> opt = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equals("-jobs"))
                opt.put("jobs", args[i + 1]);
            else if (args[i].equals("-policy"))
                opt.put("policy", args[i + 1]);

            else if (args[i].equals("-quantumTime"))
                opt.put("quantum", args[i + 1]);

        }


// Printing the hashmap of options.
        runJobs(opt);

        System.out.println(opt);
    }
}

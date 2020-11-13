import java.io.File;
import java.io.FileNotFoundException;
import java.util.PriorityQueue;
import java.util.*;
import java.math.*;

public class Run {

    public static int processesCompleted = 0;
    public static int createdProcesses = 0;
    public static long totalCPUTime = 0;
    public static long contextTime = 0;
    public static int[] IOvsCPU = new int[2];
    public static int processRan = 0;
    public static long IOTime = 0;
    public static int IOServices = 0;
    public static int IOServiceTimeTotal = 0;
    public static int CPUsCompleted = 0;
    public static int IOsCompleted = 0;
    public static long CPUTime = 0;
    public static int processTimeTotal = 0;
    public static int CPUIOServTime = 0;
    public static int IOIOServTime = 0;
    public static int CPUIOServs = 0;
    public static int IOIOServs = 0;
    public static long cpuEndTime = 0;
    public static int IOs = 0;
    public static int CPUs = 0;
    public static long nextCreationTime = 0;
    public static long timeForCreation = 0;
    public static long aveTurnAround = 0;
    public static int check = 0;
    public static int readyTime = 0;

    public static long IOTurnAroundTotal = 0;
    public static long CPUTurnAroundTotal = 0;
    public static long totalTime = 0;
    public static long simStartTime = 0;



    public static void main(String[] args) throws InterruptedException {
        IOvsCPU[0] = 0;
        IOvsCPU[1] = 0;
        beginScheduler(readFile());
        //System.out.println(expRand(1000000));

        }

    public static void wait(int ms)
    {
        //System.out.println(((System.currentTimeMillis() + ms) - simStartTime));
        if (ms < totalTime) {
            try
            {
                Thread.sleep(ms);
            }
            catch(InterruptedException ex)
            {
                //System.out.println("DIDNT SLEEP");
                Thread.currentThread().interrupt();
            }
        } else {

        }
    }

        public static Event createEvent(int[] params) {
            int avgProcessTime = params[3];
            Event e = new Event();
            e.setTime(newProcessTime(avgProcessTime));
            e.setCreationTime(System.currentTimeMillis());
            e.setType(determineType(params));
            return e;
        }

        public static int[] readFile() {
            int totalSimulationTime = 0;
            int quantum = 0;
            int contextSwitchTime = 0;
            int averageProcessLength = 0;
            int averageCreationLength = 0;
            int IOBoundPct = 0;
            int IOBoundServiceTime = 0;
            String temp;
            int[] params = new int[7];

            try {
                Scanner scanInput = new Scanner(System.in);
                System.out.println("enter 'params1.txt', 'params2.txt', or 'params3.txt'... etc");
                File myObj = new File(scanInput.nextLine());
                Scanner myReader = new Scanner(myObj);
                myReader.nextLine(); //skip first param comment line

                //READ IN TOTALSIMULATIONTIME
                temp = myReader.next();
                totalSimulationTime = Integer.parseInt(temp);
                myReader.nextLine();
                //System.out.println(totalSimulationTime);

                //READ IN QUANTUM
                temp = myReader.next();
                quantum = Integer.parseInt(temp);
                myReader.nextLine();
                //System.out.println(quantum);

                //READ IN CONTEXTSWITCHTIME
                temp = myReader.next();
                contextSwitchTime = Integer.parseInt(temp);
                myReader.nextLine();
                //System.out.println(contextSwitchTime);

                //READ IN AVERAGEPROCESSLENGTH
                temp = myReader.next();
                averageProcessLength = Integer.parseInt(temp);
                myReader.nextLine();
                //System.out.println(averageProcessLength);

                //READ IN AVERAGECREATIONLENGTH
                temp = myReader.next();
                averageCreationLength = Integer.parseInt(temp);
                myReader.nextLine();
                //System.out.println(averageCreationLength);

                //READ IN IOBOUNDPCT
                temp = myReader.next();
                IOBoundPct = Integer.parseInt(temp);
                myReader.nextLine();
                //System.out.println(IOBoundPct);

                //READ IN IOBOUNDSERVICETIME
                temp = myReader.next();
                IOBoundServiceTime = Integer.parseInt(temp);
                myReader.nextLine();
                //System.out.println(IOBoundServiceTime);

                myReader.close();

            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            params[0] = totalSimulationTime;
            params[1] = quantum;
            params[2] = contextSwitchTime;
            params[3] = averageProcessLength;
            params[4] = averageCreationLength;
            params[5] = IOBoundPct;
            params[6] = IOBoundServiceTime;

            return params;
        }

        public static void beginScheduler(int[] params) throws InterruptedException {

            totalTime = params[0] * 1000;
            int contextSwitchTime = params[2];

            long timeStart = System.currentTimeMillis();
            simStartTime = timeStart;
            long simEndTime = params[0] * 1000;

            PriorityQueue<Event> readyQueue = new PriorityQueue<Event>();

            timeForCreation = timeStart; //TIME SINCE CREATION INITAL
            nextCreationTime = newCreationTime(params[4]);
            timeForCreation += nextCreationTime;

            readyQueue.add(createEvent(params)); //adds intial process time
            System.out.println("ADDED PROCESS OF LENGTH: " + readyQueue.peek());

            //BODY

            while ((System.currentTimeMillis() - timeStart) < simEndTime) {
                //CONTEXT SWITCH
                wait(contextSwitchTime);
                contextTime += contextSwitchTime;

                System.out.println("TIME IS: " + (System.currentTimeMillis() - timeStart));
                runProcess(readyQueue, timeStart, params);
            }

            System.out.println("FINISH");

            //TODO: final method
            finalOutputs(params);

            //System.out.println(processesCompleted);
        }

        public static void finalOutputs(int[] params) {

            double CPUTurnAroundFinal;
            if (processesCompleted > 0) {
                CPUTurnAroundFinal = ((double)(CPUTurnAroundTotal/1000) / CPUsCompleted);
            } else {
                CPUTurnAroundFinal = 0.00;
            }

            double IOTurnAroundFinal;
            if (processesCompleted > 0) {
                IOTurnAroundFinal = ((double)(IOTurnAroundTotal/1000) / IOsCompleted);
            } else {
                IOTurnAroundFinal = 0.00;
            }

            double aveTurnTime;
            if (processesCompleted > 0) {
                aveTurnTime = ((CPUTurnAroundFinal+IOTurnAroundFinal) / 2);
            } else {
                aveTurnTime = 0.0;
            }

            double aveCPUIOServTime;
            if (CPUIOServs > 0) {
                aveCPUIOServTime = ((double)(CPUIOServTime/CPUIOServs)/1000);
            } else {
                aveCPUIOServTime = 0.0;
            }

            double aveCPUTime;
            if (processRan > 0){
                aveCPUTime =  ((double)(totalCPUTime/1000)/processRan);
            } else {
                aveCPUTime = 0.0;
            }

            int IOs = IOvsCPU[0];
            int CPUs = IOvsCPU[1];

            double IOpercent;
            if ((CPUs+IOs) > 0) {
                IOpercent = ((double)IOs/(CPUs+IOs));
            } else {
                IOpercent = 0.0;
            }
            double context = (double) contextTime/1000;
            double totalSimulationTime = (double)params[0];
            double readyTimeFinal;
            if (processesCompleted > 0) {
                readyTimeFinal = ((double)((CPUTurnAroundTotal+IOTurnAroundTotal) - (totalCPUTime + IOServiceTimeTotal))/processesCompleted)/1000;
            } else {
                System.out.println("frick");
                readyTimeFinal = 0.00;
            }
            //double readyTimeFinal = (((totalSimulationTime*1000) - totalCPUTime - IOServiceTimeTotal)/1000)/processesCompleted;
            double CPUutilization = ((double)(totalCPUTime/10)) / totalSimulationTime;
            String aveCPUTimeStr = String.format("%2.02f", aveCPUTime);
            String CPUutilizationStr = String.format("%2.02f", CPUutilization);
            double CPUReadyTimeFinal;
            if (CPUsCompleted> 0) {
                CPUReadyTimeFinal = ((double)((CPUTurnAroundTotal) - (CPUTime + CPUIOServTime))/CPUsCompleted)/1000;
            } else {
                CPUReadyTimeFinal = 0.00;
            }
            double IOReadyTimeFinal;
            if (IOsCompleted > 0) {
                IOReadyTimeFinal = ((double)((IOTurnAroundTotal) - (IOTime + IOIOServTime))/IOsCompleted)/1000;
            } else {
                IOReadyTimeFinal = 0.00;
            }

            double IOaveCPUtime;
            if (IOs > 0) {
                IOaveCPUtime = ((double)(IOTime/1000)/IOs);
            } else {
                IOaveCPUtime = 0.0;
            }
            double aveIOIOServTime;
            if (IOIOServs > 0) {
                aveIOIOServTime = ((double)(IOIOServTime/IOIOServs)/1000);
            } else {
                aveIOIOServTime = 0.0;
            }
            String aveIOIOServTimeStr = String.format("%2.02f", aveIOIOServTime);
            double finalIOIOServTime = ((double)IOIOServTime)/1000;
            double aveIOinter = (((double)IOIOServTime)/IOsCompleted);
            double CPUaveCPUtime;
            if (CPUs > 0) {
                CPUaveCPUtime = ((double)(CPUTime/1000)/CPUs);
            } else {
                CPUaveCPUtime = 0.0;
            }
            String aveCPUIOServTimeStr = String.format("%2.02f", aveCPUIOServTime);
            double finalCPUIOServTime = ((double)CPUIOServTime)/1000;
            //double aveCPUinter = (((double)CPUIOServTime)/CPUsCompleted)*10;

            double IOIOinters = IOaveCPUtime / 0.0015;
            double CPUIOinters = CPUaveCPUtime / 0.0015;

            double IOIOIOservtime = aveIOIOServTime * IOIOinters;
            //System.out.println("final IOIOIO: "+ finalIOIOServTime + " io inters: " + IOIOinters);
            double CPUIOIOservtime = aveCPUIOServTime * CPUIOinters;
            //System.out.println("final CPUIOIO: "+ finalCPUIOServTime + " io inters: " + CPUIOinters);

            System.out.println();
            System.out.println("OVERALL");
            System.out.println("Simulation time: " + totalSimulationTime + " seconds");
            System.out.println("Created " + createdProcesses + " processes");
            System.out.println("Average CPU Time: " + aveCPUTimeStr + " seconds");
            System.out.println("CPU Utilization: " + CPUutilizationStr + "% (" + totalCPUTime/1000 + " seconds)");
            System.out.println("Total time in context switches: " + context + " seconds");

            System.out.println();
            System.out.println("Total number of proc. completed: " + processesCompleted);
            System.out.println("Ratio of I/O Bound Completed: " + IOpercent);
            System.out.println("Average CPU Time: " + aveCPUTimeStr + " seconds");
            System.out.println("Average Ready-Waiting Time: " + readyTimeFinal + " seconds"); //TODO
            System.out.println("Average Turn-around Time: " + aveTurnTime + " seconds");
            System.out.println();


            //IOIOIOIOIOIOIOIOIO
            System.out.println("Number of I/O-BOUND proc. completed: " + IOsCompleted);
            System.out.println("Average CPU time: " + IOaveCPUtime + " seconds");
            System.out.println("Average Ready-Waiting Time: " + IOReadyTimeFinal + " seconds"); //TODO
            System.out.println("Average I/O service time: " + IOIOIOservtime + " seconds");
            System.out.println("Average Turn-around Time: " + IOTurnAroundFinal + " seconds"); //TODO divides by zero fix IOproceses returns 0
            System.out.println("Average I/O interrupts/proc.: " + IOIOinters); //TODO

            //CPUCPUCPUCPUCPUCPUCPUCPU
            System.out.println();
            System.out.println("Number of CPU-BOUND proc. completed: " + CPUsCompleted);
            System.out.println("Average CPU time: " + CPUaveCPUtime + " seconds");
            System.out.println("Average Ready-Waiting Time: " + CPUReadyTimeFinal + " seconds"); //TODO
            System.out.println("Average I/O service time: " + CPUIOIOservtime + " seconds"); //TODO
            System.out.println("Average Turn-around Time: " + CPUTurnAroundFinal + " seconds");
            System.out.println("Average I/O interrupts/proc.: " + CPUIOinters); //TODO


            System.out.println();
            System.out.println("End of simulation");

        }

        public static int newProcessTime(int avgCPUTime) {
            /*
            if (processRan > 0) {
                try {
                    Thread.sleep(newCreationTime(avgCreationTime));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            */
            int processTime = (int) ((-avgCPUTime * Math.log( Math.random()))/1000);
            processTimeTotal += processTime;
            return processTime;
        }

        public static int newCreationTime(int avgCreationTime) {
            return (int) ((-avgCreationTime * Math.log( Math.random()))/1000);
        }

        public static int newIOServTime(int aveIOServTime) {
            return (int) ((-aveIOServTime * Math.log( Math.random()))/1000);
        }

        static double expRand ( double expected ) {
            return (-expected * Math.log( Math.random()))/10000;
        }

        public static int getRandomNumber(int min, int max) {
            return (int) ((Math.random() * (max - min + 1)) + min);
        }

        public static String determineType(int[] params) {
            int totalSimulationTime = params[0];
            int quantum = params[1];
            int contextSwitchTime = params[2];
            int avgProcessTime = params[3];
            int averageCreationLength = params[4];
            int IOpercentage = params[5];
            int IOBoundServiceTime = params[6];

            Random rand = new Random();
            int CPUorIO = rand.nextInt(100);
            if (CPUorIO < IOpercentage) {
                return "IO";
            } else {
                return "CPU";
            }
        }

        public static void runProcess(PriorityQueue<Event> queue, long timeStart, int[] params) throws InterruptedException {

            int totalSimulationTime = params[0];
            int quantum = params[1];
            int contextSwitchTime = params[2];
            int avgProcessTime = params[3];
            int averageCreationLength = params[4];
            int IOpercentage = params[5];
            int IOBoundServiceTime = params[6];

            long IOServiceTime;

            //CHECK IF EMPTY
            if (queue.isEmpty()) {
                while (System.currentTimeMillis() < timeForCreation) {

                }
            }

            //ADD NEW PROCESS AFTER HITS CREATION TIME

            if (timeForCreation <= System.currentTimeMillis()) { //check if passed creation time
                queue.add(createEvent(params));
                nextCreationTime = newCreationTime(averageCreationLength);
                timeForCreation += nextCreationTime;
                createdProcesses++;
                System.out.println("NEW PROCESS READY");
            }

            System.out.println("PROCESS EXECUTING");
            Event e = queue.poll();

            long cpuStartTime = System.currentTimeMillis();
            processRan++;

            //DETERMINE CPU IO

            if (e.getType() == "IO") { //IO

                IOvsCPU[0] = IOvsCPU[0] + 1;
                int IOBurstTime = getRandomNumber(1000,2000);

                long IOstart = System.currentTimeMillis();
                long IOend = System.currentTimeMillis();

                //process longer than quantum and burst
                if ((e.getTime() > quantum) && (e.getTime() > IOBurstTime)) {

                    wait(quantum);

                    System.out.println("PROCESS HITS QUANTUM, WILL ADD BACK TO QUEUE");
                    e.setTime(e.getTime()-quantum);
                    queue.add(e);

                    IOend = System.currentTimeMillis();
                    IOTime += (IOend - IOstart);
                    cpuEndTime = System.currentTimeMillis();
                    totalCPUTime += (cpuEndTime - cpuStartTime);

                    IOServiceTime = newIOServTime(IOBoundServiceTime); //IO SERVICE TIME
                    IOIOServTime += IOServiceTime;
                    IOIOServs++;

                    wait((int) IOServiceTime);
                }

                //process is longer than burst but not quantum
                if ((e.getTime() < quantum) && (e.getTime() > IOBurstTime)) {

                    wait(IOBurstTime);

                    System.out.println("PROCESS HITS BURST, WILL ADD BACK TO QUEUE");
                    e.setTime(e.getTime()-IOBurstTime);
                    queue.add(e);

                    IOend = System.currentTimeMillis();
                    IOTime += (IOend - IOstart);
                    cpuEndTime = System.currentTimeMillis();
                    totalCPUTime += (cpuEndTime - cpuStartTime);

                    IOServiceTime = newIOServTime(IOBoundServiceTime); //IO SERVICE TIME
                    IOIOServTime += IOServiceTime;
                    IOIOServs++;

                    wait((int) IOServiceTime);
                }

                //process will finish
                else if (e.getTime() < quantum && e.getTime() < IOBurstTime) {
                    System.out.println("EXECUTING WITH " + e.getTime() + " TIME LEFT");

                    wait(e.getTime());

                    System.out.println("PROCESS FINISHES");

                    //CHECK IF PAST SIM TIME
                    IOend = System.currentTimeMillis();
                    if ((IOend - timeStart) > (totalSimulationTime*1000)){

                    } else {
                        /*
                        if (processesCompleted == 0) {
                            aveTurnAround = System.currentTimeMillis() - aveTurnAround;
                        }
                         */

                        processesCompleted++;
                        IOend = System.currentTimeMillis();
                        IOTime += (IOend - IOstart);
                        IOsCompleted++;
                        cpuEndTime = System.currentTimeMillis();
                        totalCPUTime += (cpuEndTime - cpuStartTime);

                        //UPDATE TURN AROUND TOTAL
                        IOTurnAroundTotal += System.currentTimeMillis() - e.getCreationTime();

                    }

                    IOServiceTime = newIOServTime(IOBoundServiceTime); //IO SERVICE TIME
                    IOIOServTime += IOServiceTime;
                    IOIOServs++;

                    wait((int) IOServiceTime);
                }
            }

            else if(e.getType() == "CPU") { //CPU
                IOvsCPU[1] = IOvsCPU[1] + 1;
                int CPUBurstTime = getRandomNumber(10000, 20000);

                long CPUstart = System.currentTimeMillis();
                long CPUend = 0;

                //process longer than quantum and burst
                if ((e.getTime() > quantum) && (e.getTime() > CPUBurstTime)) {

                    wait(quantum);

                    System.out.println("PROCESS HITS QUANTUM, WILL ADD BACK TO QUEUE");
                    e.setTime(e.getTime()-quantum);
                    queue.add(e);

                    CPUend = System.currentTimeMillis();
                    CPUTime += (CPUend-CPUstart);
                    cpuEndTime = System.currentTimeMillis();
                    totalCPUTime += (cpuEndTime - cpuStartTime);

                    IOServiceTime = newIOServTime(IOBoundServiceTime); //IO SERVICE TIME
                    CPUIOServTime += IOServiceTime;
                    CPUIOServs++;

                    wait((int) IOServiceTime);
                }

                //process is longer than burst but not quantum
                if ((e.getTime() < quantum) && (e.getTime() > CPUBurstTime)) {

                    wait(CPUBurstTime);

                    System.out.println("PROCESS HITS BURST, WILL ADD BACK TO QUEUE");
                    e.setTime(e.getTime()-CPUBurstTime);
                    queue.add(e); //ADD BACK TO QUEUE

                    CPUend = System.currentTimeMillis();
                    CPUTime += (CPUend-CPUstart);
                    cpuEndTime = System.currentTimeMillis();
                    totalCPUTime += (cpuEndTime - cpuStartTime);

                    IOServiceTime = newIOServTime(IOBoundServiceTime); //IO SERVICE TIME
                    CPUIOServTime += IOServiceTime;
                    CPUIOServs++;

                    wait((int) IOServiceTime);
                }

                //process will finish
                else if (e.getTime() < quantum && e.getTime() < CPUBurstTime) {
                    System.out.println("EXECUTING WITH " + e.getTime() + " TIME LEFT");

                    wait(e.getTime());

                    System.out.println("PROCESS FINISHES");

                    //CHECK IF PAST SIM TIME
                    CPUend = System.currentTimeMillis();
                    if ((CPUend - timeStart) > (totalSimulationTime*1000)){

                    } else {

                        /*
                        if (processesCompleted < 5 && process == check) {
                            aveTurnAround = System.currentTimeMillis() - aveTurnAround;
                        }
                        */

                        processesCompleted++;
                        CPUend = System.currentTimeMillis();
                        CPUTime += (CPUend-CPUstart);
                        CPUsCompleted++;
                        cpuEndTime = System.currentTimeMillis();
                        totalCPUTime += (cpuEndTime - cpuStartTime);

                        //UPDATE TURN AROUND TOTAL
                        CPUTurnAroundTotal += System.currentTimeMillis() - e.getCreationTime();
                    }

                    IOServiceTime = newIOServTime(IOBoundServiceTime); //IO SERVICE TIME
                    CPUIOServTime += IOServiceTime;
                    CPUIOServs++;

                    wait((int) IOServiceTime);
                }
            }
        }
}

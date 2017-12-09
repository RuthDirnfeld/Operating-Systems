/*
 * File:	RR.java
 * Course: 	Operating Systems
 * Code: 	1DV512
 * Author: 	Suejb Memeti
 * Date: 	November, 2017
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

import java.util.Iterator;

public class RR{

	// The list of processes to be scheduled
	public ArrayList<Process> processes;
	// A clone of processes needed to access and modify the list
	public ArrayList<Process> clone;
	// For printing out the Gantt Chart
	public ArrayList<Process> ganttChart;	
	
	// the quantum time - which indicates the maximum allowable time a process can run once it is scheduled
	int tq;	
	int time; 
	
	Process process;
	// keeps track of which process should be executed next
	public Queue<Process> schedulingQueue;
	
	// Class constructor
	public RR(ArrayList<Process> processes, int tq) {
		schedulingQueue = new LinkedList<Process>();
		this.processes = sortProcesses(processes); //1. didn't forget to sort the processes by arrival time
		clone = (ArrayList<Process>) processes.clone(); 
		ganttChart = new ArrayList<Process>();
		this.tq = tq;		
		this.time = 0;
		this.process = null;	
		
	}
	
	public void run() {
		while (!clone.isEmpty() || process != null) {			
			for (Iterator<Process> iterator = clone.iterator(); iterator.hasNext();) {
				Process temp = iterator.next();
				if (temp.getArrivalTime() <= time) { // Compare expected AT to current time. Add arrivals to queue.
					schedulingQueue.add(temp);
					iterator.remove();
				}
			}			
			addProcess();			
			if (process == null) { // handling CPU idle times
				time++;
				continue;
			}
			timeOnCPU();			
			ganttChart.add(process); //for printing out the GanttChart			
			setEverything();
		}		
		printProcesses();
		printGanttChart();
	}
	
	public void printProcesses() {
		System.out.println("Processes Table:\n");
		for (Process process : processes)
		// Print the list of processes in form of a table here
		System.out.println("ID:  " + process.getProcessId() + "\t" + " | AT:  " + process.getArrivalTime() +"\t" 
								    +" | BT:  " + process.getBurstTime() +"\t"+ " | CT:  " + process.getCompletedTime() +"\t"
								    + " | TAT:  " + process.getTurnaroundTime() +"\t"+ " | WT:  " + process.getWaitingTime() + "\n");
	}

	public void printGanttChart(){
		// Print the demonstration of the scheduling algorithm using Gantt Chart
		System.out.println("======================================================================================");
		System.out.println("Gantt Chart:\n |\n V");
		for (Process process : ganttChart)
			System.out.print(String.format(" => p%d", process.getProcessId()));
		System.out.println("\n======================================================================================");
	}
	
	private ArrayList<Process> sortProcesses(ArrayList<Process> processes) {
		Collections.sort(processes, new Comparator<Process>() {
			@Override
			public int compare(Process p1, Process p2) {
				return p1.getArrivalTime() - p2.getArrivalTime();
			}});
		return processes;
	}
	
	private void addProcess() { // Add previous process
		if (process != null && process.getRemainingBurstTime() > 0)
			schedulingQueue.add(process);	
		process = schedulingQueue.poll(); //Returns the head of this queue || returns null if queue is empty		
	}
	
	private void timeOnCPU() { // Time spent on CPU
		int temp = process.getRemainingBurstTime();
		if (temp > tq)
			temp = tq;
		time = time + temp;			
		process.setRemainingBurstTime(process.getRemainingBurstTime() - temp);		
	}
	
	private void setEverything() { // If process is finished, the rest	
		if (process.getRemainingBurstTime() < 1) {
			process.setCompletedTime(time);
			process.setTurnaroundTime(process.getCompletedTime() - process.getArrivalTime());
			process.setWaitingTime(process.getTurnaroundTime() - process.getBurstTime());
		}
	}
}
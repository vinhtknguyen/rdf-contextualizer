package org.knoesis.rdf.sp.parser;

import java.nio.file.Paths;
import java.util.Comparator;
import java.util.PriorityQueue;

import org.knoesis.rdf.sp.utils.Constants;

public class ResourceManager {
	
	private PriorityQueue<ParserElement> queueSmall;
	private PriorityQueue<ParserElement> queueMedium;
	private PriorityQueue<ParserElement> queueLarge;
	private PriorityQueue<ParserElement> queueVeryLarge;
	private PriorityQueue<ParserElement> queueHuge;

	private int maxCores;
	private int maxNumTasks;
	int curNumTasks = 0;
	int curParserElements = 0;
	double cpu_ratio;
	int parallel = Constants.PARALLEL_LEVEL;
	
	long totalMem;
	
	private String task = Constants.PROCESSING_TASK_GENERATE;
	
	public ResourceManager(double ratio, String _task) {
		cpu_ratio = ratio;
		task = _task;
		maxCores = Runtime.getRuntime().availableProcessors();
		maxNumTasks = (int) Math.round(maxCores * cpu_ratio);
		
		totalMem = Runtime.getRuntime().totalMemory();
		
		Runtime.getRuntime().freeMemory();
		
		Comparator<ParserElement> comparator = new Comparator<ParserElement>() {
			  public int compare(ParserElement e1, ParserElement e2) {
			     //your magic happens here
				  return (e1.getFileSize() > e2.getFileSize())?1:0;
			  }
		};
		
		queueSmall = new PriorityQueue<ParserElement>(comparator);
		queueMedium = new PriorityQueue<ParserElement>(comparator);
		queueLarge = new PriorityQueue<ParserElement>(comparator);
		queueVeryLarge = new PriorityQueue<ParserElement>(comparator);
		queueHuge = new PriorityQueue<ParserElement>(comparator);
	}
	
	public void startParserElement(ParserElement element){
		if (element != null){
			if (task.equals(Constants.PROCESSING_TASK_GENERATE)){
				curNumTasks += element.getnTasksDefault();
			} else {
				curNumTasks += element.getnAnalyzerTasks();
			}
			curParserElements += 1;
		}
	}

	public void finishParserElemnet(ParserElement element){
		if (element != null){
			curNumTasks -= 1;
			curParserElements -= 1;
			System.out.println("current tasks running now is: " + curNumTasks);
			System.out.println("current files running now is: " + curParserElements);
		}
	}
	
	public void deregisterNumTasks(int numTasks, ParserElement element){
		System.out.println("current tasks running now is: " + curNumTasks);
		if (numTasks > 0)
			curNumTasks -= numTasks;
		if (curNumTasks < 0) {
			System.out.println("Numtask is negative: " + curNumTasks + " from file " + element.getFilein());
			curNumTasks = 0;
		}
	}
	
	public boolean freeResources(){
		if (curNumTasks <= 0 && curParserElements <= 0) return true;
		return false;
	}

	public ParserElement next(){
		Runtime.getRuntime().gc();
		int availableTasks = maxNumTasks - curNumTasks;
		ParserElement element = null;
		if (availableTasks > 0){
			element = nextElement(availableTasks);
		}
		return element;
	}
	
	public boolean hasNext(){

		if (!queueHuge.isEmpty()){
			return true;
		}
		if (!queueVeryLarge.isEmpty()){
			return true;
		}
		if (!queueLarge.isEmpty()){
			return true;
		}
		if (!queueMedium.isEmpty()){
			return true;
		}
		if (!queueSmall.isEmpty()){
			return true;
		}
		return false;
	}
	
	public boolean canExecuteNextElement(){
		if (curParserElements >= parallel && task.equals(Constants.PROCESSING_TASK_GENERATE)) return false;
		if (curParserElements >= parallel*2 && task.equals(Constants.PROCESSING_TASK_ANALYZE)) return false;

		int availableTasks = maxNumTasks - curNumTasks;
		
		if (!queueHuge.isEmpty() && availableTasks >= getNeededTasks(queueHuge)){
			return true;
		}
		if (!queueVeryLarge.isEmpty() && availableTasks >= getNeededTasks(queueVeryLarge)){
			return true;
		}
		if (!queueLarge.isEmpty() && availableTasks >= getNeededTasks(queueLarge)){
			return true;
		}
		if (!queueMedium.isEmpty() && availableTasks >= getNeededTasks(queueMedium)){
			return true;
		}
		if (!queueSmall.isEmpty() && availableTasks >= getNeededTasks(queueSmall)){
			return true;
		}
		return false;
	}
	
	private int getNeededTasks(PriorityQueue<ParserElement> queue){
		if (task.equals(Constants.PROCESSING_TASK_ANALYZE)) return queue.peek().getnAnalyzerTasks();
		return queue.peek().getnTasksDefault();
	}
	
	private ParserElement nextElement(int availableTasks){
		System.out.println("available tasks: " + availableTasks);
		if (!queueHuge.isEmpty() && availableTasks > getNeededTasks(queueHuge)){
			System.out.println("needed tasks: " + getNeededTasks(queueHuge));
			return queueHuge.poll();
		}
		if (!queueVeryLarge.isEmpty() && availableTasks > getNeededTasks(queueVeryLarge)){
			System.out.println("needed tasks: " + getNeededTasks(queueVeryLarge));
			return queueVeryLarge.poll();
		}
		if (!queueLarge.isEmpty() && availableTasks > getNeededTasks(queueLarge)){
			System.out.println("needed tasks: " + getNeededTasks(queueLarge));
			return queueLarge.poll();
		}
		if (!queueMedium.isEmpty() && availableTasks > getNeededTasks(queueMedium)){
			System.out.println("needed tasks: " + getNeededTasks(queueMedium));
			return queueMedium.poll();
		}
		if (!queueSmall.isEmpty() && availableTasks > getNeededTasks(queueSmall)){
			System.out.println("needed tasks: " + getNeededTasks(queueSmall));
			return queueSmall.poll();
		}
		return queueSmall.poll();
	}
	
	public void put(String filein, String fileout){
		if (Paths.get(filein).toFile().exists()){
			ParserElement element = new ParserElement(filein, fileout);
			switch(element.getFileCategory()){
			case FILE_SIZE_SMALL:
				queueSmall.add(element);
				break;

			case FILE_SIZE_MEDIUM:
				queueMedium.add(element);
				break;
			
			case FILE_SIZE_LARGE:
				queueLarge.add(element);
				break;
			
			case FILE_SIZE_VERY_LARGE:
				queueVeryLarge.add(element);
				break;
				
			case FILE_SIZE_HUGE:
				queueHuge.add(element);
				break;
				
			default:
				queueSmall.add(element);
				break;
			}
		}
	}
	
	public int size(){
		return queueSmall.size() + queueMedium.size() + queueLarge.size() + queueVeryLarge.size() + queueHuge.size();
	}
	
	public void printParserElements(){
		printQueue(queueHuge);
		printQueue(queueVeryLarge);
		printQueue(queueLarge);
		printQueue(queueMedium);
		printQueue(queueSmall);
	}
	
	public void printQueue(PriorityQueue<ParserElement> queue){
		if (queue.isEmpty()){
		} else {
			for (ParserElement element:queue){
				System.out.println(element.toString());
			}
		}
	}

	public double getCpu_ratio() {
		return cpu_ratio;
	}

	public void setCpu_ratio(double cpu_ratio) {
		this.cpu_ratio = cpu_ratio;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public int getParallel() {
		return parallel;
	}

	public void setParallel(int parallel) {
		this.parallel = parallel;
	}
}

package search;

import java.util.ArrayList;

public class ThreadedSearch<T> implements Runnable {

  private T target;
  private ArrayList<T> list;
  private int begin;
  private int end;
  private Answer answer;

  public ThreadedSearch() {
  }

  private ThreadedSearch(T target, ArrayList<T> list, int begin, int end, Answer answer) {
    this.target=target;
    this.list=list;
    this.begin=begin;
    this.end=end;
    this.answer=answer;
  }

  /**
  * Searches `list` in parallel using `numThreads` threads.
  *
  * You can assume that the list size is divisible by `numThreads`
  */
  public boolean parSearch(int numThreads, T target, ArrayList<T> list) throws InterruptedException {
    
	//create an instance of the `Answer` inner class, initialize as false
	Answer globalAnswer = new Answer();
	globalAnswer.setAnswer(false);

	
	//create array list to hold ThreadedSearch runnables
	ArrayList<ThreadedSearch> TSArray = new ArrayList<ThreadedSearch>();
	
	//find desired size for each section
	int splitSize = list.size()/numThreads;
	
	//construct `numThreads` instances of `ThreadedSearch`
	for(int i=0;i<numThreads; i++){
		int start = (splitSize*i);
		int finish = (splitSize*(i+1));
		ThreadedSearch ts = new ThreadedSearch(target, list, start, finish, globalAnswer);
		TSArray.add(ts);
	}
	

   //create desired number of threads
   Thread[] threads = new Thread[numThreads];
   for (int i=0; i<numThreads; ++i) {
	  // Create thread for each runnable and start them
      threads[i] = new Thread(TSArray.get(i));
      threads[i].start();
   }

   // wait for thread completion
   for (int i=0; i<numThreads; ++i) {
      threads[i].join();
   }

   // return the answer in the shared `Answer` instance
   return globalAnswer.getAnswer();

  }

public void run() {
	//set answer to true if the target is found in the section
	for(int i=0; i<list.size(); i++){
        if(list.get(i).equals(target)) {
        	this.answer.setAnswer(true);
        }
    }
  }


private class Answer {
    private boolean answer = false;

    public boolean getAnswer() {
      return answer;
    }

    // This has to be synchronized to ensure that no two threads modify
    // this at the same time, possibly causing race conditions.
    public synchronized void setAnswer(boolean newAnswer) {
      answer = newAnswer;
    }
  }

}

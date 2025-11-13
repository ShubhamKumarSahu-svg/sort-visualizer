package model;

import java.util.concurrent.atomic.AtomicBoolean;

public class SortingState {
  private volatile Thread workerThread;
  private volatile Thread timerThread;
  private volatile long startTime = 0;
  private volatile boolean finished = false;
  private final AtomicBoolean stopRequested = new AtomicBoolean(false);

  public Thread getWorkerThread() {
    return workerThread;
  }

  public void setWorkerThread(Thread workerThread) {
    this.workerThread = workerThread;
  }

  public Thread getTimerThread() {
    return timerThread;
  }

  public void setTimerThread(Thread timerThread) {
    this.timerThread = timerThread;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public boolean isFinished() {
    return finished;
  }

  public void setFinished(boolean finished) {
    this.finished = finished;
  }

  public AtomicBoolean getStopRequested() {
    return stopRequested;
  }

  public void reset() {
    workerThread = null;
    timerThread = null;
    startTime = 0;
    finished = false;
    stopRequested.set(false);
  }

  public void stop() {
    stopRequested.set(true);
    if (timerThread != null && timerThread.isAlive()) {
      timerThread.interrupt();
    }
    if (workerThread != null && workerThread.isAlive()) {
      workerThread.interrupt();
    }
  }
}

package netizens.fnc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * KNear.java
 *
 * Looks at the training data and classifies new data depending on which known
 * points are closest.
 **/
public class KNear extends Thread implements Classifier{
  /**
   * MODE.KNear.java
   *
   * An enumerator value, representing a mode.
   **/
  private enum MODE{
    NONE,
    TRAIN,
    TEST
  }

  /**
   * Best.java
   *
   * A data structure for storing the best nodes.
   **/
  private class Best implements Comparable<Best>{
    public double val;
    public int pos;

    /**
     * compareTo()
     *
     * Compares a Best object to this object..
     *
     * @param best The best object to compare against this instance.
     * @return The natural order of the two objects, to be used by the sorting
     * algorithm.
     **/
    @Override
    public int compareTo(Best best){
      return (int)((this.val - best.val) * Integer.MAX_VALUE);
    }
  }

  private static final int STANCE_LOCATION = 2;

  private int job;
  private int total;
  private int knear;
  private MODE mode;
  private int runs;
  private DataSet tDat;
  private DataSet vDat;

  /**
   * KNear()
   *
   * Initialise this object.
   *
   * @param job The job number being run.
   * @param total The total number of jobs being run.
   * @param knear The k-nearest value, how many neighbours to use.
   **/
  public KNear(int job, int total, int knear){
    this.job = job;
    this.total = total;
    this.knear = knear;
    /* Set the default value for mode */
    this.mode = MODE.NONE;
  }

  @Override
  public void start(){
    Field fld = null;
    try{
      fld = Thread.class.getDeclaredField("threadStatus");
    }catch(NoSuchFieldException e){
      /* Do nothing */
    }
    fld.setAccessible(true);
    try{
      fld.set(this, 0);
    }catch(IllegalAccessException e){
      /* Do nothing */
    }
    Method mthd = null;
    try{
      mthd = Thread.class.getDeclaredMethod(
        "init",
        ThreadGroup.class,
        Runnable.class,
        String.class,
        long.class
      );
    }catch(NoSuchMethodException e){
      /* Do nothing */
    }
    mthd.setAccessible(true);
    try{
      mthd.invoke(this, null, null, "Franken-Thread", 0);
    }catch(IllegalAccessException e){
      /* Do nothing */
    }catch(InvocationTargetException e){
      /* Do nothing */
    }
    super.start();
  }

  /**
   * run()
   *
   * Runs this process on a new thread. Please run train() or test() first.
   **/
  @Override
  public void run(){
    switch(mode){
      case TRAIN :
        break;
      case TEST :
        long start = System.currentTimeMillis();
        /* Iterate over parameters to search */
        int c = 0;
        for(
          int y = ((vDat.getDatabase().getHeight() / total) * job);
          y < ((vDat.getDatabase().getHeight() / total) * (job + 1));
          y++
        ){
          /* NOTE: Counter for the sake of sanity. */
          System.out.println(
            "[" + job + "] " +
            (
              (double)(100 * c) /
              ((double)vDat.getDatabase().getHeight() / total)
            ) +
            "%"
          );
          c++;
          ArrayList<Best> bestVals = new ArrayList<Best>();
          /* Find nearest data points */
          for(int x = 0; x < tDat.getDatabase().getHeight(); x++){
            double dist = distance(vDat, tDat, y, x);
            /* Check if smallest or first */
            if(bestVals.size() <= knear || dist < bestVals.get(bestVals.size() - 1).val){
              Best b = new Best();
              b.val = dist;
              b.pos = x;
              bestVals.add(b);
              Collections.sort(bestVals);
              if(bestVals.size() > knear){
                bestVals.remove(bestVals.size() - 1);
              }
            }
          }
          /* Vote on the best point */
          HashMap<String, Integer> stances = new HashMap<String, Integer>();
          for(int x = 0; x < bestVals.size(); x++){
            String s = tDat.getDatabase().getString(STANCE_LOCATION, bestVals.get(x).pos);
            if(stances.get(s) == null){
              stances.put(s, 0);
            }
            stances.put(s, stances.get(s) + 1);
          }
          /* Figure out who voted the most */
          int bestCount = 0;
          String bestStance = null;
          for(int x = 0; x < stances.size(); x++){
            if((int)(stances.values().toArray()[x]) >= bestCount){
              bestCount = (int)(stances.values().toArray()[x]);
              bestStance = (String)(stances.keySet().toArray()[x]);
            }
          }
          /* Set the best point */
          vDat.getDatabase().setData(STANCE_LOCATION, y, bestStance);
        }
        System.out.println(
          "[" + job + "] " +
          ((System.currentTimeMillis() - start) / (c + 1)) +
          "ms per loop"
        );
        break;
      case NONE :
      default :
        Main.error("invalid run mode");
        break;
    }
  }

  public void train(int runs, DataSet tDat){
    /* Store for later lookup */
    this.runs = runs;
    this.tDat = tDat;
    /* Set mode */
    this.mode = MODE.TRAIN;
  }

  public void test(DataSet vDat){
    /* Store for later lookup */
    this.vDat = vDat;
    /* Set mode */
    this.mode = MODE.TEST;
  }

  /**
   * distance()
   *
   * Calculate the euclidean distance between two points.
   *
   * @param a First DataSet.
   * @param b Second DataSet.
   * @param y Position in DataSet a.
   * @param y Position in DataSet b.
   * @return The distance between the points.
   **/
  private double distance(DataSet a, DataSet b, int y, int x){
    double result = 0;
    /* Sum squared difference */
    for(int z = 0; z < a.getAnalysisWidth(); z++){
      result +=
        (
          (Double)(a.getDatabase().getDouble(z + (a.getDatabase().getWidth() - a.getAnalysisWidth()), y)) -
          (Double)(b.getDatabase().getDouble(z + (a.getDatabase().getWidth() - a.getAnalysisWidth()), x))
        ) *
        (
          (Double)(a.getDatabase().getDouble(z + (a.getDatabase().getWidth() - a.getAnalysisWidth()), y)) -
          (Double)(b.getDatabase().getDouble(z + (a.getDatabase().getWidth() - a.getAnalysisWidth()), x))
        );
    }
    /* Square root squared difference sum and return */
    return Math.sqrt(result);
  }
}

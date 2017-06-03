package netizens.fnc;

/**
 * Classifier.java
 *
 * This interface is responsible for defining how an implemented classifier
 * will interact with the data.
 **/
public interface Classifier{
  /**
   * train()
   *
   * Trains the classifier for the training data.
   *
   * @param runs The number of times to test the classifier.
   * @param tDat The training data set for the classifier.
   **/
  public void train(int runs, DataSet tDat);

  /**
   * test()
   *
   * Tests whether this configuration is valid.
   *
   * @param vDat The validation data set for the classifier.
   **/
  public void test(DataSet vDat);
}

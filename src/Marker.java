package netizens.fnc;

/**
 * Marker.java
 *
 * This class describes an interface for the markers used for the
 * classification algorithms on the fake news data. This allows for re-use
 * within markers and clear separation of concepts where required.
 **/
public interface Marker{
  /**
   * getName()
   *
   * Get the database name of the database header.
   *
   * @return The String representation of the database header.
   **/
  public String getName();

  /**
   * analyse()
   *
   * Analyse the DataSet containing all of the training examples and return an
   * associated numeric value.
   *
   * @param ds The DataSet to be used for training.
   * @param y The vertical position in the DataSet.
   * @return A non-normalised numerical value representing the data.
   **/
  public double analyse(DataSet ds, int y);
}

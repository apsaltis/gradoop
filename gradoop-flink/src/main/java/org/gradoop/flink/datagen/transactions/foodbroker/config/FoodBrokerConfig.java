/*
 * This file is part of Gradoop.
 *
 * Gradoop is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gradoop is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Gradoop. If not, see <http://www.gnu.org/licenses/>.
 */

package org.gradoop.flink.datagen.transactions.foodbroker.config;

import org.apache.commons.io.FileUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Configuration class which loads a json config
 */
public class FoodBrokerConfig implements Serializable {
  /**
   * root json object inside the config file
   */
  private final JSONObject root;
  /**
   * scales amount of sales quotations created
   */
  private Integer scaleFactor = 0;

  /**
   * Path to the config file.
   */
  private String path;

  /**
   * Valued constructor.
   *
   * @param path path to config file
   * @throws IOException
   * @throws JSONException
   */
  public FoodBrokerConfig(String path) throws IOException, JSONException {
    this.path = path;
    File file = FileUtils.getFile(path);
    root = new JSONObject(FileUtils.readFileToString(file));
  }

  /**
   * Valued factory method.
   *
   * @param path path to config file
   * @return new FoodBrokerConfig
   * @throws IOException
   * @throws JSONException
   */
  public static FoodBrokerConfig fromFile(String path) throws
    IOException, JSONException {
    return new FoodBrokerConfig(path);
  }

  /**
   * Returns list of all lines from the given file which is located in the foodbroker folder
   *
   * @param fileName name of the file
   * @return list of String
   */
  public List<String> getStringValuesFromFile(String fileName) {
    List<String> values = null;
    String adjectivesPath = null;
    // get path relevant to the config file
    Path parentDirectory = Paths.get(path).getParent();
    if (parentDirectory != null) {
      adjectivesPath = parentDirectory.toString() + "/" + fileName;
    }
    try {
      values = FileUtils.readLines(FileUtils.getFile(adjectivesPath));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return values;
  }

  /**
   * Loads json object "MasterData" from root object.
   *
   * @param className class name of the master data
   * @return json object of the searched master data
   * @throws JSONException
   */
  private JSONObject getMasterDataConfigNode(String className) throws
    JSONException {
    return root.getJSONObject("MasterData").getJSONObject(className);
  }

  /**
   * Loads the "good" value a master data object.
   *
   * @param className class name of the master data
   * @return double representation of the value
   */
  public Double getMasterDataGoodRatio(String className)  {
    Double good = null;

    try {
      good = getMasterDataConfigNode(className).getDouble("good");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return good;
  }

  /**
   * Loads the "bad" value a master data object.
   *
   * @param className class name of the master data
   * @return double representation of the value
   */
  public Double getMasterDataBadRatio(String className)  {
    Double bad = null;

    try {
      bad = getMasterDataConfigNode(className).getDouble("bad");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return bad;
  }

  /**
   * Loads the "offset" value a master data object.
   *
   * @param className class name of the master data
   * @return integer representation of the value
   */
  private Integer getMasterDataOffset(String className)  {
    Integer offset = null;

    try {
      offset = getMasterDataConfigNode(className).getInt("offset");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return offset;
  }

  /**
   * Loads the "growth" value a master data object.
   *
   * @param className class name of the master data
   * @return integer representation of the value
   */
  private Integer getMasterDataGrowth(String className) {
    Integer growth = null;

    try {
      growth = getMasterDataConfigNode(className).getInt("growth");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return growth;
  }

  /**
   * Loads the minimal price of a product.
   *
   * @return float value of the min price
   */
  public Float getProductMinPrice() {
    Float minPrice = Float.MIN_VALUE;

    try {
      minPrice = (float) getMasterDataConfigNode("Product").getDouble("minPrice");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return minPrice;
  }

  /**
   * Loads the maximal price of a product.
   *
   * @return float value of the max price
   */
  public Float getProductMaxPrice() {
    Float maxPrice = Float.MIN_VALUE;

    try {
      maxPrice = (float) getMasterDataConfigNode("Product").getDouble("maxPrice");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return maxPrice;
  }

  /**
   * Sets the scale factor for the sales quotation count
   *
   * @param scaleFactor the scalefactor for sales quotation count
   */
  public void setScaleFactor(Integer scaleFactor) {
    this.scaleFactor = scaleFactor;
  }

  /**
   * Returns the scale factor for the sales quotation count
   *
   * @return the scale factor
   */
  public Integer getScaleFactor() {
    return scaleFactor;
  }

  /**
   * Loads the cases per scale factor and calculates the case count.
   *
   * @return the case count
   */
  public Integer getCaseCount() {
    Integer casesPerScaleFactor = 0;
    int caseCount;

    try {
      casesPerScaleFactor = root.getJSONObject("Process").getInt("casesPerScaleFactor");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    caseCount = scaleFactor * casesPerScaleFactor;
    if (caseCount == 0) {
      caseCount = 10;
    }
    return caseCount;
  }

  /**
   * Loads the start date.
   *
   * @return long representation of the start date
   */
  public Long getStartDate() {
    String startDate;
    DateFormat formatter;
    Date date = Date.from(Instant.EPOCH);

    try {
      startDate = root.getJSONObject("Process").getString("startDate");
      formatter = new SimpleDateFormat("yyyy-MM-dd");
      date = formatter.parse(startDate);
    } catch (JSONException | ParseException e) {
      e.printStackTrace();
    }
    return date.getTime();
  }

  /**
   * Loads the growth of the specified master data and multiplies this with
   * the scale factor.
   *
   * @param className class name of the maste date
   * @return amount of master data to be created
   */
  public Integer getMasterDataCount(String className) {
    return getMasterDataOffset(className) + (getMasterDataGrowth(className) * scaleFactor);
  }

  /**
   * Loads the "TransactionalData" object.
   *
   * @return json object of the transactional data nodes
   * @throws JSONException
   */
  private JSONObject getTransactionalNodes() throws JSONException {
    return root.getJSONObject("TransactionalData");
  }

  /**
   * Loads the "Quality" object.
   *
   * @return json object containing the quality settings
   * @throws JSONException
   */
  private JSONObject getQualityNode() throws JSONException {
    return root.getJSONObject("Quality");
  }

  /**
   * Loads the "good" quality value.
   *
   * @return float representation of the good value
   */
  public Float getQualityGood() {
    Float quality = null;

    try {
      quality = (float) getQualityNode().getDouble("good");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return quality;
  }

  /**
   * Loads the "normal" quality value.
   *
   * @return float representation of the normal value
   */
  public Float getQualityNormal() {
    Float quality = null;

    try {
      quality = (float) getQualityNode().getDouble("normal");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return quality;
  }

  /**
   * Loads the "bad" quality value.
   *
   * @return float representation of the bad value
   */
  public Float getQualityBad() {
    Float quality = null;

    try {
      quality = (float) getQualityNode().getDouble("bad");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return quality;
  }

  /**
   * Loads the "HigherIsBetter" boolean value or returns the default value if
   * there is no "HigherIsBetter" found (for the given key).
   *
   * @param node the transactional data node
   * @param key the key to load the higher is better boolean from
   * @param defaultValue default value
   * @return loaded boolean value or default if none was found
   */
  private boolean getHigherIsBetter(String node, String key, boolean defaultValue) {
    Boolean value;

    try {
      value = getTransactionalNodes().getJSONObject(node).getBoolean(key + "HigherIsBetter");
    } catch (JSONException e) {
      value = defaultValue;
    }
    return value;
  }

  /**
   * Loads the "Influence" float value or returns the default value if
   * there is no "Influence" found (for the given key).
   *
   * @param node the transactional data node
   * @param key the key to load the influence float from
   * @param defaultValue default value
   * @return loaded float value or default if none was found
   */
  private Float getInfluence(String node, String key, Float defaultValue) {
    Float value;

    try {
      value = (float) getTransactionalNodes().getJSONObject(node).getDouble(key + "Influence");
    } catch (JSONException e) {
      value = defaultValue;
    }
    return value;
  }

  /**
   * Adds positive or negative influence to the start value, depending on the
   * quality of the master data objects.
   *
   * @param influencingMasterDataQuality list of influencing master data quality
   * @param higherIsBetter true if positiv influence shall be added, negative
   *                       influence otherwise
   * @param influence influence value to be added to the start value
   * @param startValue the start value
   * @return aggregated start value
   */
  private Float getValue(List<Float> influencingMasterDataQuality, boolean higherIsBetter,
    Float influence, Float startValue) {
    Float value = startValue;

    for (float quality : influencingMasterDataQuality) {
      // check quality value of the masterdata and adjust the result value
      if (quality >= getQualityGood()) {
        if (higherIsBetter) {
          value += influence;
        } else {
          value -= influence;
        }
      } else if (quality <= getQualityBad()) {
        if (higherIsBetter) {
          value -= influence;
        } else {
          value += influence;
        }
      }
    }
    return value;
  }

  /**
   * Calculates and returns integer value of the loaded key.
   *
   * @param influencingMasterDataQuality list of influencing master data quality
   * @param node the transactional data node
   * @param key the key to load from
   * @param higherIsBetterDefault default value to define that a higher value is better or not
   * @return integer value
   */
  public Integer getIntRangeConfigurationValue(List<Float> influencingMasterDataQuality,
    String node, String key, boolean higherIsBetterDefault) {
    Integer min = 0;
    Integer max = 0;
    Integer startValue;
    Integer value;
    Random random = new Random();
    Boolean higherIsBetter = getHigherIsBetter(node, key, higherIsBetterDefault);
    Float influence = getInfluence(node, key, 0.0f);

    // load the min and max values for the node and key combination
    try {
      min = getTransactionalNodes().getJSONObject(node).getInt(key + "Min");
      max = getTransactionalNodes().getJSONObject(node).getInt(key + "Max");
    } catch (JSONException e) {
      e.printStackTrace();
    }

    // generate a random value to start with
    startValue = 1 + random.nextInt((max - min) + 1) + min;

    // get the result value depending on the start value
    value = getValue(
      influencingMasterDataQuality, higherIsBetter, influence, startValue.floatValue()).intValue();

    // keep result in boundaries
    if (value < min) {
      value = min;
    } else if (value > max) {
      value = max;
    }

    return value;
  }

  /**
   * Calculates and returns BigDecimal value of the loaded key.
   *
   * @param influencingMasterDataQuality list of influencing master data quality
   * @param node the transactional data node
   * @param key the key to load from
   * @param higherIsBetterDefault default value to define that a higher value is better or not
   * @return BigDecimal value
   */
  public BigDecimal getDecimalVariationConfigurationValue(List<Float> influencingMasterDataQuality,
    String node, String key, boolean higherIsBetterDefault) {
    Float baseValue = null;
    Float value;
    Boolean higherIsBetter = getHigherIsBetter(node, key, higherIsBetterDefault);
    Float influence = getInfluence(node, key, null);

    // load the value to start with
    try {
      baseValue = (float) getTransactionalNodes().getJSONObject(node).getDouble(key);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    // get the result value based on the loaded one
    value = getValue(influencingMasterDataQuality, higherIsBetter, influence, baseValue);

    // round and return the value
    return BigDecimal.valueOf(value).setScale(2, BigDecimal.ROUND_HALF_UP);
  }

  /**
   * Calculates wether a transition happens or not, based on the influencing
   * master data objects.
   *
   * @param influencingMasterDataQuality list of influencing master data quality
   * @param node the transactional data node
   * @param key the key to load from
   * @param higherIsBetterDefault default value to define that a higher value is better or not
   * @return true of transactions happens
   */
  public boolean happensTransitionConfiguration(List<Float> influencingMasterDataQuality,
    String node, String key, boolean higherIsBetterDefault) {
    Float baseValue = null;
    Float value;
    Boolean higherIsBetter = getHigherIsBetter(node, key, higherIsBetterDefault);
    Float influence = getInfluence(node, key, null);

    // load the value to start with
    try {
      baseValue = (float) getTransactionalNodes().getJSONObject(node).getDouble(key);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    // get the result value based on the loaded one
    value = getValue(influencingMasterDataQuality, higherIsBetter, influence, baseValue);

    // return if the value is greater or equal to a random one
    return (float) Math.random() <= value;
  }

  /**
   * Calculates and returns the new date corresponding to the quality of the
   * influencing master data objects.
   *
   * @param date initial date
   * @param influencingMasterDataQuality list of influencing master data quality
   * @param node the transactional data node
   * @param key the key to load from
   * @return long representation of the new date
   */
  public long delayDelayConfiguration(long date, List<Float> influencingMasterDataQuality,
    String node, String key) {
    // get the delay from range
    int delay = getIntRangeConfigurationValue(influencingMasterDataQuality, node, key, false);

    // calculate time with delay
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(date);
    calendar.add(Calendar.DATE, delay);

    return calendar.getTimeInMillis();
  }

  /**
   * Calculates and returns the new date corresponding to the quality of the
   * influencing master data object.
   *
   * @param date initial date
   * @param influencingMasterDataQuality influencing master data quality
   * @param node the transactional data node
   * @param key the key to load from
   * @return long representation of the new date
   */
  public long delayDelayConfiguration(long date, Float influencingMasterDataQuality,
    String node, String key) {
    List<Float> influencingMasterDataQualities = new ArrayList<>();
    influencingMasterDataQualities.add(influencingMasterDataQuality);
    return delayDelayConfiguration(date, influencingMasterDataQualities, node, key);
  }

  /**
   * Returns the path to the config file.
   *
   * @return path to config file
   */
  public String getPath() {
    return path;
  }
}

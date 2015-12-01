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
 * along with Gradoop.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gradoop.model.impl.functions.bool;

import org.apache.flink.api.common.functions.CrossFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.tuple.Tuple2;

/**
 * Logical equals as Flink function.
 *
 * @param <T> compared type
 */
public class Equals<T>
  implements CrossFunction<T, T, Boolean> {

  @Override
  public Boolean cross(T left, T right) throws Exception {
    return left.equals(right);
  }

  public static <T> DataSet<Boolean> cross(
    DataSet<T> first, DataSet<T> second) {

    return first.cross(second).with(new Equals<T>());
  }

}

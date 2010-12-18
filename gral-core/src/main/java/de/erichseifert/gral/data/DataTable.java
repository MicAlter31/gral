/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2010 Erich Seifert <dev[at]richseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.data;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.erichseifert.gral.data.comparators.DataComparator;


/**
 * </p>Creates a table-like data source. <code>DataTable</code> is a basic writable
 * implementation of <code>DataSource</code>.</p>
 * <p>Its functionality includes:</p>
 * <ul>
 *   <li>Adding and getting rows</li>
 *   <li>Getting rows cells</li>
 *   <li>Deleting all data in the table</li>
 *   <li>Getting row and column count</li>
 *   <li>Sorting the table with a specific <code>DataComparator</code></li>
 * </ul>
 */
public class DataTable extends AbstractDataSource {
	/** All values stored as rows of column arrays. */
	private final List<Number[]> rows;
	/** Data types that are allowed in the respective columns. */
	private final Class<? extends Number>[] types;
	/** Number of columns. */
	private final int columnCount;
	/** Number of rows. */
	private int rowCount;

	/**
	 * Creates a new DataTable object.
	 * @param types type for each column
	 */
	public DataTable(Class<? extends Number>... types) {
		this.types = Arrays.copyOf(types, types.length);
		columnCount = types.length;
		rows = new ArrayList<Number[]>();
	}

	/**
	 * Adds a row with the specified <code>Number</code> values to the table.
	 * The values are added in the order they are specified. If the types of
	 * the table columns and the values do not match, an
	 * <code>IllegalArgumentException</code> is thrown.
	 * @param values values to be added as a row
	 */
	public void add(Number... values) {
		add(Arrays.asList(values));
	}

	/**
	 * Adds a row with the specified container's elements to the table.
	 * The values are added in the order they are specified. If the types of
	 * the table columns and the values do not match, an
	 * <code>IllegalArgumentException</code> is thrown.
	 * @param values values to be added as a row
	 */
	public void add(Collection<? extends Number> values) {
		if (values.size() != getColumnCount()) {
			throw new IllegalArgumentException(MessageFormat.format(
					"Wrong number of columns! Expected {0,number,integer}, got {1,number,integer}.", //$NON-NLS-1$
					types.length, values.size()));
		}
		int i = 0;
		Number[] row = new Number[values.size()];
		for (Number value : values) {
			if ((value != null)
					&& !(types[i].isAssignableFrom(value.getClass()))) {
				throw new IllegalArgumentException(MessageFormat.format(
						"Wrong column type! Expected {0}, got {1}.", //$NON-NLS-1$
						types[i], value.getClass()));
			}
			row[i++] = value;
		}
		rows.add(row);
		rowCount++;
		notifyDataChanged();
	}

	/**
	 * Removes a specified row from the table.
	 * @param row Index of the row to remove
	 */
	public void remove(int row) {
		rows.remove(row);
		rowCount--;
		notifyDataChanged();
	}

	/**
	 * Deletes all rows this table contains.
	 */
	public void clear() {
		rows.clear();
		rowCount = 0;
		notifyDataChanged();
	}

	@Override
	public Number get(int col, int row) {
		return rows.get(row)[col];
	}

	/**
	 * Sets the value of a certain cell.
	 * @param col Column of the cell to change.
	 * @param row Row of the cell to change.
	 * @param value New value to be set.
	 * @return Old value that was replaced.
	 */
	public Number set(int col, int row, Number value) {
		Number old = get(col, row);
		if (!old.equals(value)) {
			rows.get(row)[col] = value;
			notifyDataChanged();
		}
		return old;
	}

	@Override
	public int getRowCount() {
		return rowCount;
	}

	@Override
	public int getColumnCount() {
		return columnCount;
	}

	/**
	 * Returns the data type of the specified column.
	 * @param col Column.
	 * @return Data type.
	 */
	public Class<? extends Number> getColumnClass(int col) {
		return this.types[col];
	}

	/**
	 * Sorts the table rows with the specified DataComparators.
	 * The row values are compared in the way the comparators are specified.
	 * @param comparators comparators used for sorting
	 */
	public void sort(final DataComparator... comparators) {
		Collections.sort(rows, new Comparator<Number[]>() {
			@Override
			public int compare(Number[] o1, Number[] o2) {
				for (DataComparator comp : comparators) {
					int result = comp.compare(o1, o2);
					if (result != 0) {
						return result;
					}
				}
				return 0;
			}
		});
	}
}

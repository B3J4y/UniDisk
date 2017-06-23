package de.unidisk.common.mysql;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * 
 * @author Julian Diese Klasse dient dazu, die Exceptions von ResultSet zu
 *         kapseln, so dass diese nicht gehandled werden müssen.
 */
public class VereinfachtesResultSet {

	private ResultSet resultSet;

	public VereinfachtesResultSet(ResultSet resultset) {
		if (resultset == null) {
			throw new Error("kein Resultset");
		}
		this.resultSet = resultset;
	}

	public boolean next() {
		try {
			return resultSet.next();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	// public void close() {
	// try { return
	// resultSet.close();
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public boolean wasNull() {
	// try { return
	// resultSet.wasNull();
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public String getString(int columnIndex) {
	// try { return
	// resultSet.getString(columnIndex);
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public boolean getBoolean(int columnIndex) {
	// try { return
	// resultSet.getBoolean(columnIndex);
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public byte getByte(int columnIndex) {
	// try { return
	// resultSet.getByte(columnIndex);
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	// public short getShort(int columnIndex) {
	// try { return
	// resultSet.getShort(columnIndex);
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	public int getInt(int columnIndex) {
		try {
			return resultSet.getInt(columnIndex);
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	// public long getLong(int columnIndex) {
	// try { return
	// resultSet.getLong(columnIndex);
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	public float getFloat(int columnIndex) {
		try {
			return resultSet.getFloat(columnIndex);
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	public double getDouble(int columnIndex) {
		try {
			return resultSet.getDouble(columnIndex);
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	// public BigDecimal getBigDecimal(int columnIndex, int scale) {
	// try { return
	// resultSet.getBigDecimal(columnIndex,scale);
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	// public byte[] getBytes(int columnIndex) {
	// try { return
	// resultSet.getBytes(columnIndex);
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	public Date getDate(int columnIndex) {
		try {
			return resultSet.getDate(columnIndex);
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	// public Time getTime(int columnIndex) {
	// try { return
	// resultSet.getTime(columnIndex);
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	public Timestamp getTimestamp(int columnIndex) {
		try {
			return resultSet.getTimestamp(columnIndex);
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	// public InputStream getAsciiStream(int columnIndex) {
	// try { return
	// resultSet.getAsciiStream(columnIndex);
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	public String getString(String columnLabel) {
		try {
			return resultSet.getString(columnLabel);
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	public boolean getBoolean(String columnLabel) {
		try {
			return resultSet.getBoolean(columnLabel);
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	public int getInt(String columnLabel) {
		try {
			return resultSet.getInt(columnLabel);
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	public long getLong(String columnLabel) {
		try {
			return resultSet.getLong(columnLabel);
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	public float getFloat(String columnLabel) {
		try {
			return resultSet.getFloat(columnLabel);
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	public double getDouble(String columnLabel) {
		try {
			return resultSet.getDouble(columnLabel);
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	public ResultSetMetaData getMetaData() {
		try {
			return resultSet.getMetaData();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	public Object getObject(int columnIndex) {
		try {
			return resultSet.getObject(columnIndex);
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	public Object getObject(String columnLabel) {
		try {
			return resultSet.getObject(columnLabel);
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	public int findColumn(String columnLabel) {
		try {
			return resultSet.findColumn(columnLabel);
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	public boolean isBeforeFirst() {
		try {
			return resultSet.isBeforeFirst();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	public boolean isAfterLast() {
		try {
			return resultSet.isAfterLast();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	public boolean isFirst() {
		try {
			return resultSet.isFirst();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	public boolean isLast() {
		try {
			return resultSet.isLast();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	public void beforeFirst() {
		try {
			resultSet.beforeFirst();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	public void afterLast() {
		try {
			resultSet.afterLast();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	public boolean first() {
		try {
			return resultSet.first();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	public boolean last() {
		try {
			return resultSet.last();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	public int getRow() {
		try {
			return resultSet.getRow();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		throw new Error("error in VereinfachtesResultSet");
	}

	// public boolean absolute(int row) {
	// try { return
	// resultSet.ab
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public boolean relative(int rows) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public boolean previous() {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void setFetchDirection(int direction) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public int getFetchDirection() {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void setFetchSize(int rows) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public int getFetchSize() {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public int getType() {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public int getConcurrency() {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public boolean rowUpdated() {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public boolean rowInserted() {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public boolean rowDeleted() {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateNull(int columnIndex) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateBoolean(int columnIndex, boolean x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateByte(int columnIndex, byte x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateShort(int columnIndex, short x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateInt(int columnIndex, int x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateLong(int columnIndex, long x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateFloat(int columnIndex, float x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateDouble(int columnIndex, double x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateBigDecimal(int columnIndex, BigDecimal x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateString(int columnIndex, String x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateBytes(int columnIndex, byte[] x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateDate(int columnIndex, Date x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateTime(int columnIndex, Time x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateTimestamp(int columnIndex, Timestamp x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateAsciiStream(int columnIndex, InputStream x, int length)
	// {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateBinaryStream(int columnIndex, InputStream x, int
	// length) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateCharacterStream(int columnIndex, Reader x, int length)
	// {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateObject(int columnIndex, Object x, int scaleOrLength) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateObject(int columnIndex, Object x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateNull(String columnLabel) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateBoolean(String columnLabel, boolean x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateByte(String columnLabel, byte x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateShort(String columnLabel, short x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateInt(String columnLabel, int x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateLong(String columnLabel, long x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateFloat(String columnLabel, float x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateDouble(String columnLabel, double x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateBigDecimal(String columnLabel, BigDecimal x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateString(String columnLabel, String x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateBytes(String columnLabel, byte[] x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateDate(String columnLabel, Date x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateTime(String columnLabel, Time x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateTimestamp(String columnLabel, Timestamp x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateAsciiStream(String columnLabel, InputStream x, int
	// length) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateBinaryStream(String columnLabel, InputStream x, int
	// length) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateCharacterStream(String columnLabel, Reader reader, int
	// length) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateObject(String columnLabel, Object x, int scaleOrLength)
	// {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateObject(String columnLabel, Object x) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void insertRow() {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void updateRow() {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void deleteRow() {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void refreshRow() {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void cancelRowUpdates() {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void moveToInsertRow() {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public void moveToCurrentRow() {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public Statement getStatement() {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public Object getObject(int columnIndex, Map<String, Class<?>> map) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public Ref getRef(int columnIndex) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public Blob getBlob(int columnIndex) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public Clob getClob(int columnIndex) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public Array getArray(int columnIndex) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public Object getObject(String columnLabel, Map<String, Class<?>> map) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public Ref getRef(String columnLabel) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public Blob getBlob(String columnLabel) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public Clob getClob(String columnLabel) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public Array getArray(String columnLabel) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public Date getDate(int columnIndex, Calendar cal) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public Date getDate(String columnLabel, Calendar cal) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public Time getTime(int columnIndex, Calendar cal) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public Time getTime(String columnLabel, Calendar cal) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public Timestamp getTimestamp(int columnIndex, Calendar cal) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public Timestamp getTimestamp(String columnLabel, Calendar cal) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public URL getURL(int columnIndex) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
	//
	// public URL getURL(String columnLabel) {
	// try { return
	// } catch (SQLException ex) {
	// System.err.println(ex.getMessage());
	// }
	// throw new Error("error in VereinfachtesResultSet");
	// }
}

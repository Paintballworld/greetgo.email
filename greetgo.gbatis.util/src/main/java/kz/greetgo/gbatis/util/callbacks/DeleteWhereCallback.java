package kz.greetgo.gbatis.util.callbacks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import kz.greetgo.gbatis.futurecall.SqlViewer;
import kz.greetgo.gbatis.util.SqlUtil;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;

public final class DeleteWhereCallback implements ConnectionCallback<Integer> {
  
  public SqlViewer sqlViewer;
  private final String tableName;
  private final String where;
  private final Object[] values;
  
  public DeleteWhereCallback(String tableName, String where, Object... values) {
    this.tableName = tableName;
    this.where = where;
    this.values = values;
  }
  
  public DeleteWhereCallback(SqlViewer sqlViewer, String tableName, String where, Object... values) {
    this.sqlViewer = sqlViewer;
    this.tableName = tableName;
    this.where = where;
    this.values = values;
  }
  
  @Override
  public Integer doInConnection(Connection con) throws SQLException, DataAccessException {
    StringBuilder sql = new StringBuilder();
    sql.append("delete from ").append(tableName);
    U.appendWhere(sql, where);
    
    long startedAt = System.currentTimeMillis();
    
    PreparedStatement ps = con.prepareStatement(sql.toString());
    if (values != null) {
      int index = 1;
      for (Object value : values) {
        ps.setObject(index++, SqlUtil.forSql(value));
      }
    }
    String err = U.SQLERROR;
    try {
      int ret = ps.executeUpdate();
      err = null;
      return ret;
    } finally {
      ps.close();
      
      if (U.need(sqlViewer)) U.view(startedAt, sqlViewer, err, sql.toString(), values);
    }
  }
}

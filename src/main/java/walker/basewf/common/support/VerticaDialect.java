package walker.basewf.common.support;

/**
 *
 * 实现Vertica数据库的SQL分页逻辑
 *
 * Created by Huqingmiao on 2015/4/14.
 *
 */
import com.github.walker.mybatis.paginator.dialect.Dialect;
import com.github.walker.mybatis.paginator.PageBounds;
import org.apache.ibatis.mapping.MappedStatement;

public class VerticaDialect extends Dialect {

    public VerticaDialect(MappedStatement mappedStatement, Object parameterObject, PageBounds pageBounds) {
        super(mappedStatement, parameterObject, pageBounds);
    }

    protected String getLimitString(String sql, String offsetName, int offset, String limitName, int limit) {
        StringBuffer buffer = (new StringBuffer(sql.length() + 20)).append(sql);
        if (offset > 0) {
            buffer.append(" offset ? limit ?");
            setPageParameter(offsetName, Integer.valueOf(offset), Integer.class);
            setPageParameter(limitName, Integer.valueOf(limit), Integer.class);
        } else {
            buffer.append(" limit ?");
            setPageParameter(limitName, Integer.valueOf(limit), Integer.class);
        }
        return buffer.toString();
    }
}

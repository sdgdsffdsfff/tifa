package walker.basewf.common;

import walker.mybatis.paginator.PageBounds;

import java.util.List;
import java.util.Map;

/**
 * mybatis DAO基类
 * <p/>
 * Created by HuQingmiao on 2015-4-29.
 */
public interface BasicDao {

    public int save(BasicVo basicVo);

    public void saveBatch(List list);


    public int update(BasicVo basicVo);

    public int updateIgnoreNull(BasicVo basicVo);

    public void updateBatch(List list);


    public int delete(BasicVo basicVo);

    public void deleteBatch(List list);

    public int deleteByPK(Long id);

    public int deleteAll();


    public long count();

    public BasicVo findByPK(Long id);

    public List find(Map<String, Object> paramMap, PageBounds pageBounds);
}

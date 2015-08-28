package walker.basewf.demo.dao;


import walker.basewf.common.BasicDao;

import java.util.List;

public interface BookDao extends BasicDao {

    public void deleteByPks(List<Long> ids);

    public List<String> findBookTitles();
}

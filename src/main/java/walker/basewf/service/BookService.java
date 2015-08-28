package walker.basewf.service;

import java.util.*;

import com.github.walker.mybatis.paginator.Order;
import com.github.walker.mybatis.paginator.PageBounds;
import com.github.walker.mybatis.paginator.PageList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import walker.basewf.common.utils.DateTimeUtil;
import walker.basewf.dao.BookDao;
import walker.basewf.vo.Book;



/**
 * 演示、测试mybatis的常用功能
 *
 * @author HuQingmiao
 */
@Service
@Transactional
public class BookService {
    private Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private BookDao bookDao;

    public void clearTestData() {
        log.info("\n\nclearTestData()>>>>>>>>>>>>>>>>>>>>");

        bookDao.deleteAll();
    }

    public void deleteBooks() {
        log.info("\n\ndeleteBooks()>>>>>>>>>>>>>>>>>>>>");

        List<Long> idSets = new ArrayList<Long>();
        idSets.add(new Long(101));
        idSets.add(new Long(103));
        bookDao.deleteByPks(idSets);
    }

    /**
     * 增加一本书;
     */
    public void addOneBook() {
        log.info("\n\naddOneBook()>>>>>>>>>>>>>>>>>>>>");

        Book book = new Book();
        book = new Book();
        book.setBookId(new Long(101));
        book.setTitle(new String("三国演义"));
        book.setCost(new Double(200.0));
        // book.setPublishTime(DateTimeUtil.toSqlDate(new Date()));
        bookDao.save(book);
    }

    /**
     * 批量增加多本书;
     */
    public void addMultiBooks() {
        log.info("\n\naddMultiBooks()>>>>>>>>>>>>>>>>>>>>");

        int size = 3;
        Book[] BookArray = new Book[size];

        BookArray[0] = new Book();
        BookArray[0].setBookId(new Long(102));
        BookArray[0].setTitle(new String("UNIX-上册"));
        BookArray[0].setCost(new Double(40.0));

        BookArray[1] = new Book();
        BookArray[1].setBookId(new Long(103));
        BookArray[1].setTitle(new String("UNIX-中册"));
        BookArray[1].setCost(new Double(60.0));

        BookArray[2] = new Book();
        BookArray[2].setBookId(new Long(104));
        BookArray[2].setTitle(new String("UNIX-下册"));
        BookArray[2].setCost(new Double(50.0));
        //	BookArray[2].setPublishTime(new Date());

        bookDao.saveBatch(Arrays.asList(BookArray));
    }

    /**
     * 根据主键更新某本书
     */
    public void updateOneBook() {
        log.info("\n\nupdateOneBook()>>>>>>>>>>>>>>>>>>>>");

        Book book = new Book();
        book.setTitle(new String("八国演义(第二版)"));
        book.setBookId(new Long(101));

        bookDao.updateIgnoreNull(book);
    }

    /**
     * 对部分图书设定折扣价
     */
    public void updateMultiBooks() {
        log.info("\n\nupdateMultiBooks>>>>>>>>>>>>>>>>>>>>");

        // 找出50元以上的书，然后打6折
        HashMap<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("minCost", new Double(50));
//        List<Book> bookList = bookDao.find(paramMap, new PageBounds());
//        for (Book book : bookList) {
//            book.setCost(new Double(book.getCost() * 0.6));
//            bookDao.update(book);
//        }
    }

    public void deleteOneBook() {
        log.info("\n\ndeleteOneBook()>>>>>>>>>>>>>>>>>>>>");
        bookDao.deleteByPK(new Long(101));
    }

    public void deleteMultiBooks() {
        log.info("\n\ndeleteMultiBooks>>>>>>>>>>>>>>>>>>>>");

        HashMap<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("maxCost", new Float(40));
       // List<Book> bookList = bookDao.find(paramMap, new PageBounds());

       // bookDao.deleteBatch(bookList);
    }

    public List<Book> findBooks() {
        HashMap<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("title", "%UNIX%");
        paramMap.put("minCost", new Float(21));
        paramMap.put("maxCost", new Float(101));

        // new PageBounds(); //非分页方式，采用默认构造函数
        // new PageBounds(int limit); //取前面的limit条记录

        //取从offset开始的limit条记录，offset从1开始
        // new PageBounds(int offset, int limit);

        //按cost升序、book_id倒序排列后再分页
        // new PageBounds(int page, int limit, Order.formString("cost.asc, book_id.desc"));

        //如果想排序的话,以逗号分隔多项排序,若查询语句中有ORDER BY, 则仍然会以此为准。
        String sortString = "cost.asc, book_id.desc";

        //取第4条开始的3条记录
        PageBounds pageBounds = new PageBounds(4, 3, Order.formString(sortString));
        List<Book> bookList = bookDao.find(paramMap, pageBounds);

        PageList<Book> pageList = (PageList<Book>) bookList;// 获得结果集条总数
        log.info("本页记录数: " + bookList.size());
        log.info("总的记录数: " + pageList.size());
        for (Book book : bookList) {
            log.info(book.getBookId() + " " + book.getTitle() + " " + book.getCost());
        }

        return bookList;
    }


    public void testBatchInsert(int cnt) {

        int size = cnt;//每条约100bytes, 则10000条/M, 10万条/10M; 约20秒
        Book[] BookArray = new Book[size];

        for (int i = 0; i < BookArray.length; i++) {
            BookArray[i] = new Book();
            BookArray[i].setBookId(new Long(i));
            //BookArray[i].setBookId(new Long(i + 1 + begin));
            BookArray[i].setTitle(new String("UNIX-中册"));
            BookArray[i].setCost(new Double(Math.random() * 100));
            BookArray[i].setPublishTime(DateTimeUtil.toSqlDate(new Date()));
            BookArray[i].setUpdateTime(DateTimeUtil.currentTime());

            // bookDao.save(BookArray[i]);
        }

        bookDao.saveBatch(Arrays.asList(BookArray));
    }


    public void testss() {
        List<String> tList = bookDao.findBookTitles();
        for (String s : tList) {
            System.out.println(s);
        }
    }

}
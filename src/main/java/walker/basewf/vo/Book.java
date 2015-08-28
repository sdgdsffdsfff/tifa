package walker.basewf.vo;


import walker.basewf.common.BasicVo;

public class Book extends BasicVo {
    private static final long serialVersionUID = 1L;

    private Long bookId;
    private String title;
    private Double cost;
    private java.sql.Date publishTime;
    private byte[] blobContent;
    private String textContent;
    private java.sql.Timestamp updateTime;


    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public java.sql.Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(java.sql.Date publishTime) {
        this.publishTime = publishTime;
    }

    public byte[] getBlobContent() {
        return blobContent;
    }

    public void setBlobContent(byte[] blobContent) {
        this.blobContent = blobContent;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public java.sql.Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(java.sql.Timestamp updateTime) {
        this.updateTime = updateTime;
    }

}

/*List columns as follows:
"book_id", "title", "cost", "publish_time", "blob_content", "text_content", "update_time"
*/
package lodsve.search.engine;

import lius.index.Indexer;
import lius.index.excel.ExcelIndexer;
import lius.index.msword.WordIndexer;
import lius.index.pdf.PdfIndexer;
import lius.index.powerpoint.PPTIndexer;
import lius.index.txt.TXTIndexer;
import lius.index.xml.XmlFileIndexer;
import lodsve.search.bean.BaseSearchBean;
import lodsve.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;

/**
 * 搜索引擎的公用方法.
 *
 * @author sunhao(sunhao.java@gmail.com)
 * @version V1.0
 * @createTime 13-5-8 下午10:53
 */
public abstract class AbstractSearchEngine implements SearchEngine {
    private static final Logger logger = LoggerFactory.getLogger(AbstractSearchEngine.class);

    /**
     * 进行高亮处理时,html片段的前缀
     */
    private String htmlPrefix = "<p>";
    /**
     * 进行高亮处理时,html片段的后缀
     */
    private String htmlSuffix = "</p>";

    public String getHtmlPrefix() {
        return htmlPrefix;
    }

    public void setHtmlPrefix(String htmlPrefix) {
        this.htmlPrefix = htmlPrefix;
    }

    public String getHtmlSuffix() {
        return htmlSuffix;
    }

    public void setHtmlSuffix(String htmlSuffix) {
        this.htmlSuffix = htmlSuffix;
    }

    @Override
    public Page<BaseSearchBean> doSearch(BaseSearchBean bean, boolean isHighlighter, Pageable pageable) throws Exception {
        if (bean == null) {
            logger.debug("given search bean is empty!");
            return new PageImpl<>(Collections.<BaseSearchBean>emptyList(), null, 0);
        }

        return doSearch(Collections.singletonList(bean), isHighlighter, pageable);
    }

    /**
     * 获取index类型
     *
     * @param bean
     * @return
     */
    protected String getIndexType(BaseSearchBean bean) {
        return StringUtils.isNotEmpty(bean.getIndexType()) ? bean.getIndexType() : bean.getClass().getSimpleName();
    }

    /**
     * 根据indexType从BaseSearchBean的集合中获取对应的bean
     *
     * @param indexType
     * @param beans
     * @return
     */
    protected BaseSearchBean getBaseSearchBean(String indexType, List<BaseSearchBean> beans) {
        BaseSearchBean result = null;
        if (StringUtils.isEmpty(indexType) || beans == null || beans.isEmpty()) {
            logger.debug("indexType is null or beans is null!");
            return result;
        }

        for (BaseSearchBean b : beans) {
            if (indexType.equals(b.getIndexType())) {
                result = BeanUtils.instantiate(b.getClass());
                break;
            }
        }

        return result;
    }

    /**
     * 根据文件名称获取文件lius的索引
     *
     * @param fileName
     * @return
     */
    protected Indexer getFileIndexer(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            logger.warn("fileName is empty!");
            return null;
        }

        Indexer indexer;
        if (fileName.endsWith(".doc")) {
            indexer = new WordIndexer();
        } else if (fileName.endsWith(".xls")) {
            indexer = new ExcelIndexer();
        } else if (fileName.endsWith(".pdf")) {
            indexer = new PdfIndexer();
        } else if (fileName.endsWith(".txt")) {
            indexer = new TXTIndexer();
        } else if (fileName.endsWith(".ppt")) {
            indexer = new PPTIndexer();
        } else if (fileName.endsWith(".xml")) {
            indexer = new XmlFileIndexer();
        } else {
            indexer = null;
        }

        return indexer;
    }

    protected String getFileContent(File file) {
        if (file == null || !file.exists()) {
            return StringUtils.EMPTY;
        }

        FileInputStream is;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return StringUtils.EMPTY;
        }

        Indexer indexer = this.getFileIndexer(file.getName());
        if (indexer == null) {
            return StringUtils.EMPTY;
        }

        indexer.setStreamToIndex(is);
        return indexer.getContent();
    }
}

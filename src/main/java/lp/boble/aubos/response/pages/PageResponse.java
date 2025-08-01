package lp.boble.aubos.response.pages;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class PageResponse<T> {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<T> content;

    private PageResponse(){}

    public PageResponse(Page<T> page){
        this.page = page.getNumber() + 1;
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
        this.content = page.getContent();
    }

    public <R> PageResponse<R> map(Function<T, R> mapper){

        List<R> mappedContent = this.content.stream()
                .map(mapper)
                .collect(Collectors.toList());

        PageResponse<R> mappedPage = new PageResponse<>();
        mappedPage.page = this.page;
        mappedPage.size = this.size;
        mappedPage.totalElements = this.totalElements;
        mappedPage.totalPages = this.totalPages;
        mappedPage.hasNext = this.hasNext;
        mappedPage.hasPrevious = this.hasPrevious;
        mappedPage.content = mappedContent;

        return mappedPage;
    }


}

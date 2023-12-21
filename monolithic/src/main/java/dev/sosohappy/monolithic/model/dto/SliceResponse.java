package dev.sosohappy.monolithic.model.dto;

import lombok.Data;
import org.springframework.data.domain.Slice;

import java.util.List;

@Data
public class SliceResponse<T> {

    private List<T> content;

    private Integer numberOfElements;
    private Integer pageNumber;
    private Integer pageSize;
    private Boolean isLast;

    public SliceResponse(Slice<T> result){
        this.content = result.getContent();

        this.numberOfElements = result.getNumberOfElements();
        this.pageNumber = result.getNumber();
        this.pageSize = result.getSize();
        this.isLast = result.isLast();
    }

}

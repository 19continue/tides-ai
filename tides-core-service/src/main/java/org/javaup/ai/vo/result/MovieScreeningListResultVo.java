package org.javaup.ai.vo.result;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.javaup.ai.vo.MovieScreeningVo;
import org.javaup.ai.vo.result.base.ApiResponse;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MovieScreeningListResultVo extends ApiResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<MovieScreeningVo> data;
}

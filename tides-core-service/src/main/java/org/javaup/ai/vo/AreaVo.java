package org.javaup.ai.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AreaVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long parentId;

    private String name;

    private Integer type;

    private Integer municipality;
}

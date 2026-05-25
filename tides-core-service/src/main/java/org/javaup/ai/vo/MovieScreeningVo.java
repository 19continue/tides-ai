package org.javaup.ai.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class MovieScreeningVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long movieId;

    private Long programId;

    private Long cinemaId;

    private String cinemaName;

    private String cinemaAddress;

    private Long hallId;

    private String hallName;

    private String hallType;

    private Date showTime;

    private Date showDayTime;

    private String showWeekTime;

    private Date endTime;

    private String language;

    private String version;

    private BigDecimal lowestPrice;

    private Date stopSellTime;

    private Integer screeningStatus;
}

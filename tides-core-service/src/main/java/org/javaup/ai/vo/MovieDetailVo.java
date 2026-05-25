package org.javaup.ai.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class MovieDetailVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long movieId;

    private Long programId;

    private String movieName;

    private String movieAlias;

    private String director;

    private String actors;

    private Integer durationMinutes;

    private String language;

    private String region;

    private Date releaseDate;

    private String poster;

    private String description;

    private String genre;

    private Integer releaseStatus;

    private Long wantWatchCount;

    private Long watchedCount;

    private BigDecimal ratingScore;

    private BigDecimal boxOfficeAmount;

    private String producer;

    private String distributor;

    private String ageTips;

    private String longDescription;

    private BigDecimal lowestPrice;

    private Date nearestShowTime;

    private Integer cinemaCount;

    private List<Date> showDayList;

    private List<MovieScreeningVo> screeningList;
}

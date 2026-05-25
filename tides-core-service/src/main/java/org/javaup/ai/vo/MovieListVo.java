package org.javaup.ai.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class MovieListVo implements Serializable {

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

    private BigDecimal lowestPrice;

    private Date nearestShowTime;

    private Integer cinemaCount;

    private Integer screeningCount;
}

package rs.edu.raf.banka1.dtos.market_service;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Country {
    private Long id;
    private String ISOCode;
    private int timezoneOffset;

    // "mm:hh:ss
    private String openTime;
    private String closeTime;
}


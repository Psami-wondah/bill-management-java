package backend.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import backend.enums.ReadingType;
import backend.repositories.MeterReadingRepository;

public class MeterReading implements BaseModel {

    public static final MeterReadingRepository objects = new MeterReadingRepository();

    private final String id;
    private String meterId;
    private LocalDate date; // ISO 8601 format: YYYY-MM-DD
    private BigDecimal singleValue;
    private BigDecimal dayValue;
    private BigDecimal nightValue;
    private ReadingType readingType;

    @JsonCreator
    public MeterReading(@JsonProperty("id") String id, @JsonProperty("meterId") String meterId,
            @JsonProperty("date") LocalDate date, @JsonProperty("singleValue") BigDecimal singleValue,
            @JsonProperty("dayValue") BigDecimal dayValue, @JsonProperty("nightValue") BigDecimal nightValue,
            @JsonProperty("readingType") ReadingType readingType) {
        this.id = id;
        this.meterId = meterId;
        this.date = date;
        this.singleValue = singleValue;
        this.dayValue = dayValue;
        this.nightValue = nightValue;
        this.readingType = readingType;
    }

    public MeterReading(String meterId, LocalDate date, BigDecimal dayValue,
            BigDecimal nightValue, ReadingType readingType) {
        this.id = objects.generateId();
        this.meterId = meterId;
        this.date = date;
        this.dayValue = dayValue;
        this.nightValue = nightValue;
        this.readingType = readingType;
    }

    public MeterReading(String meterId, LocalDate date, BigDecimal singleValue, ReadingType readingType) {
        this.id = objects.generateId();
        this.meterId = meterId;
        this.date = date;
        this.singleValue = singleValue;
        this.readingType = readingType;
    }

    public String getId() {
        return id;
    }

    public String getMeterId() {
        return meterId;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getSingleValue() {
        return singleValue;
    }

    public BigDecimal getDayValue() {
        return dayValue;
    }

    public BigDecimal getNightValue() {
        return nightValue;
    }

    public ReadingType getReadingType() {
        return readingType;
    }

    public void save() {
        objects.add(this);
    }
}

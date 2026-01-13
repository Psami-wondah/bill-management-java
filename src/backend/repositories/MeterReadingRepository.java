package backend.repositories;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import backend.enums.RegisterType;
import backend.enums.UsageKeys;
import backend.models.Database;
import backend.models.Meter;
import backend.models.MeterReading;

public class MeterReadingRepository extends BaseRepository<MeterReading> {

    @Override
    protected void setCollection(Database db, List<MeterReading> items) {
        db.meter_readings = items;
    }

    @Override
    protected List<MeterReading> getCollection(Database db) {
        return db.meter_readings;
    }

    public String generateId() {
        return "MTR" + this.generateId(7);
    }

    public MeterReading findLatestReadingByMeterId(String meterId) {
        return this.filter(r -> r.getMeterId().equals(meterId))
                .stream()
                .max(Comparator.comparing(MeterReading::getDate))
                .orElse(null);
    }

    public Boolean checkValidUsagePeriod(String meterId, LocalDate startDate, LocalDate endDate) {
        List<MeterReading> readings = this.filter(r -> r.getMeterId().equals(meterId));

        if (readings.isEmpty()
                || startDate.isBefore(readings.get(0).getDate())
                || endDate.isAfter(readings.get(readings.size() - 1).getDate())) {
            return false;
        }
        return true;
    }

    public Map<UsageKeys, BigDecimal> getUsageForPeriodByMeter(Meter meter, LocalDate periodStart,
            LocalDate periodEnd) {
        List<MeterReading> readings = MeterReading.objects
                .filter(r -> r.getMeterId().equals(meter.getId()));

        BigDecimal totalSingleUsage = BigDecimal.ZERO;
        BigDecimal totalDayUsage = BigDecimal.ZERO;
        BigDecimal totalNightUsage = BigDecimal.ZERO;

        for (int i = 1; i < readings.size(); i++) {
            MeterReading prev = readings.get(i - 1);
            MeterReading cur = readings.get(i);

            if (prev == null || cur == null) {
                continue;
            }

            System.out.println(
                    "Evaluating readings: Prev Date = " + prev.getDayValue() + ", Cur Date = " + cur.getDayValue());

            if (!cur.getDate().isAfter(prev.getDate())) {
                System.out.println("Meter readings are not in chronological order. Cannot generate invoice.");
                return null;
            }
            LocalDate from = prev.getDate();
            LocalDate to = cur.getDate();

            boolean overlaps = to.isAfter(periodStart) &&
                    from.isBefore(periodEnd);

            System.out.println("Checking readings from " + from + " to " + to + ": Overlaps = " + overlaps);

            if (!overlaps) {
                continue;
            }
            long intervalDays = ChronoUnit.DAYS.between(from, to);
            if (intervalDays <= 0) {

                return null; // or continue
            }

            // clamp to the billing window
            LocalDate overlapStart = from.isBefore(periodStart) ? periodStart : from;
            LocalDate overlapEnd = to.isAfter(periodEnd) ? periodEnd : to;

            if (!overlapStart.isBefore(overlapEnd)) {
                continue;
            }

            long overlapDays = ChronoUnit.DAYS.between(overlapStart, overlapEnd);

            BigDecimal fraction = BigDecimal.valueOf(overlapDays)
                    .divide(BigDecimal.valueOf(intervalDays), 10, RoundingMode.HALF_UP);

            if (meter.getRegisterType() == RegisterType.TWO_REGISTER) {
                BigDecimal dayUsageDelta = cur.getDayValue().subtract(prev.getDayValue());
                BigDecimal nightUsageDelta = cur.getNightValue().subtract(prev.getNightValue());
                BigDecimal dayUsageInThisInterval = dayUsageDelta.multiply(fraction);
                BigDecimal nightUsageInThisInterval = nightUsageDelta.multiply(fraction);
                totalDayUsage = totalDayUsage.add(dayUsageInThisInterval);
                totalNightUsage = totalNightUsage.add(nightUsageInThisInterval);
            } else {
                BigDecimal usageDelta = cur.getSingleValue().subtract(prev.getSingleValue());
                BigDecimal usageInThisInterval = usageDelta.multiply(fraction);
                totalSingleUsage = totalSingleUsage.add(usageInThisInterval);
            }

        }

        return Map.of(
                UsageKeys.SINGLE, totalSingleUsage,
                UsageKeys.DAY, totalDayUsage,
                UsageKeys.NIGHT, totalNightUsage);

    }

}


package net.gidosa.full.webadmin.models.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceDto {
    private Long id;
    private String name;
    private LocalDateTime workStartTime;
    private LocalDateTime workEndTime;
    private String attendanceType;
    private LocalDate date;
}
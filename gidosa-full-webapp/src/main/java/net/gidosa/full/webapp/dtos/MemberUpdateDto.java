package net.gidosa.full.webapp.dtos;

import lombok.Data;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberGeneral;

@Data
public class MemberUpdateDto {
    private String username;
    private String name;
    private String email;
    private String phone;
    private String password;

    public static MemberUpdateDto from(MemberGeneral memberGeneral) {
        MemberUpdateDto dto = new MemberUpdateDto();
        dto.setUsername(memberGeneral.getUsername());
        dto.setName(memberGeneral.getName());
        dto.setEmail(memberGeneral.getEmail());
        dto.setPhone(memberGeneral.getPhone());
        return dto;
    }
} 
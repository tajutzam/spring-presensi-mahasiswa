package com.absensi.absensi.model;

import com.absensi.absensi.model.enums.STATUS;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class Absen {
    @Id
    private  String id;
    private Student student;
    private STATUS status;
    private Date date;
    private Dosen dosen;
}

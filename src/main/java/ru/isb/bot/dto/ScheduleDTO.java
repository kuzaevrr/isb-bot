package ru.isb.bot.dto;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ScheduleDTO implements Serializable {

    private String building_name;
    private String cabinet_fullnumber_wotype;
    private String cabinet_number;
    private String classtype_short;
    private Date date_end;
    private Date date_start;
    private String date_start_text;
    private String daytime_name;
    private String discipline_name;
    private String discipline_short;
    private String group_name;
    private Integer studyyear_id;
    private String subgroup_name;
    private String teacher_cathedra;
    private String teacher_fio;
    private String term;
    private String weekday_name;
    private Integer weekday_ord;
    private String weekday_shortname;

}

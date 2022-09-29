package ru.isb.bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.Deserializers;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonDeserialize
@JsonSerialize
public class ScheduleDTO implements Serializable {

    private String building_id;
    private String building_litera;
    private String building_name;
    private List<Object> cabinet_equipment;
    private String cabinet_fullnumber;
    private String cabinet_fullnumber_wotype;
    private String cabinet_id;
    private String cabinet_number;
    private String cabinet_purpose_name;
    private String cabinet_type_name;
    private Integer classtype_id;
    private String classtype_name;
    private String classtype_short;
    private Date date_end;
    private String date_end_text;
    private Date date_start;
    private String date_start_text;
    private String daytime_id;
    private String daytime_name;
    private Integer daytime_ord;
    private Integer discipline_id;
    private String discipline_name;
    private String discipline_short;
    private Integer group_id;
    private String group_name;
    private String id;
    private Boolean is_exam;
    private String notes;
    private Integer ratetype_id;
    private Integer stream_id;
    private String stream_name;
    private Integer studyyear_id;
    private Integer subgroup_id;
    private String subgroup_name;
    private String teacher_cathedra;
    private String teacher_fio;
    private Integer teacher_id;
    private String term;
    private Integer week_number;
    private String weekday_id;
    private String weekday_name;
    private Integer weekday_ord;
    private String weekday_shortname;

}

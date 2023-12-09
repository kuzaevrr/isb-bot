package ru.isb.bot.dto

import lombok.*
import java.io.Serializable
import java.util.*

@Getter
@Setter
@ToString
class ScheduleDTO : Serializable {


    //    private String building_id;
    //    private String building_litera;
    //    private String building_name;
    //    private List<Object> cabinet_equipment;
    var cabinet_fullnumber: String? = null
    var cabinet_fullnumber_wotype: String? = null

    //    private String cabinet_id;
    //    private String cabinet_number;
    //    private String cabinet_purpose_name;
    //    private String cabinet_type_name;
    //    private Integer classtype_id;
    //    private String classtype_name;
    var classtype_short: String? = null
    var date_end: Date? = null
    var date_end_text: String? = null
    var date_start: Date? = null
    var date_start_text: String? = null

    //    private String daytime_id;
    var daytime_name: String? = null

    //    private Integer daytime_ord;
    //    private Integer discipline_id;
    var discipline_name: String? = null

    //    private String discipline_short;
    //    private Integer group_id;
    //    private String group_name;
    //    private String id;
    //    private Boolean is_exam;
    var notes: String? = null

    //    private Integer ratetype_id;
    //    private Integer stream_id;
    //    private String stream_name;
    //    private Integer studyyear_id;
    //    private Integer subgroup_id;
    //    private String subgroup_name;
    //    private String teacher_cathedra;
    var teacher_fio: String? = null

    //    private Integer teacher_id;
    //    private String term;
    //    private Integer week_number;
    //    private String weekday_id;
    var weekday_name: String? = null
//    private Integer weekday_ord;
//    private String weekday_shortname;
    
}
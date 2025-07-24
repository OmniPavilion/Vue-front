package diary.pojo.vo

import diary.pojo.po.Status

data class PlanTaskVO (
    var title : String,
    var description : String,
    var status : Status
)
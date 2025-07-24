package diary.pojo.vo

import diary.pojo.po.Status
import java.time.LocalDate

data  class PlanVO(
    val id: Int?,
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: Status,
    val tasks: List<PlanTaskVO>
)
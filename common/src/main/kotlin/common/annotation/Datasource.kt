package common.annotation

import common.enumerate.DataSourceType

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Datasource(
    val value: DataSourceType
)

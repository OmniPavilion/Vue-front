package application.annotation

import application.enumerate.DataSourceType

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Datasource(
    val value: DataSourceType
)

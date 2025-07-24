package music.pojo.dto

data class MusicQuery(
    var title: String? = null,
    var singerId: Int? = null,
    var categoryId: Int? = null,
    var isFavorite: Boolean? = null
)
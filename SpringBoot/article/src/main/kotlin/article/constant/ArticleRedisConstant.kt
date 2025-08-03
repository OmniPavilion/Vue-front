package article.constant

class ArticleRedisConstant {
    companion object {
        const val ARTICLE_KEY = "article:"

        const val FILE_KEY = ARTICLE_KEY + "file:"

        // 文件根路径
        const val DEFAULT_ROOT_FIELD = "default_root_path"
        const val ROOT_FIELD = "root_path"
    }
}
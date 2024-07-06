package au.com.afewroosloose.videoexercise.presentation



sealed class Destination(open val routeName: String) {
    data object List : Destination(LIST_NAME)
    data object Error : Destination(ERROR_NAME)
    data class Detail(val index: Int) : Destination(DETAIL_NAME) {
        override val routeName = "${super.routeName}/$index"
    }

    companion object {
        const val LIST_NAME = "list"
        const val ERROR_NAME = "error"
        const val DETAIL_NAME = "detail"
    }
}
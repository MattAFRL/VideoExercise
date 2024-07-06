package au.com.afewroosloose.videoexercise.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType

@Composable
@Preview
fun ErrorText(@PreviewParameter(ErrorPreviewProvider::class) errorString: String?) {
    Text(
        text = errorString ?: "Something went wrong",
        color = Color.Red,
        fontSize = TextUnit(20f, TextUnitType.Sp)
    )
}

class ErrorPreviewProvider() : PreviewParameterProvider<String> {
    override val values: Sequence<String>
        get() = sequenceOf("Something went very wrong...!")
}
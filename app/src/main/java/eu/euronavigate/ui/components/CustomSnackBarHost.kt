package eu.euronavigate.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.euronavigate.ui.utils.UIConstants

@Composable
fun CustomSnackbarHost(hostState: SnackbarHostState) {
	SnackbarHost(hostState = hostState) { data ->
		Snackbar(
			modifier = Modifier
				.padding(UIConstants.PADDING)
				.fillMaxWidth()
				.wrapContentHeight()
				.defaultMinSize(minHeight = 56.dp),
			shape = RoundedCornerShape(UIConstants.SPACER_SMALL),
			containerColor = MaterialTheme.colorScheme.surfaceContainer,
			contentColor = MaterialTheme.colorScheme.secondary
		) {
			Box(
				modifier = Modifier.fillMaxWidth(),
				contentAlignment = Alignment.Center
			) {
				Text(text = data.visuals.message)
			}
		}
	}
}

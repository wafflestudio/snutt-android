package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.components.compose.ArrowDownIcon
import com.wafflestudio.snutt2.components.compose.EmbedMap
import com.wafflestudio.snutt2.components.compose.MapIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.network.dto.core.LectureBuildingDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

var isEmbedMapFoldedSaved = true

@Composable
fun FoldableEmbedMap(
    distinctBuildings: List<LectureBuildingDto>,
) {
    var embedMapFolded by remember {
        mutableStateOf(isEmbedMapFoldedSaved)
    }
    val embedMapAlpha by animateFloatAsState(
        targetValue = if (embedMapFolded) 0f else 1f,
        label = "",
    )

    Column {
        distinctBuildings.let {
            if (it.isNotEmpty()) {
                AnimatedVisibility(visible = embedMapFolded) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .alpha(1f - embedMapAlpha)
                            .padding(vertical = 12.dp)
                            .clicks {
                                if (embedMapFolded) {
                                    embedMapFolded = false
                                }
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        MapIcon(
                            modifier = Modifier.size(17.dp, 19.dp),
                        )
                        Text(
                            text = "지도에서 보기",
                            style = SNUTTTypography.body1.copy(color = SNUTTColors.DarkGray),
                            modifier = Modifier.padding(start = 8.dp, end = 4.dp),
                        )
                        ArrowDownIcon(modifier = Modifier.size(24.dp))
                    }
                }
                AnimatedVisibility(visible = embedMapFolded.not()) {
                    Column {
                        EmbedMap(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(255.dp)
                                .alpha(embedMapAlpha)
                                .padding(horizontal = 20.dp),
                            buildings = it,
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .alpha(embedMapAlpha)
                                .padding(vertical = 12.dp)
                                .clicks {
                                    if (embedMapFolded.not()) embedMapFolded = true
                                },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "지도 닫기",
                                style = SNUTTTypography.body1.copy(color = SNUTTColors.DarkGray),
                                modifier = Modifier.padding(start = 8.dp, end = 4.dp),
                            )
                            ArrowDownIcon(
                                modifier = Modifier
                                    .size(24.dp)
                                    .rotate(180f),
                            )
                        }
                    }
                }
            }
        }
    }
}

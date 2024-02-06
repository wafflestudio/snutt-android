package com.wafflestudio.snutt2.components.compose.embed_map

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ArrowDownIcon
import com.wafflestudio.snutt2.components.compose.MapIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.network.dto.core.LectureBuildingDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

// FIXME: 귀찮아요 그냥 이렇게 할래요
private var isEmbedMapFoldedSaved = true

@Composable
fun FoldableEmbedMap(
    modifier: Modifier,
    buildings: List<LectureBuildingDto>,
) {
    var embedMapFolded by remember {
        mutableStateOf(isEmbedMapFoldedSaved)
    }

    Column(modifier = modifier) {
        if (buildings.isNotEmpty()) {
            AnimatedVisibility(visible = embedMapFolded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(vertical = 12.dp)
                        .clicks {
                            embedMapFolded = false
                            isEmbedMapFoldedSaved = false
                        },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MapIcon(
                        modifier = Modifier.size(17.dp, 19.dp),
                    )
                    Text(
                        text = stringResource(R.string.embed_map_unfold_button),
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
                            .height(
                                if (buildings.size == 1) {
                                    EmbedMapConstants.MapShortHeight
                                } else {
                                    EmbedMapConstants.MapLongHeight
                                },
                            )
                            .padding(horizontal = 20.dp),
                        buildings = buildings,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(vertical = 12.dp)
                            .clicks {
                                embedMapFolded = true
                                isEmbedMapFoldedSaved = true
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.embed_map_fold_button),
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

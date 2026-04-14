package com.reco.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val RecoRadiusSm = 8.dp
val RecoRadiusMd = 12.dp
val RecoRadiusLg = 16.dp
val RecoRadiusXl = 24.dp

val RecoShapes = Shapes(
    extraSmall = RoundedCornerShape(RecoRadiusSm),
    small = RoundedCornerShape(RecoRadiusSm),
    medium = RoundedCornerShape(RecoRadiusMd),
    large = RoundedCornerShape(RecoRadiusLg),
    extraLarge = RoundedCornerShape(RecoRadiusXl),
)

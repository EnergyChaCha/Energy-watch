package chacha.energy.ganghannal.presentation.icons

import androidx.compose.ui.graphics.vector.ImageVector
import chacha.energy.ganghannal.presentation.icons.myiconpack.EcgHeart
import kotlin.collections.List as ____KtList

object MyIconPack

private var __AllIcons: ____KtList<ImageVector>? = null

val MyIconPack.AllIcons: ____KtList<ImageVector>
    get() {
        if (__AllIcons != null) {
            return __AllIcons!!
        }
        __AllIcons = listOf(EcgHeart)
        return __AllIcons!!
    }

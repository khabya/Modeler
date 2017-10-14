package com.cout970.modeler.gui.react.core

import com.cout970.vector.api.IVector2
import org.liquidengine.legui.system.context.Context

/**
 * Created by cout970 on 2017/09/23.
 */
data class RBuildContext(
        val parentSize: IVector2,
        val leguiCtx: Context
)
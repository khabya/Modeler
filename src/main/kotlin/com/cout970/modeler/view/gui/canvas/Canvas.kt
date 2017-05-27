package com.cout970.modeler.view.gui.canvas

import com.cout970.modeler.view.gui.comp.CBorderRenderer
import com.cout970.modeler.view.gui.comp.CPanel
import org.liquidengine.legui.util.ColorConstants

/**
 * Created by cout970 on 2017/05/02.
 */

class Canvas : CPanel() {

    val state = CanvasState()

    init {
        backgroundColor = ColorConstants.transparent()
        border.renderer = CBorderRenderer
    }
}
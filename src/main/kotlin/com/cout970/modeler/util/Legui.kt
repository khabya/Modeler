package com.cout970.modeler.util

import com.cout970.modeler.view.controller.ButtonController
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.plus
import org.joml.Vector2f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Frame
import org.liquidengine.legui.event.component.MouseClickEvent

val Component.absolutePosition: IVector2 get() {
    var sum = this.position.toIVector()
    var parent = this.parent
    while (parent != null) {
        sum += parent.position.toIVector()
        parent = parent.parent
    }
    return sum
}

fun <T : Component> T.onClick(id: String, buttonController: ButtonController): T {
    leguiEventListeners.addListener(MouseClickEvent::class.java, {
        if (it.action == MouseClickEvent.MouseClickAction.PRESS) {
            buttonController.onClick(id)
        }
    })
    return this
}

inline fun <T : Component> T.onClick(id: Int, crossinline func: (Int) -> Unit): T {
    leguiEventListeners.addListener(MouseClickEvent::class.java, {
        if (it.action == MouseClickEvent.MouseClickAction.PRESS) {
            func(id)
        }
    })
    return this
}

var Frame.size: Vector2f
    get() = componentLayer.container.size
    set(value) {
        allLayers.forEach {
            it.container.size = value
        }
        componentLayer.container.size = value
    }
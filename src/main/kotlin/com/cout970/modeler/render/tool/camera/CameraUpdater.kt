package com.cout970.modeler.render.tool.camera

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.event.EventMouseScroll
import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.helpers.PickupHelper
import com.cout970.modeler.gui.canvas.Canvas
import com.cout970.modeler.gui.canvas.CanvasContainer
import com.cout970.modeler.input.event.IInput
import com.cout970.modeler.util.absolutePositionV
import com.cout970.modeler.util.isInside
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toRads
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 2017/05/02.
 */

class CameraUpdater(
        val canvasContainer: CanvasContainer,
        val input: IInput,
        val timer: Timer,
        val isBlocked: () -> Boolean
) {

    private var selectedCanvas: Canvas? = null

    fun updateCameras() {
        canvasContainer.canvas.forEach { canvas ->
            canvas.cameraHandler.updateAnimation(timer)
        }

        updateSelectedCanvas()

        if (!isBlocked()) {
            selectedCanvas?.let { moveCamera(it) }
        }
    }

    private fun updateSelectedCanvas() {
        if (Config.keyBindings.moveCamera.check(input) ||
                Config.keyBindings.rotateCamera.check(input)) {

            if (selectedCanvas == null) {
                val mousePos = input.mouse.getMousePos()
                val hover = canvasContainer.canvas.indexOfFirst { canvas ->
                    mousePos.isInside(canvas.absolutePositionV, canvas.size.toIVector())
                }
                selectedCanvas = if (hover == -1) null else canvasContainer.canvas[hover]
            }
        } else {
            selectedCanvas = null
        }
    }

    private fun moveCamera(selectedScene: Canvas) {

        val move = Config.keyBindings.moveCamera.check(input) ||
                (input.keyboard.isKeyPressed(Keyboard.KEY_SPACE) && input.mouse.isButtonPressed(1))

        val rotate = Config.keyBindings.rotateCamera.check(input)

        if (!move && !rotate) return

        val speed = 1 / (600 * timer.delta) *
                if (Config.keyBindings.slowCameraMovements.check(input)) 1 / 10f else 1f

        if (selectedScene.viewMode == SelectionTarget.MODEL) {
            if (move) {
                moveModelCamera(selectedScene, 1 / 60.0)
            } else if (rotate) {
                rotateModelCamera(selectedScene, speed)
            }
        } else {
            moveTextureCamera(selectedScene)
        }
    }

    private fun rotateModelCamera(selectedScene: Canvas, speed: Double) {
        val diff = input.mouse.getMousePosDiff()
        selectedScene.cameraHandler.rotate(
                diff.yd * Config.mouseRotationSpeedY * speed,
                diff.xd * Config.mouseRotationSpeedX * speed
        )
    }

    private fun moveModelCamera(selectedScene: Canvas, speed: Double) {
        val camera = selectedScene.modelCamera.camera
        val rotations = vec2Of(camera.angleY, camera.angleX).toDegrees()
        val axisX = vec2Of(Math.cos(rotations.x.toRads()), Math.sin(rotations.x.toRads()))
        var axisY = vec2Of(Math.cos((rotations.xd - 90).toRads()), Math.sin((rotations.xd - 90).toRads()))
        axisY *= Math.sin(rotations.y.toRads())
        var a = vec3Of(axisX.x, 0.0, axisX.y)
        var b = vec3Of(axisY.x, Math.cos(rotations.y.toRads()), axisY.y)
        val diff = input.mouse.getMousePosDiff()

        a = a.normalize() * (diff.xd * Config.mouseTranslateSpeedX * speed * Math.sqrt(camera.zoom))
        b = b.normalize() * (-diff.yd * Config.mouseTranslateSpeedY * speed * Math.sqrt(camera.zoom))

        selectedScene.modelCamera.translate(a + b)
    }

    fun moveTextureCamera(selectedScene: Canvas) {
        val prev = input.mouse.getMousePos() - input.mouse.getMousePosDiff()
        val current = input.mouse.getMousePos()

        val start = PickupHelper.getMousePosAbsolute(selectedScene, prev)
        val end = PickupHelper.getMousePosAbsolute(selectedScene, current)

        val diff = end - start
        selectedScene.textureCamera.translate(vec3Of(diff.xd, diff.yd, 0))
    }

    fun updateZoom(canvas: Canvas, e: EventMouseScroll) {
        canvas.run {
            val camera = cameraHandler.camera
            val scroll = -e.offsetY * Config.cameraScrollSpeed
            if (camera.zoom > 0.5 || scroll > 0) {
                cameraHandler.setZoom(cameraHandler.desiredZoom + scroll * (cameraHandler.desiredZoom / 50f))
            }
        }
    }
}
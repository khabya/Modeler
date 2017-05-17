package com.cout970.modeler.view.render

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.structure.Timer
import com.cout970.glutilities.window.GLFWWindow
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.util.ITickeable
import com.cout970.modeler.view.gui.GuiUpdater
import com.cout970.modeler.view.window.WindowHandler
import com.cout970.vector.extensions.xf
import com.cout970.vector.extensions.yf
import com.cout970.vector.extensions.zf
import java.awt.Color

/**
 * Created by cout970 on 2016/11/29.
 */
class RenderManager : ITickeable {

    // @Injected
    lateinit var guiUpdater: GuiUpdater

    lateinit var windowHandler: WindowHandler
    lateinit var window: GLFWWindow
    lateinit var timer: Timer

    lateinit var guiRenderer: GuiRenderer
    lateinit var shaderHandler: ShaderHandler
    lateinit var canvasRenderer: CanvasRenderer

    fun initOpenGl(resourceLoader: ResourceLoader, windowHandler: WindowHandler) {
        this.windowHandler = windowHandler
        this.window = windowHandler.window
        this.timer = windowHandler.timer
        log(Level.FINE) { "[RenderManager] Creating GuiRenderer" }
        guiRenderer = GuiRenderer(guiUpdater.root, window.id)
        log(Level.FINE) { "[RenderManager] Creating ShaderHandler" }
        shaderHandler = ShaderHandler(resourceLoader)
        log(Level.FINE) { "[RenderManager] Creating CanvasRenderer" }
        canvasRenderer = CanvasRenderer(this)
        val c = Config.colorPalette.modelBackgroundColor
        GLStateMachine.clearColor = Color(c.xf, c.yf, c.zf)
    }

    override fun preTick() {
        guiRenderer.updateEvents()
    }

    override fun tick() {
        GLStateMachine.clear()
        canvasRenderer.render(guiUpdater)
        guiRenderer.render(guiUpdater.root)
    }
}
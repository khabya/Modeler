package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.ResourceManager
import com.cout970.modeler.log.print
import com.cout970.modeler.model.Model
import com.cout970.modeler.modeleditor.ModelController

/**
 * Created by cout970 on 2017/01/02.
 */
class ActionImportModel(val modelController: ModelController,
                        val resourceManager: ResourceManager,
                        val path: String,
                        val function: () -> Model) : IAction {

    val oldModel = modelController.model

    override fun run() {
        try {
            val newModel = function()
            modelController.selectionManager.clearSelection()
            newModel.groups.map { it.material }.distinct().forEach {
                it.loadTexture(resourceManager)
            }
            modelController.updateModel(newModel)
        } catch(e: Exception) {
            e.print()
        }
    }

    override fun undo() {
        modelController.updateModel(oldModel)
    }

    override fun toString(): String {
        return "ActionImportModel(path='$path')"
    }
}
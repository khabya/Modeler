package com.cout970.modeler.modelcontrol

import com.cout970.modeler.model.*
import com.cout970.modeler.util.replaceWithIndex

/**
 * Created by cout970 on 2016/12/09.
 */
class ModelInserter(val modelController: ModelController) {

    var groupCount = 0
    var objCount = 0

    var objectIndex = -1
        private set
    var groupIndex = -1
        private set

    fun insertComponent(comp: Mesh) {
        if (groupIndex == -1) {
            insertGroup()
            groupIndex = 0
        }
        modelController.apply {
            updateModel(model.copy(model.objects.replaceWithIndex({ i, obj -> i == objectIndex }, { i, obj ->
                obj.copy(obj.groups.replaceWithIndex({ i, group -> i == groupIndex }, { i, group ->
                    group.add(comp)
                }))
            })))
        }
    }

    fun insertGroup(group: ModelGroup = ModelGroup(listOf(), Transformation.IDENTITY, "Group${groupCount++}")) {
        if (objectIndex == -1) {
            insertObject()
            objectIndex = 0
        }
        modelController.apply {
            updateModel(model.copy(model.objects.replaceWithIndex({ i, obj -> i == objectIndex }, { i, obj ->
                obj.add(group)
            })))
        }
    }

    fun insertObject(obj: ModelObject = ModelObject(listOf(), Transformation.IDENTITY, "Object${objCount++}", Material.MaterialNone)) {
        modelController.apply {
            updateModel(model.add(obj))
        }
    }
}
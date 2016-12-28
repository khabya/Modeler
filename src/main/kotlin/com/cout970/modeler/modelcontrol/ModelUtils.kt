package com.cout970.modeler.modelcontrol

import com.cout970.modeler.model.Model
import com.cout970.modeler.model.Quad
import com.cout970.modeler.modelcontrol.selection.ModelPath
import com.cout970.modeler.modelcontrol.selection.Selection
import com.cout970.modeler.modelcontrol.selection.SelectionMode
import com.cout970.modeler.util.replace
import com.cout970.modeler.util.replaceWithIndex
import com.cout970.modeler.view.controller.SelectionAxis
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.times

/**
 * Created by cout970 on 2016/12/17.
 */

fun Model.translate(selection: Selection, axis: SelectionAxis, offset: Float): Model {
    return when (selection.mode) {

        SelectionMode.GROUP -> {
            copy(objects = objects.replace({ obj -> obj.groups.any { selection.isSelected(ModelPath.of(this, obj, it)) } }, { obj ->
                obj.copy(groups = obj.groups.replace({ selection.isSelected(ModelPath.of(this, obj, it)) }, { group ->
                    group.copy(transform = group.transform.move(axis, offset))
                }))
            }))
        }

        SelectionMode.COMPONENT -> {
            copy(objects = objects.replace({ obj ->
                obj.groups.any { group ->
                    group.meshes.any { selection.isSelected(ModelPath.of(this, obj, group, it)) }
                }
            }, { obj ->
                obj.copy(groups = obj.groups.replace({ group ->
                    group.meshes.any { selection.isSelected(ModelPath.of(this, obj, group, it)) }
                }, { group ->
                    group.copy(meshes = group.meshes.replace({ selection.isSelected(ModelPath.of(this, obj, group, it)) }, { comp ->
                        comp.copy(transform = comp.transform.move(axis, offset))
                    }))
                }))
            }))
        }
        SelectionMode.QUAD -> {
            copy(objects = objects.replace({ obj ->
                obj.groups.any { group ->
                    group.meshes.any { selection.containsSelectedElements(ModelPath.of(this, obj, group, it)) }
                }
            }, { obj ->
                obj.copy(groups = obj.groups.replace({ group ->
                    group.meshes.any { selection.containsSelectedElements(ModelPath.of(this, obj, group, it)) }
                }, { group ->
                    group.copy(meshes = group.meshes.replace({ selection.containsSelectedElements(ModelPath.of(this, obj, group, it)) }, { comp ->
                        val selectedQuadsIndex = selection.paths.filter { it.getMesh(this) == comp }.map { it.quad }
                        val selectedPositions = comp.indices.filterIndexed { i, quadIndices -> i in selectedQuadsIndex }.map { it.toQuad(comp.positions, comp.textures) }.flatMap(Quad::vertex).map { it.pos }.distinct()

                        comp.copy(positions = comp.positions.replace({ pos -> pos in selectedPositions }, { pos ->
                            pos + axis.axis * offset
                        }))
                    }))
                }))
            }))
        }

        SelectionMode.VERTEX -> {
            copy(objects = objects.replace({ obj ->
                obj.groups.any { group ->
                    group.meshes.any { selection.containsSelectedElements(ModelPath.of(this, obj, group, it)) }
                }
            }, { obj ->
                obj.copy(groups = obj.groups.replace({ group ->
                    group.meshes.any { selection.containsSelectedElements(ModelPath.of(this, obj, group, it)) }
                }, { group ->
                    group.copy(meshes = group.meshes.replace({ selection.containsSelectedElements(ModelPath.of(this, obj, group, it)) }, { comp ->

                        val pathToThisComponent = selection.paths.filter { it.getMesh(this) == comp }
                        val selectedIndices = pathToThisComponent.map { it.vertex }

                        comp.copy(positions = comp.positions.replaceWithIndex({ i, pos -> i in selectedIndices }, { i, pos ->
                            pos + axis.axis * offset
                        }))
                    }))
                }))
            }))
        }
        else -> this
    }
}
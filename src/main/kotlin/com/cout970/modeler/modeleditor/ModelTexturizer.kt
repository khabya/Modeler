package com.cout970.modeler.modeleditor

import com.cout970.modeler.modeleditor.action.ActionModifyModel
import com.cout970.modeler.selection.VertexTexSelection

/**
 * Created by cout970 on 2017/02/11.
 */
class ModelTexturizer(val editor: ModelEditor) {

    fun splitTextures() {
        if (editor.selectionManager.vertexTexSelection != VertexTexSelection.EMPTY) {
            val newModel = editor.model.splitUV(editor.selectionManager.vertexTexSelection)
            editor.historyRecord.doAction(ActionModifyModel(editor, newModel))
        }
    }
}
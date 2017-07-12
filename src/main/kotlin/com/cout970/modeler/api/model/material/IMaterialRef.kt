package com.cout970.modeler.api.model.material

/**
 * Created by cout970 on 2017/07/09.
 */
interface IMaterialRef {
    val materialIndex: Int

    operator fun compareTo(materialRef: IMaterialRef): Int {
        return materialIndex.compareTo(materialRef.materialIndex)
    }
}
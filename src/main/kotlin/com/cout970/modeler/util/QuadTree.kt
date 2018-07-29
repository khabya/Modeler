package com.cout970.modeler.util

/**
 * Created by cout970 on 2017/09/09.
 */

class QuadTree<T>(val depth: Int) {

    private var root: Node<T>? = null
    private var actual = false


    fun get(x: Int, y: Int): T? {
        var dep = depth
        var iter = root
        while (true) {
            if (iter == null) return null
            else if (iter is Node.Leaf) return iter.value

            iter = (iter as Node.Branch<T>).nodes[number(x, y, --dep)]
        }
    }


    fun set(x: Int, y: Int, value: T) {
        if (root == null) root = Node.Branch()
        if (root!!.set(x, y, value, depth - 1)) {
            root = Node.Leaf(value)
        }
        actual = false
    }

    override fun toString(): String = root.toString()

    sealed class Node<T> {

        abstract fun set(x: Int, y: Int, value: T, depth: Int): Boolean


        class Leaf<T>(var value: T) : Node<T>() {

            override fun set(x: Int, y: Int, value: T, depth: Int): Boolean {
                throw UnsupportedOperationException("set on Leaf element")
            }

            override fun toString(): String = "L{$value}"
        }


        class Branch<T>() : Node<T>() {

            constructor(value: T, exclude: Int) : this() {

                var i = 0
                while (i < 4) {
                    if (i != exclude) {
                        nodes[i] = Leaf(value)
                    }
                    i++
                }
            }

            private fun canClusterize(value: T): Boolean {
                var i = 0
                while (i < 4) {
                    val w = nodes[i]
                    if (w == null || w !is Leaf || value != w.value) {
                        return false
                    }
                    i++
                }
                return true
            }

            override fun set(x: Int, y: Int, value: T, depth: Int): Boolean {
                val branchIndex = number(x, y, depth)
                val node = nodes[branchIndex]
                when (node) {
                    null -> {
                        if (depth == 0) {
                            nodes[branchIndex] = Leaf(value)
                            return canClusterize(value)
                        } else {
                            nodes[branchIndex] = Branch()
                        }
                    }
                    is Leaf<T> -> {
                        if (node.value == value) {
                            return false
                        } else if (depth == 0) {
                            node.value = value
                            return canClusterize(value)
                        }
                        nodes[branchIndex] = Branch(node.value, number(x, y, depth - 1))
                    }
                    else -> Unit
                }

                if (nodes[branchIndex]!!.set(x, y, value, depth - 1)) {
                    nodes[branchIndex] = Leaf(value)
                    return canClusterize(value)
                }
                return false
            }

            val nodes = arrayOfNulls<Node<T>>(4)
            override fun toString(): String = nodes.joinToString(prefix = "[", postfix = "]")
        }
    }

    companion object {
        fun number(x: Int, y: Int, depth: Int): Int {
            val mask = 1 shl depth

            if (x and mask != 0) {
                if (y and mask != 0) {
                    return 3
                }
                return 2
            }
            if (y and mask != 0) {
                return 1
            }
            return 0
        }
    }
}
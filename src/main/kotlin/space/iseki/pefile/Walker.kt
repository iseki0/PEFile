@file:JvmName(" Walker")

package space.iseki.pefile

fun ResourceNode.walk(): Sequence<ResourceWalker.Entry> = sequence {
    yieldAll(ResourceWalker(this@walk))
}

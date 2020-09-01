package org.jglrxavpok.mcclient

import javax.swing.JButton
import javax.swing.JFrame

fun main() {
    val frame = JFrame("Waiting for Renderdoc...")
    frame.add(JButton("Confirm that Renderdoc has been attached").apply {
        addActionListener {
            frame.dispose()
            Client.main(emptyArray())
        }
    })
    frame.pack()
    frame.setLocationRelativeTo(null)
    frame.isVisible = true
}
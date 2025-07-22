package com.dideng.mptt.morepycharmtkintertab

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.util.ProcessingContext
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.jetbrains.python.psi.PyStringLiteralExpression

class TkinterCompletionProvider : CompletionProvider<CompletionParameters>() {

    // 1. 🚪 窗口管理协议 (Window Manager Protocols)
    private val WINDOW_PROTOCOLS = listOf(
        "WM_DELETE_WINDOW",
        "WM_TAKE_FOCUS",
        "WM_SAVE_YOURSELF",
        "WM_PROTOCOL_MESSAGE"
    )

    // 2. 🖱️ 拖放支持（Xdnd）
    private val XDND_EVENTS = listOf(
        "XdndEnter", "XdndPosition", "XdndDrop", "XdndStatus",
        "XdndLeave", "XdndFinished", "XdndSelection"
    )
    private val XDND_ACTIONS = listOf(
        "XdndActionCopy", "XdndActionMove", "XdndActionLink", "XdndActionAsk"
    )

    // 3. 📋 剪贴板
    private val CLIPBOARD = listOf("CLIPBOARD", "PRIMARY", "TARGETS")

    // 4. 🖱️ 鼠标光标
    private val CURSORS = listOf(
        "arrow", "hand2", "xterm", "crosshair", "watch",
        "size_nw_se", "size_ne_sw", "sb_h_double_arrow", "sb_v_double_arrow"
    )

    // 6. ⚙️ 控件状态
    private val STATES = listOf("normal", "disabled", "readonly")

    // 7. 🧭 锚点位置
    private val ANCHORS = listOf("center", "n", "s", "e", "w", "ne", "nw", "se", "sw")

    // 8. 📐 布局与填充
    private val EXPAND = listOf("both", "x", "y")
    private val FILL = listOf("x", "y", "both", "none")
    private val SIDE = listOf("top", "bottom", "left", "right")

    // 9. 🧱 边框样式
    private val RELIEF = listOf("raised", "sunken", "flat", "groove", "ridge", "solid")

    // 10. 🔤 其他常用值
    private val BOOL_YESNO = listOf("yes", "no", "1", "0", "default")

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = parameters.position
        val parent = position.parent as? PyStringLiteralExpression ?: return

        when {
            // 场景1: root.protocol(...)
            isInsideProtocolCall(position) -> {
                addItems(result, WINDOW_PROTOCOLS, " (窗口协议)")
            }
            // 场景2: widget.config(cursor="...")
            isCursorProperty(parent) -> {
                addItems(result, CURSORS, " (鼠标样式)")
            }
            // 场景3: widget.config(state="...")
            isStateProperty(parent) -> {
                addItems(result, STATES, " (控制状态)")
            }
            // 场景4: pack(side="...") / grid(sticky="...")
            isInLayoutContext(parent) -> {
                addItems(result, SIDE + ANCHORS + FILL + EXPAND, " (布局选项)")
            }
            // 场景5: bind("<...>")
            isInEventBindingContext(parent) -> {
                addItems(result, XDND_EVENTS + XDND_ACTIONS + CLIPBOARD, " (事件/剪贴板)")
            }
            // 场景6: 通用情况
            else -> {
                val allItems = (WINDOW_PROTOCOLS + CURSORS + STATES + ANCHORS + FILL + EXPAND + SIDE + RELIEF + BOOL_YESNO).distinct().sorted()
                addItems(result, allItems, " (Tkinter 字符串)")
            }
        }
    }

    private fun isInsideProtocolCall(element: PsiElement): Boolean {
        return element.text.contains(".protocol(\"") || element.text.contains(".protocol('")
    }

    private fun isCursorProperty(literal: PyStringLiteralExpression): Boolean {
        val text = literal.text.lowercase()
        return text.contains("cursor=\"") || text.contains("cursor='")
    }

    private fun isStateProperty(literal: PyStringLiteralExpression): Boolean {
        val text = literal.text.lowercase()
        return text.contains("state=\"") || text.contains("state='")
    }

    private fun isInLayoutContext(literal: PyStringLiteralExpression): Boolean {
        val text = literal.text.lowercase()
        return text.contains("side=\"") || text.contains("side='") ||
                text.contains("fill=\"") || text.contains("fill='") ||
                text.contains("expand=\"") || text.contains("expand='") ||
                text.contains("sticky=\"") || text.contains("sticky='")
    }

    private fun isInEventBindingContext(literal: PyStringLiteralExpression): Boolean {
        val text = literal.text
        return text.startsWith("\"<") || text.startsWith("'<'")
    }

    private fun addItems(result: CompletionResultSet, items: List<String>, tailText: String) {
        for (item in items) {
            result.addElement(
                LookupElementBuilder.create(item)
                    .bold()
                    .withTailText(tailText, true)
            )
        }
    }
}